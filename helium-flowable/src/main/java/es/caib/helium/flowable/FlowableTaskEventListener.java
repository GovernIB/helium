package es.caib.helium.flowable;

import java.util.function.Supplier;

import es.caib.helium.bpmn.engine.WTaskInstance;
import es.caib.helium.logic.intf.bpmn.MotorHeliumService;
import es.caib.helium.logic.intf.bpmn.MotorHeliumService.EstatTasca;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.*;
import org.flowable.common.engine.impl.cfg.TransactionState;
import org.flowable.common.engine.impl.interceptor.CommandConfig;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

@Slf4j
@RequiredArgsConstructor
public class FlowableTaskEventListener implements FlowableEventListener {
	private final ProcessEngineConfiguration processEngineConfiguration;
	/** Serveis de Helium. Es resolen quan arriba un event: Helium també depèn del motor. */
	private final Supplier<MotorHeliumService> motorHeliumService;

	@Override
	public void onEvent(FlowableEvent event) {
		try {
			if(event instanceof FlowableEntityEvent) {
				processEntityEvent((FlowableEntityEvent) event);
			}
		} catch(Exception ex) {
			log.error("Error inesperat processant event de tasca", ex);
		}
	}

	private void processEntityEvent(FlowableEntityEvent event) {
		Object entity = event.getEntity();
		String taskId;
		if(entity instanceof TaskEntity) {
			taskId = ((TaskEntity)entity).getId();
		} else if(entity instanceof IdentityLinkEntity) {
			IdentityLinkEntity identityLinkEntity = (IdentityLinkEntity)entity;
			taskId = identityLinkEntity.getTaskId();
			if(taskId == null)
				return;
		} else {
			return;
		}

		FlowableEngineEventType eventType = (FlowableEngineEventType) event.getType();
		// L'event arriba després del commit (getOnTransaction). Amb JTA (JBoss) la transacció confirmada continua
		// associada al fil i qualsevol consulta hi falla (IJ031070 STATUS_COMMITTED): cal una transacció nova.
		processEngineConfiguration.getManagementService().executeCommand(new CommandConfig().transactionRequiresNew(), commandContext -> {
			try {
				TaskEntity currentTask = (TaskEntity) processEngineConfiguration
					.getTaskService()
					.createTaskQuery()
					.includeIdentityLinks()
					.taskId(taskId)
					.singleResult();
				if(currentTask == null)
					return null;

				boolean taskCreated = false;

				EstatTasca estat = null;
				switch (eventType) {
					case ENTITY_UPDATED:
						if (currentTask.isCanceled())
							estat = EstatTasca.CANCELADA;
						else
							estat = EstatTasca.INICIADA;
						break;
					case ENTITY_SUSPENDED:
					case ENTITY_DELETED:
						estat = EstatTasca.CANCELADA;
						break;
					case ENTITY_CREATED:
					case TASK_CREATED:
						taskCreated = true;
						estat = EstatTasca.PENDENT;
						break;
					case TASK_ASSIGNED:
						if (currentTask.getAssignee() != null) {
							estat = EstatTasca.INICIADA;
						} else {
							estat = EstatTasca.PENDENT;
						}
						break;
					case TASK_COMPLETED:
						estat = EstatTasca.FINALITZADA;
						break;
					default:
						return null;
				}

				currentTask.getIdentityLinks();

				if (currentTask.isCanceled() || currentTask.isSuspended()) {
					estat = EstatTasca.CANCELADA;
				}

				if (taskCreated && currentTask.getCandidates() != null && currentTask.getCandidates().size() == 1) {
					IdentityLink il = currentTask.getCandidates().stream().findFirst().get();
					if (il.getUserId() != null)
						processEngineConfiguration
							.getTaskService()
							.setAssignee(currentTask.getId(), il.getUserId());
				}

				if (taskCreated)
					motorHeliumService.get().tascaCreada(taskId);

				WTaskInstance taskInstance = FlowableEngineImpl.toWTaskInstance(currentTask);
				motorHeliumService.get().tascaEstatModificat(taskInstance, estat);
			} catch (Exception ex) {
				log.error("No s'ha pogut processar event de la tasca = {}", taskId, ex);
			}
			return null;
		});
	}

	@Override
	public boolean isFailOnException() {
		// Si el logging falla, no volem que faci fallar el procés
		return false;
	}

	@Override
	public boolean isFireOnTransactionLifecycleEvent() {
		return true; // without this, getOnTransaction() is ignored
	}

	@Override
	public String getOnTransaction() {
		return TransactionState.COMMITTED.name(); // fires only after a successful commit
	}

}
