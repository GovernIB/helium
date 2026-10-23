package es.caib.helium.logic.flowable;

import es.caib.comanda.model.management.TascaEstat;
import es.caib.helium.logic.helper.ComandaHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.TascaHelper;
import es.caib.helium.disseny.engine.WTaskInstance;
import es.caib.helium.logic.intf.service.TascaService;
import es.caib.helium.persistence.entity.Expedient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.*;
import org.flowable.common.engine.impl.cfg.TransactionState;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import javax.transaction.Transactional;

@Slf4j
@RequiredArgsConstructor
public class FlowableTaskEventListener implements FlowableEventListener {
	private final ProcessEngineConfiguration processEngineConfiguration;
	private final ComandaHelper comandaHelper;
	private final TascaService tascaService;
	private final ExpedientHelper expedientHelper;

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
		processEngineConfiguration.getManagementService().executeCommand(commandContext -> {
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

				TascaEstat estat = null;
				switch (eventType) {
					case ENTITY_UPDATED:
						if (currentTask.isCanceled())
							estat = TascaEstat.CANCELADA;
						else
							estat = TascaEstat.INICIADA;
						break;
					case ENTITY_SUSPENDED:
					case ENTITY_DELETED:
						estat = TascaEstat.CANCELADA;
						break;
					case ENTITY_CREATED:
					case TASK_CREATED:
						taskCreated = true;
						estat = TascaEstat.PENDENT;
						break;
					case TASK_ASSIGNED:
						if (currentTask.getAssignee() != null) {
							estat = TascaEstat.INICIADA;
						} else {
							estat = TascaEstat.PENDENT;
						}
						break;
					case TASK_COMPLETED:
						estat = TascaEstat.FINALITZADA;
						break;
					default:
						return null;
				}

				currentTask.getIdentityLinks();

				Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(currentTask.getProcessInstanceId());
				String expedientNumero = expedient != null ? expedient.getNumero() : "";

				if (currentTask.isCanceled() || currentTask.isSuspended()) {
					estat = TascaEstat.CANCELADA;
				}

				if (taskCreated && currentTask.getCandidates() != null && currentTask.getCandidates().size() == 1) {
					IdentityLink il = currentTask.getCandidates().stream().findFirst().get();
					if (il.getUserId() != null)
						processEngineConfiguration
							.getTaskService()
							.setAssignee(currentTask.getId(), il.getUserId());
				}

				if (taskCreated)
					tascaService.refrescar(taskId);

				WTaskInstance taskInstance = FlowableEngineImpl.toWTaskInstance(currentTask);
				comandaHelper.upsertTasca(
					taskId,
					currentTask.getName(),
					expedientNumero,
					null,
					taskInstance,
					estat
				);
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
