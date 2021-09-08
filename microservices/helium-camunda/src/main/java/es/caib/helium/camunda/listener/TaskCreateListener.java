package es.caib.helium.camunda.listener;

import es.caib.helium.client.expedient.tasca.TascaClientService;
import es.caib.helium.client.expedient.tasca.model.TascaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskCreateListener implements TaskListener {

    private final TascaClientService tascaClientService;

    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Expedients i Tasques
        log.info("Tasca " + delegateTask.getName() + "has been created");

        boolean tascaAssignada = delegateTask.getAssignee() == null || delegateTask.getAssignee().isBlank();
        List<String> grupsCandidats = delegateTask.getCandidates()
                .stream()
                .filter(c -> c.getGroupId() != null)
                .map(c -> c.getGroupId())
                .collect(Collectors.toList());
        List<String> usuarisCandidats =  delegateTask.getCandidates()
                .stream()
                .filter(c -> c.getUserId() != null)
                .map(c -> c.getGroupId())
                .collect(Collectors.toList());

        TascaDto tascaDto = TascaDto.builder()
                .tascaId(delegateTask.getId())
//                .expedientId()
                .procesId(delegateTask.getProcessInstanceId())
                .nom(delegateTask.getName())
                .titol(delegateTask.getDescription() != null ? delegateTask.getDescription() : delegateTask.getName())
//                .afagada(tascaAssignada)
                .afagada(false)
                .cancelada(false)
                .suspesa(false)
                .completada(false)
                .assignada(tascaAssignada)
//                .marcadaFinalitzar()
//                .errorFinalitzacio()
                .dataFins(delegateTask.getDueDate())
//                .dataFi(null)
//                .iniciFinalitzacio()
                .dataCreacio(delegateTask.getCreateTime())
                .usuariAssignat(delegateTask.getAssignee())
                .grups(grupsCandidats)
                .responsables(usuarisCandidats)
                .prioritat(delegateTask.getPriority())
                .processDefinitionId(delegateTask.getProcessDefinitionId())
                .build();
        tascaClientService.createTascaV1(tascaDto);
    }
}
