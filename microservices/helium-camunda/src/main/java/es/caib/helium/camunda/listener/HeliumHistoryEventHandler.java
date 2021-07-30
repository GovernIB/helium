package es.caib.helium.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;

import java.util.List;

@Slf4j
public class HeliumHistoryEventHandler implements HistoryEventHandler {

    @Override
    public void handleEvent(HistoryEvent historyEvent) {
      log.info("Event: " + historyEvent.toString());

        // HistoricVariableUpdateEventEntity[variableName=invoiceDocument, variableInstanceId=13ae610e-ed25-11eb-a2f6-0242a7d600dc, revision=0, serializerName=string, longValue=null, doubleValue=null, textValue=, textValue2=!emptyString!, byteArrayId=null, activityInstanceId=1022c946-ed25-11eb-a2f6-0242a7d600dc, scopeActivityInstanceId=1022c946-ed25-11eb-a2f6-0242a7d600dc, eventType=create, executionId=1022c946-ed25-11eb-a2f6-0242a7d600dc, id=null, processDefinitionId=1022c946-ed25-11eb-a2f6-0242a7d600dc, processInstanceId=1022c946-ed25-11eb-a2f6-0242a7d600dc, taskId=null, timestamp=Sun Jul 25 10:48:26 CEST 2021, tenantId=null, isInitial=true]
    }

    @Override
    public void handleEvents(List<HistoryEvent> list) {
        log.info("Events: ");
        list.stream().forEach(e -> log.info("Events: " + e.toString()));
    }
}
