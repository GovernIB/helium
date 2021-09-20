package es.caib.helium.camunda.listener;

import es.caib.helium.camunda.helper.EngineHelper;
import es.caib.helium.camunda.helper.ThreadLocalInfo;
import es.caib.helium.camunda.service.bridge.WorkflowBridgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskAssignListener implements TaskListener {

//    private final TascaClientService tascaClientService;
    private final WorkflowBridgeService workflowBridgeService;

    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Expedients i Tasques
        log.info("Tasca " + delegateTask.getName() + "has been assigned to " + delegateTask.getAssignee());

        TaskEntity task = (TaskEntity) delegateTask;

        if (!task.getPropertyChanges().isEmpty()) {

            // Si es modifica l'usuari assignat, hem de comprovar que no hi hagi una redirecció definida
            if (task.getPropertyChanges().get("assignee") != null) {
                var previousActorId = task.getPropertyChanges().get("assignee").getOrgValueString();
                var currentActorId = task.getPropertyChanges().get("assignee").getNewValueString();

                if (currentActorId != null && !currentActorId.isBlank()) {
                    // Si s'està iniciant l'expedient no es fa reassignació
                    if (ThreadLocalInfo.getStartExpedient() == null || !ThreadLocalInfo.getStartExpedient()) {
                        var reassignacio = workflowBridgeService.findReassignacioActivaPerUsuariOrigen(
                                delegateTask.getProcessInstanceId(),
                                currentActorId);
                        if (reassignacio != null) {
                            EngineHelper.getInstance().getTaskService().setAssignee(task.getId(), reassignacio.getUsuariDesti());
                        }
                    }
                }
            }

        }
    }
}
