package es.caib.helium.camunda.config;

import es.caib.helium.client.dada.DadaClient;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.DelegateVariableInstance;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.event.HistoryEventTypes;
import org.camunda.bpm.spring.boot.starter.event.ExecutionEvent;
import org.camunda.bpm.spring.boot.starter.event.TaskEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CamundaEventListener {

    @Autowired
    RuntimeService runtimeService;
    @Autowired
    HistoryService historyService;
    @Autowired
    DadaClient dadaClient;

    @EventListener
    public void onExecutionEvent(DelegateExecution executionDelegate) {
        // handle mutable execution event
        log.info("ExecutionEvent listener processing mutable event. \n Current ActivityId: {} ExecutionId: {} \n >>> Event Name: {}  \n",
                executionDelegate.getCurrentActivityId(), executionDelegate.getId(), executionDelegate.getEventName());
//        if (executionDelegate.getEventName().equals(ExecutionListener.EVENTNAME_START)) {
//            ((ExecutionEntity) executionDelegate).addVariableListener(new HeliumVariableListener());
//        }
    }

    @EventListener
    public void onVariableEvent(DelegateVariableInstance variableDelegate) {
        log.info("VariableEvent listener. \n Current ActivityId: {} VarialbeId: {} VariableName: {} \n >>> Event Name: {}  \n",
                variableDelegate.getActivityInstanceId(), variableDelegate.getId(), variableDelegate.getName(), variableDelegate.getEventName());
    }

    @EventListener
    public void onHistoryEvent(HistoryEvent historyEvent) {
        // handle history event
        log.info("HistoryEvent listener processing mutable event. \n ProcessDefinitionKey: {} ProcessInstanceId: {} \n EventId: {} \n >>> EventType: {} \n",
                historyEvent.getProcessDefinitionKey(), historyEvent.getProcessInstanceId(), historyEvent.getId(), historyEvent.getEventType());
        if (historyEvent.isEventOfType(HistoryEventTypes.PROCESS_INSTANCE_START)) {
            log.info("ProcessInstance Start. ProcessInstanceId:{}, RootProcessInstanceId: {}",
                    historyEvent.getProcessInstanceId(), historyEvent.getRootProcessInstanceId());
//            // 1. Informar al MS Dades del nom ProcessInstance
//            String rootProcessInstanceId = historyEvent.getProcessInstanceId();
//            HistoricProcessInstance superProcessInstance = null;
//            do {
//                superProcessInstance = historyService.createHistoricProcessInstanceQuery().subProcessInstanceId(rootProcessInstanceId).singleResult();
//                if (superProcessInstance != null) {
//                    rootProcessInstanceId = superProcessInstance.getId();
//                }
//            } while (superProcessInstance != null);
//            // TODO: Afegir mètode per crear nova instància de procés al MS DADES
//            // dadaClient.createProcess(historyEvent.getProcessInstanceId(), rootProcessInstanceId);
//
//            // 2. Activar listener per variables
//            var execution = runtimeService.createExecutionQuery()
//                    .executionId(historyEvent.getExecutionId())
////                    .processInstanceId(historyEvent.getProcessInstanceId())
//                    .singleResult();
//            ((ExecutionEntity) execution).addVariableListener(new HeliumVariableListener());
        }
    }

    @EventListener
    public void onExecutionEvent(ExecutionEvent executionEvent) {
        // handle immutable execution event
        log.info("ExecutionEvent listener processing immutable event. \n Current ActivityId: {} ExecutionId: {} \n >>> Event Name: {}  \n",
                executionEvent.getCurrentActivityId(), executionEvent.getId(), executionEvent.getEventName());
    }

    @EventListener
    public void onTaskEvent(TaskEvent taskEvent) {
        // handle immutable task event
        log.info("TaskEvent listener processing immutable task event. \n TaskDefinitionKey: {} TaskId: {} \n >>> Event Name: {}  \n",
                taskEvent.getTaskDefinitionKey(), taskEvent.getId(), taskEvent.getEventName());
//        log.info("Name: " + taskEvent.getName());
//        switch (taskEvent.getEventName()) {
//            case TaskListener.EVENTNAME_CREATE:
//                log.info("Task create");
//                break;
//            case TaskListener.EVENTNAME_COMPLETE:
//                log.info("Task complete");
//                break;
//            case TaskListener.EVENTNAME_ASSIGNMENT:
//                log.info("Task assign");
//                break;
//            case TaskListener.EVENTNAME_UPDATE:
//                log.info("Task update");
//                break;
//            case TaskListener.EVENTNAME_TIMEOUT:
//                log.info("Task timeout");
//                break;
//            case TaskListener.EVENTNAME_DELETE:
//                log.info("Task delete");
//                break;
//            case ExecutionListener.EVENTNAME_START:
//                log.info("Task start");
//                break;
//            case ExecutionListener.EVENTNAME_END:
//                log.info("Task end");
//                break;
//            case ExecutionListener.EVENTNAME_TAKE:
//                log.info("Task take");
//                break;
//            default:
//                log.info("Task " + taskEvent.getEventName());
//        }
    }

    @EventListener
    public void onDelegateTaskEvent(DelegateTask taskDelegate) {
        // handle mutable task event
        log.info("1st TaskEvent listener processing mutable event. \n TaskDefinitionKey: {} TaskId: {} \n >>> Event Name: {}  \n",
                taskDelegate.getTaskDefinitionKey(), taskDelegate.getId(), taskDelegate.getEventName());
        log.info("Name: " + taskDelegate.getName());
        switch (taskDelegate.getEventName()) {
            case TaskListener.EVENTNAME_CREATE:
                log.info("Task create");
                break;
            case TaskListener.EVENTNAME_COMPLETE:
                log.info("Task complete");
                break;
            case TaskListener.EVENTNAME_ASSIGNMENT:
                log.info("Task assign");
                break;
            case TaskListener.EVENTNAME_UPDATE:
                log.info("Task update");
                break;
            case TaskListener.EVENTNAME_TIMEOUT:
                log.info("Task timeout");
                break;
            case TaskListener.EVENTNAME_DELETE:
                log.info("Task delete");
                break;
            case ExecutionListener.EVENTNAME_START:
                log.info("Task start");
                break;
            case ExecutionListener.EVENTNAME_END:
                log.info("Task end");
                break;
            case ExecutionListener.EVENTNAME_TAKE:
                log.info("Task take");
                break;
            default:
                log.info("Task " + taskDelegate.getEventName());
        }
    }

}
