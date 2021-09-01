package es.caib.helium.camunda.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.helium.client.expedient.proces.ProcesClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonPatchBuilder;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProcessEndListener implements ExecutionListener {

    private final ProcesClientService procesClientService;

    @Override
    public void notify(DelegateExecution delegateExecution) {
        log.info("Process " + delegateExecution.getProcessInstanceId() + "has been created");

        var processInstance =  (ExecutionEntity)delegateExecution.getProcessInstance();
//        HistoricProcessInstance historicProcessInstance = EngineHelper.getInstance().getHistoryService()
//                .createHistoricProcessInstanceQuery()
//                .processInstanceId(processInstanceId)
//                .singleResult();

        JsonPatchBuilder jpb = Json.createPatchBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//        jpb.replace("dataFi", sdf.format(processInstance.getEndTime()));
        jpb.replace("dataFi", sdf.format(new Date()));
        procesClientService.patchProcesV1(
                processInstance.getId(),
                new ObjectMapper().valueToTree(jpb.build()));
    }
}
