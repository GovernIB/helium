package es.caib.helium.camunda.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.helium.camunda.helper.EngineHelper;
import es.caib.helium.client.expedient.tasca.TascaClientService;
import es.caib.helium.client.helper.PatchHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonPatchBuilder;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskUpdateListener implements TaskListener {

    private final TascaClientService tascaClientService;

    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Expedients i Tasques
        log.info("Tasca " + delegateTask.getName() + "has been updated");

//        var tasca = EngineHelper.getInstance().getTaskService().createTaskQuery()
//                .taskId(delegateTask.getId())
//                .singleResult();
        var tascaOriginal = EngineHelper.getInstance().getHistoryService()
                .createHistoricTaskInstanceQuery()
                .taskId(delegateTask.getId())
                .singleResult();
        var originalCandidates = EngineHelper.getInstance().getTaskService().getIdentityLinksForTask(delegateTask.getId());

        List<String> originalUsuarisCandidats =  originalCandidates.stream()
                .filter(c -> c.getUserId() != null)
                .map(c -> c.getUserId())
                .collect(Collectors.toList());
        List<String> originalGrupsCandidats = originalCandidates.stream()
                .filter(c -> c.getGroupId() != null)
                .map(c -> c.getGroupId())
                .collect(Collectors.toList());

        List<String> usuarisCandidats =  delegateTask.getCandidates().stream()
                .filter(c -> c.getUserId() != null)
                .map(c -> c.getUserId())
                .collect(Collectors.toList());
        List<String> grupsCandidats = delegateTask.getCandidates().stream()
                .filter(c -> c.getGroupId() != null)
                .map(c -> c.getGroupId())
                .collect(Collectors.toList());

//        boolean tascaAssignada = delegateTask.getAssignee() != null && !delegateTask.getAssignee().isBlank();
//        TascaDto tascaDto = TascaDto.builder()
//                .id(delegateTask.getId())
////                .expedientId()
//                .procesId(delegateTask.getProcessInstanceId())
//                .nom(delegateTask.getName())
//                .titol(delegateTask.getDescription() != null ? delegateTask.getDescription() : delegateTask.getName())
////                .afagada(tascaAssignada)
//                .agafada(delegateTask.getAssignee() != null)
////                .cancelada(false)
//                .suspesa(false)
//                .completada(false)
//                .assignada(tascaAssignada)
////                .marcadaFinalitzar()
////                .errorFinalitzacio()
//                .dataFins(delegateTask.getDueDate())
////                .dataFi(null)
////                .iniciFinalitzacio()
//                .dataCreacio(delegateTask.getCreateTime())
//                .usuariAssignat(delegateTask.getAssignee())
//                .grupsAssignat(grupsCandidats)
//                .prioritat(delegateTask.getPriority())
//                .responsables(usuarisCandidats)
//                .processDefinitionId(delegateTask.getProcessDefinitionId())
//                .build();

        boolean hasChanged = false;
        JsonPatchBuilder jpb = Json.createPatchBuilder();

        // Nom
        hasChanged |= PatchHelper.replaceStringProperty(jpb, "nom", tascaOriginal.getName(), delegateTask.getName());
        // Titol
        String titolOriginal = tascaOriginal.getDescription() != null ? tascaOriginal.getDescription() : tascaOriginal.getName();
        String titol = delegateTask.getDescription() != null ? delegateTask.getDescription() : delegateTask.getName();
        hasChanged |= PatchHelper.replaceStringProperty(jpb, "titol", titolOriginal, titol);
        // Agafada
        if (tascaOriginal.getAssignee() == null && delegateTask.getAssignee() != null && delegateTask.getCandidates() != null && !delegateTask.getCandidates().isEmpty()) {
            hasChanged |= PatchHelper.replaceBooleanProperty(jpb, "agafada", true);
        }
        // Suspesa --> No es pot suspendre una tasca. Es suspen el procés complet

        // Assignada
        hasChanged |= PatchHelper.replaceBooleanProperty(jpb, "assignada", tascaOriginal.getAssignee() != null, delegateTask.getAssignee() != null);
        // DataFins
        hasChanged |= PatchHelper.replaceDateProperty(jpb, "dataFins", tascaOriginal.getDueDate(), delegateTask.getDueDate());
        // UsuariAssignat
        hasChanged |= PatchHelper.replaceStringProperty(jpb, "usuariAssignat", tascaOriginal.getAssignee(), delegateTask.getAssignee());
        // Responsables
        hasChanged |= PatchHelper.replaceArrayProperty(jpb, "responsables", originalUsuarisCandidats, usuarisCandidats);
        // GrupsAssignat
        hasChanged |= PatchHelper.replaceArrayProperty(jpb, "grups", originalGrupsCandidats, grupsCandidats);
        // Prioritat
        hasChanged |= PatchHelper.replaceIntegerProperty(jpb, "prioritat", tascaOriginal.getPriority(), delegateTask.getPriority());

        if (hasChanged) {
            tascaClientService.patchTascaV1(
                    delegateTask.getId(),
                    PatchHelper.toJsonNode(jpb));
        }
    }

}