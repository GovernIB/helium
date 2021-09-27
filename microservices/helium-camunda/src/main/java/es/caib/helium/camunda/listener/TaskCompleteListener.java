package es.caib.helium.camunda.listener;

import es.caib.helium.camunda.helper.EngineHelper;
import es.caib.helium.camunda.listener.events.TaskCompletEvent;
import es.caib.helium.client.dada.dades.DadaClient;
import es.caib.helium.client.dada.dades.model.Dada;
import es.caib.helium.client.engine.bridge.WorkflowBridgeClientService;
import es.caib.helium.client.engine.helper.DadaHelper;
import es.caib.helium.client.engine.model.CampTipus;
import es.caib.helium.client.expedient.tasca.TascaClientService;
import es.caib.helium.client.helper.PatchHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonPatchBuilder;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskCompleteListener implements TaskListener {

    private final TascaClientService tascaClientService;
    private final DadaClient dadaClient;
    private final WorkflowBridgeClientService workflowBridgeClientService;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Expedients i Tasques
        log.info("Tasca " + delegateTask.getName() + " has been completed");

        TaskEntity task = (TaskEntity) delegateTask;

        // Guardar dades de la tasca
        //   Obtenim informació de les variables de la tasca
        var campsTasca = workflowBridgeClientService.findCampsPerTaskInstance(
                task.getProcessInstanceId(),
                task.getProcessDefinitionId(),
                task.getName());

        //   Guardar les dades configurades com a writeTo i no lectura
        var campsWritable = campsTasca.stream()
                .filter(c -> c.isWriteTo() && !c.isReadOnly() && !CampTipus.ACCIO.equals(c.getCamp().getTipus()))
                .map(c -> c.getCamp())
                .collect(Collectors.toList());

        var variables = EngineHelper.getInstance().getTaskService().getVariablesLocal(task.getId());
        List<Dada> dades = DadaHelper.prepararDades(variables, campsWritable);
//        dadaClient.postDadaByProcesId(task.getProcessInstanceId(), dades);

        // Marcar tasca com a completada
        JsonPatchBuilder jpb = Json.createPatchBuilder();
        PatchHelper.replaceBooleanProperty(jpb, "completada", true);
//        tascaClientService.patchTascaV1(
//                delegateTask.getId(),
//                PatchHelper.toJsonNode(jpb)
//        );

        var taskCompletEvent = TaskCompletEvent.builder()
                .taskId(delegateTask.getId())
                .procesId(task.getProcessInstanceId())
                .dades(dades)
                .taskPatch(PatchHelper.toJsonNode(jpb))
                .build();
        applicationEventPublisher.publishEvent(taskCompletEvent);
    }

}
