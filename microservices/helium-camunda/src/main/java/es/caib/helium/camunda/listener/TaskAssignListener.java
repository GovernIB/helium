package es.caib.helium.camunda.listener;

import java.util.List;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonPatchBuilder;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.camunda.helper.EngineHelper;
import es.caib.helium.client.expedient.tasca.TascaClientService;
import es.caib.helium.client.helper.PatchHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskAssignListener implements TaskListener {

    private final TascaClientService tascaClientService;

    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Expedients i Tasques
        log.info("Tasca " + delegateTask.getName() + "has been assigned to " + delegateTask.getAssignee());

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

        JsonPatchBuilder jpb = Json.createPatchBuilder();

        // Agafada
        if (tascaOriginal.getAssignee() == null && delegateTask.getAssignee() != null && delegateTask.getCandidates() != null && !delegateTask.getCandidates().isEmpty()) {
            PatchHelper.replaceBooleanProperty(jpb, "agafada", true);
        }
        PatchHelper.replaceBooleanProperty(jpb, "assignada", tascaOriginal.getAssignee() != null, delegateTask.getAssignee() != null);
        // UsuariAssignat
        PatchHelper.replaceStringProperty(jpb, "usuariAssignat", tascaOriginal.getAssignee(), delegateTask.getAssignee());
        // Responsables
        PatchHelper.replaceArrayProperty(jpb, "responsables", originalUsuarisCandidats, usuarisCandidats);
        // GrupsAssignat

        tascaClientService.patchTascaV1(
                delegateTask.getId(),
                new ObjectMapper().valueToTree(jpb.build()));
    }
}
