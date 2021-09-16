package es.caib.helium.camunda.listener;

import es.caib.helium.camunda.helper.EngineHelper;
import es.caib.helium.client.expedient.tasca.TascaClientService;
import es.caib.helium.client.helper.PatchHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonPatchBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskUpdateListener implements TaskListener {

    private final TascaClientService tascaClientService;

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

    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Expedients i Tasques
        log.info("Tasca " + delegateTask.getName() + " has been updated");

        TaskEntity task = (TaskEntity) delegateTask;

        JsonPatchBuilder jpb = Json.createPatchBuilder();

        // Representants i grups
        List<String> originalUsuarisCandidats = new ArrayList<>();
        List<String> originalGrupsCandidats = new ArrayList<>();
        var historicCandidats = EngineHelper.getInstance().getHistoryService().createHistoricIdentityLinkLogQuery()
                .taskId(task.getId())
                .orderByTime().asc()
                .list();

        historicCandidats.stream()
                .filter(c -> c.getUserId() != null)
                .forEach(c -> {
                    if ("add".equals(c.getOperationType()))
                        originalUsuarisCandidats.add(c.getUserId());
                    else
                        originalUsuarisCandidats.remove(c.getUserId());
                });
        historicCandidats.stream()
                .filter(c -> c.getGroupId() != null)
                .forEach(c -> {
                    if ("add".equals(c.getOperationType()))
                        originalGrupsCandidats.add(c.getGroupId());
                    else
                        originalGrupsCandidats.remove(c.getGroupId());
                });

        List<String> usuarisCandidats =  delegateTask.getCandidates().stream()
                .filter(c -> c.getUserId() != null)
                .map(c -> c.getUserId())
                .collect(Collectors.toList());
        List<String> grupsCandidats = delegateTask.getCandidates().stream()
                .filter(c -> c.getGroupId() != null)
                .map(c -> c.getGroupId())
                .collect(Collectors.toList());

        if (!Objects.equals(originalUsuarisCandidats, usuarisCandidats)) {
            if (usuarisCandidats.isEmpty()) {
                tascaClientService.deleteResponsablesV1(task.getId());
            } else {
                tascaClientService.setResponsablesV1(task.getId(), usuarisCandidats);
            }
        }
        if (!Objects.equals(originalGrupsCandidats, grupsCandidats)) {
            if (grupsCandidats.isEmpty()) {
                tascaClientService.deleteGrupsV1(task.getId());
            } else {
                tascaClientService.setGrupsV1(task.getId(), grupsCandidats);
            }
        }

        if (!task.getPropertyChanges().isEmpty()) {
            var propertiesCahnged = task.getPropertyChanges();

            String originalName = task.getName();
            String originalDesc = task.getDescription();
            String finalName = task.getName();
            String finalDesc = task.getDescription();
            for (var entry: task.getPropertyChanges().entrySet()) {
                var propietatCanviada = entry.getValue();
                switch (entry.getKey()) {
                    case "caseInstanceId":
                        break;
                    case "name":
                        originalName = propietatCanviada.getOrgValueString();
                        finalName = propietatCanviada.getNewValueString();
                        PatchHelper.replaceStringProperty(jpb, "nom", originalName, finalName);
                        break;
                    case "description":
                        originalDesc = propietatCanviada.getOrgValueString();
                        finalDesc = propietatCanviada.getNewValueString();
//                        PatchHelper.replaceStringProperty(jpb, "titol", propietatCanviada.getOrgValueString(), propietatCanviada.getNewValueString());
                        break;
                    case "assignee":
                        PatchHelper.replaceStringProperty(jpb, "usuariAssignat", propietatCanviada.getOrgValueString(), propietatCanviada.getNewValueString());
                        PatchHelper.replaceBooleanProperty(jpb, "assignada", propietatCanviada.getNewValueString() != null ? true : false);
                        if (propietatCanviada.getOrgValueString() == null && propietatCanviada.getNewValueString() != null && (!usuarisCandidats.isEmpty() || !grupsCandidats.isEmpty())) {
                            PatchHelper.replaceBooleanProperty(jpb, "agafada", true);
                        }
                        break;
                    case "owner":
                        break;
                    case "dueDate":
                        PatchHelper.replaceDateProperty(jpb, "dataFins", (Date) propietatCanviada.getOrgValue(), (Date) propietatCanviada.getNewValue());
                        break;
                    case "priority":
                        PatchHelper.replaceIntegerProperty(jpb, "prioritat", (Integer) propietatCanviada.getOrgValue(), (Integer) propietatCanviada.getNewValue());
                        break;
                    case "parentTask":
                        break;
                    case "delegation":
                        break;
                    case "followUpDate":
                        break;
                    case "delete":
                        break;
                }
            }
            String titolOriginal = originalDesc != null ? originalDesc : originalName;
            String titolFinal = finalDesc != null ? finalDesc : finalName;
            PatchHelper.replaceStringProperty(jpb, "titol", titolOriginal, titolFinal);

            tascaClientService.patchTascaV1(
                    delegateTask.getId(),
                    PatchHelper.toJsonNode(jpb));
        }

    }

}