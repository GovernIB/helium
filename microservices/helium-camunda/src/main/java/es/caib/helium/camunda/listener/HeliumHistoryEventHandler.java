package es.caib.helium.camunda.listener;

import es.caib.helium.camunda.helper.EngineHelper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.impl.history.event.HistoricVariableUpdateEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.event.HistoryEventTypes;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.engine.task.Task;

import java.util.List;

@Slf4j
public class HeliumHistoryEventHandler implements HistoryEventHandler {

    @Override
    public void handleEvent(HistoryEvent historyEvent) {
        log.info("Event: " + historyEvent.toString());

        if (historyEvent.isEventOfType(HistoryEventTypes.VARIABLE_INSTANCE_CREATE) ||
              historyEvent.isEventOfType(HistoryEventTypes.VARIABLE_INSTANCE_UPDATE)) {
            log.info("Creant o actualitzant variable");
            Task task = null;
            var event = (HistoricVariableUpdateEventEntity) historyEvent;
            if (event.getActivityInstanceId() != null) {
                task = EngineHelper.getInstance().getTaskService().createTaskQuery()
                        .taskId(event.getActivityInstanceId().substring(event.getActivityInstanceId().indexOf(":") + 1))
                        .singleResult();
            }
            if (task == null) {
                log.info("Variable de procés");
            } else {
                log.info("Variable de tasca");
            }
        }
        if (historyEvent.isEventOfType(HistoryEventTypes.VARIABLE_INSTANCE_DELETE)) {
            log.info("Borrant variable: " + historyEvent.toString());
        }
        // HistoricVariableUpdateEventEntity[variableName=invoiceDocument, variableInstanceId=13ae610e-ed25-11eb-a2f6-0242a7d600dc, revision=0, serializerName=string, longValue=null, doubleValue=null, textValue=, textValue2=!emptyString!, byteArrayId=null, activityInstanceId=1022c946-ed25-11eb-a2f6-0242a7d600dc, scopeActivityInstanceId=1022c946-ed25-11eb-a2f6-0242a7d600dc, eventType=create, executionId=1022c946-ed25-11eb-a2f6-0242a7d600dc, id=null, processDefinitionId=1022c946-ed25-11eb-a2f6-0242a7d600dc, processInstanceId=1022c946-ed25-11eb-a2f6-0242a7d600dc, taskId=null, timestamp=Sun Jul 25 10:48:26 CEST 2021, tenantId=null, isInitial=true]
    }

    @Override
    public void handleEvents(List<HistoryEvent> list) {
        log.info("Events: ");
        list.stream().forEach(e -> log.info("Events: " + e.toString()));
    }
}
