package es.caib.helium.logic.flowable;

import es.caib.comanda.model.management.TascaEstat;
import es.caib.helium.logic.helper.ComandaHelper;
import es.caib.helium.logic.helper.TascaHelper;
import es.caib.helium.disseny.engine.WTaskInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.AbstractFlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

@Slf4j
@RequiredArgsConstructor
public class FlowableTaskEventListener extends AbstractFlowableEventListener {
	private final ProcessEngineConfiguration processEngineConfiguration;
	private final ComandaHelper comandaHelper;
	private final TascaHelper tascaHelper;

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
		TaskEntity currentTask;
		String taskId;
		if(entity instanceof TaskEntity) {
			currentTask = (TaskEntity)entity;
			taskId = currentTask.getId();
		} else if(entity instanceof IdentityLinkEntity) {
			IdentityLinkEntity identityLinkEntity = (IdentityLinkEntity)entity;
			taskId = identityLinkEntity.getTaskId();
			if(taskId == null)
				return;
			currentTask = (TaskEntity) processEngineConfiguration
							.getTaskService()
							.createTaskQuery()
							.includeIdentityLinks()
							.taskId(taskId)
							.singleResult();
		} else {
			//log.error("No s'ha pogut processar event de la tasca");
			return;
		}
		try {
			ProcessInstance pc = processEngineConfiguration
				.getRuntimeService()
				.createProcessInstanceQuery()
				.processInstanceId(currentTask.getProcessInstanceId())
				.includeProcessVariables()
				.singleResult();

			TascaEstat estat = null;
			switch ((FlowableEngineEventType) event.getType()) {
			case ENTITY_UPDATED:
				if(currentTask.isCanceled())
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
				estat = TascaEstat.PENDENT;
				break;
			case TASK_ASSIGNED:
				if(currentTask.getAssignee() != null) {
					estat = TascaEstat.INICIADA;
				} else {
					estat = TascaEstat.PENDENT;
				}
				break;
			case TASK_COMPLETED:
				estat = TascaEstat.FINALITZADA;
				break;
			default:
				return;
			}

			Object expedientNumeroObj = pc.getProcessVariables().get("__expedient_numero__");
			String expedientNumero = expedientNumeroObj != null? (String) expedientNumeroObj : "";

			if(currentTask.isCanceled() || currentTask.isSuspended()) {
				estat = TascaEstat.CANCELADA;
			}

			WTaskInstance taskInstance = FlowableEngineImpl.toWTaskInstance(currentTask);
			comandaHelper.upsertTasca(
				taskId,
				currentTask.getName(),
				expedientNumero,
				null,
				taskInstance,
				estat
			);

			// tascaHelper.refreshExpedientTasca(taskId);

		} catch(Exception ex) {
			log.error("No s'ha pogut processar event de la tasca = {}", taskId, ex);
		}
	}

	@Override
	public boolean isFailOnException() {
		// Si el logging falla, no volem que faci fallar el procés
		return false;
	}
}
