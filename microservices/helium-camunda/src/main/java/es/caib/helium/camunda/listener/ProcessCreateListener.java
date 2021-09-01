package es.caib.helium.camunda.listener;

import es.caib.helium.client.expedient.proces.ProcesClientService;
import es.caib.helium.client.expedient.proces.model.ProcesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProcessCreateListener implements ExecutionListener {

    private final ProcesClientService procesClientService;

//    private final RuntimeService runtimeService;
//    private final HistoryService historyService;

    @Override
    public void notify(DelegateExecution delegateExecution) {
        log.info("Process " + delegateExecution.getProcessInstanceId() + "has been created");

//        var processInstanceId =  delegateExecution.getProcessInstanceId();
        var processInstance = (ExecutionEntity)delegateExecution.getProcessInstance();

//        ProcessInstance processInstancePare = null;
//        try {
//            processInstancePare = EngineHelper.getInstance().getRuntimeService()
//                    .createProcessInstanceQuery()
//                    .processInstanceId(delegateExecution.getProcessInstanceId())
//                    .subProcessInstanceId(delegateExecution.getProcessInstanceId())
//                    .singleResult();
//        } catch (Exception e) {}

        ProcesDto procesDto = ProcesDto.builder()
                .id(processInstance.getId())
                .processDefinitionId(processInstance.getProcessDefinitionId())
                .procesArrelId(processInstance.getRootProcessInstanceId())
                .procesPareId(processInstance.getParentId())
//                .descripcio()
                .suspes(false)
                .dataInici(new Date())
//                .dataInici(processInstance.getStartTime())
//                .dataFi()
                .build();
        procesClientService.createProcesV1(procesDto);
    }

}
