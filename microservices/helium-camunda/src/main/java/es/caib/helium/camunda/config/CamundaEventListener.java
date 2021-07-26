package es.caib.helium.camunda.config;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CamundaEventListener {

    @EventListener
    public void onExecutionEvent(DelegateExecution executionDelegate) {
        // handle mutable execution event
        log.info("ExecutionEvent listener processing mutable event. \n Current ActivityId: {} ExecutionId: {} \n >>> Event Name: {}  \n",
                executionDelegate.getCurrentActivityId(), executionDelegate.getId(), executionDelegate.getEventName());
    }

    @EventListener
    public void onHistoryEvent(HistoryEvent historyEvent) {
        // handle history event
        log.info("HistoryEvent listener processing mutable event. \n ProcessDefinitionKey: {} ProcessInstanceId: {} \n EventId: {} \n >>> EventType: {} \n",
                historyEvent.getProcessDefinitionKey(), historyEvent.getProcessInstanceId(), historyEvent.getId(), historyEvent.getEventType());
    }

    /**
     * The mutable event stream objects can be modified multiple times between creation and reception of the
     * event the listener has asynchronously subscribed to. However, immutable event objects reflect the state
     * at the event creation time, regardless of the time they are received by the listener.
     */

//    @EventListener
//    public void onExecutionEvent(ExecutionEvent executionEvent) {
//        // handle immutable execution event
//        log.info("ExecutionEvent listener processing immutable event. \n Current ActivityId: {} ExecutionId: {} \n >>> Event Name: {}  \n",
//                executionEvent.getCurrentActivityId(), executionEvent.getId(), executionEvent.getEventName());
//    }
//
//    @EventListener
//    public void onTaskEvent(TaskEvent taskEvent) {
//        // handle immutable task event
//        log.info("TaskEvent listener processing immutable task event. \n TaskDefinitionKey: {} TaskId: {} \n >>> Event Name: {}  \n",
//                taskEvent.getTaskDefinitionKey(), taskEvent.getId(), taskEvent.getEventName());
//    }

    /**
     * Multiple listeners for the same event type can be registered. Their order is defined via the @order annotation.
     *
     * @param taskDelegate event's context information
     */
    @Order(1)
    @EventListener
    public void firstTaskEventListener(DelegateTask taskDelegate) {
        // handle mutable task event
        log.info("1st TaskEvent listener processing mutable event. \n TaskDefinitionKey: {} TaskId: {} \n >>> Event Name: {}  \n",
                taskDelegate.getTaskDefinitionKey(), taskDelegate.getId(), taskDelegate.getEventName());
    }

    @Order(2)
    @EventListener
    public void secondTaskEventListener(DelegateTask taskDelegate) {
        // handle mutable task event
        log.info("2nd TaskEvent listener processing mutable event. \n TaskDefinitionKey: {} TaskId: {} \n >>> Event Name: {}  \n",
                taskDelegate.getTaskDefinitionKey(), taskDelegate.getId(), taskDelegate.getEventName());
    }
}
