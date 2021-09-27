package es.caib.helium.camunda.config;

import es.caib.helium.camunda.listener.events.TaskCompletEvent;
import es.caib.helium.client.dada.dades.DadaClient;
import es.caib.helium.client.expedient.tasca.TascaClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class CamundaEventListener {

////    private final RuntimeService runtimeService;
//    private final HistoryService historyService;
//    private final ProcesClientService procesClientService;

    private final TascaClientService tascaClientService;
    private final DadaClient dadaClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskCompleteEvent(TaskCompletEvent event) {

        if (event.getDades() != null && !event.getDades().isEmpty()) {
            dadaClient.postDadaByProcesId(event.getProcesId(), event.getDades());
        }
        tascaClientService.patchTascaV1(event.getTaskId(), event.getTaskPatch());

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleTaskCompleteRollbackEvent(TaskCompletEvent event) {

        log.error("S'ha produït un error al completar la tasca {}", event.getTaskId());

    }

//    // ExecutionListener.EVENTNAME_START;
//    @EventListener(condition = "#event.eventName=='start'")
//    public void onActivityStartEvent(ExecutionEvent event) {
//        log.info("handle execution event {} with event id {}", event.getEventName(), event.getId());
//    }


//    @EventListener
//    public void onExecutionEvent(DelegateExecution delegateExecution) {
//        // handle mutable execution event
//        log.info("ExecutionEvent listener processing mutable event. \n Current ActivityId: {} ExecutionId: {} \n >>> Event Name: {}  \n",
//                delegateExecution.getCurrentActivityId(), delegateExecution.getId(), delegateExecution.getEventName());
//
//        var activity = historyService.createHistoricActivityInstanceQuery()
//                .activityId(delegateExecution.getCurrentActivityId())
//                .singleResult();
//
//        if (ActivityTypes.START_EVENT.equals(activity.getActivityType())) {
//
//        }
//        switch (delegateExecution.getEventName()) {
//            case ExecutionListener.EVENTNAME_START:
//                log.info("Execution start");
//                ((ExecutionEntity) delegateExecution).addVariableListener(new HeliumVariableListener());
//                createProcess(delegateExecution);
//                break;
//            case ExecutionListener.EVENTNAME_END:
//                log.info("Execution end");
//                break;
//            case ExecutionListener.EVENTNAME_TAKE:
//                log.info("Execution take");
//                break;
//            default:
//                log.info("Execution " + delegateExecution.getEventName());
//        }
//
//    }
//
//    @EventListener
//    public void onHistoryEvent(HistoryEvent historyEvent) {
//        // handle history event
//        log.info("HistoryEvent listener processing mutable event. \n ProcessDefinitionKey: {} ProcessInstanceId: {} \n EventId: {} \n >>> EventType: {} \n",
//                historyEvent.getProcessDefinitionKey(), historyEvent.getProcessInstanceId(), historyEvent.getId(), historyEvent.getEventType());
//        if (historyEvent.isEventOfType(HistoryEventTypes.PROCESS_INSTANCE_START)) {
//            log.info("ProcessInstance Start. ProcessInstanceId:{}, RootProcessInstanceId: {}",
//                    historyEvent.getProcessInstanceId(), historyEvent.getRootProcessInstanceId());
////            // 1. Informar al MS Dades del nom ProcessInstance
////            String rootProcessInstanceId = historyEvent.getProcessInstanceId();
////            HistoricProcessInstance superProcessInstance = null;
////            do {
////                superProcessInstance = historyService.createHistoricProcessInstanceQuery().subProcessInstanceId(rootProcessInstanceId).singleResult();
////                if (superProcessInstance != null) {
////                    rootProcessInstanceId = superProcessInstance.getId();
////                }
////            } while (superProcessInstance != null);
////            // dadaClient.createProcess(historyEvent.getProcessInstanceId(), rootProcessInstanceId);
////
////            // 2. Activar listener per variables
////            var execution = runtimeService.createExecutionQuery()
////                    .executionId(historyEvent.getExecutionId())
//////                    .processInstanceId(historyEvent.getProcessInstanceId())
////                    .singleResult();
////            ((ExecutionEntity) execution).addVariableListener(new HeliumVariableListener());
//        }
//    }

//    @EventListener
//    public void onExecutionEvent(ExecutionEvent executionEvent) {
//        // handle immutable execution event
//        log.info("ExecutionEvent listener processing immutable event. \n Current ActivityId: {} ExecutionId: {} \n >>> Event Name: {}  \n",
//                executionEvent.getCurrentActivityId(), executionEvent.getId(), executionEvent.getEventName());
//    }

//    @EventListener
//    public void onTaskEvent(TaskEvent taskEvent) {
//        // handle immutable task event
//        log.info("TaskEvent listener processing immutable task event. \n TaskDefinitionKey: {} TaskId: {} \n >>> Event Name: {}  \n",
//                taskEvent.getTaskDefinitionKey(), taskEvent.getId(), taskEvent.getEventName());
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
//    }

//    @EventListener
//    public void onDelegateTaskEvent(DelegateTask delegateTask) {
//        // handle mutable task event
//        log.info("TaskEvent listener processing mutable event. \n TaskDefinitionKey: {} TaskId: {} \n >>> Event Name: {}  \n",
//                delegateTask.getTaskDefinitionKey(),
//                delegateTask.getId(),
//                delegateTask.getEventName());
//        log.info("Name: " + delegateTask.getName());
//
//        switch (delegateTask.getEventName()) {
//            case TaskListener.EVENTNAME_CREATE:
//                log.info("Task create");
//                createTask(delegateTask);
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
//                log.info("Task " + delegateTask.getEventName());
//        }
//    }

//    private void createProcess(DelegateExecution delegateExecution) {
//        // Publicar al MS Expedients i Tasques
//        log.info("Process " + delegateExecution.getProcessInstanceId() + "has been created");
//
//        ProcesDto procesDto = ProcesDto.builder()
//                .id(delegateExecution.getProcessInstanceId())
//                .processDefinitionId(delegateExecution.getProcessDefinitionId())
//                .procesArrelId()
//                .procesPareId()
//                .descripcio()
//                .suspes(false)
//                .dataInici(delegateExecution.)
////                .dataFi()
//                .build();
//        procesClientService.createProcesV1(procesDto);
//    }
//
//    private void createTask(DelegateTask delegateTask) {
//        // Publicar al MS Expedients i Tasques
//        log.info("Tasca " + delegateTask.getName() + "has been created");
//
//        boolean tascaAssignada = delegateTask.getAssignee() == null || delegateTask.getAssignee().isBlank();
//        List<String> grupsCandidats = delegateTask.getCandidates()
//                .stream()
//                .filter(c -> c.getGroupId() != null)
//                .map(c -> c.getGroupId())
//                .collect(Collectors.toList());
//        List<String> usuarisCandidats =  delegateTask.getCandidates()
//                .stream()
//                .filter(c -> c.getUserId() != null)
//                .map(c -> c.getGroupId())
//                .collect(Collectors.toList());
//
//        TascaDto tascaDto = TascaDto.builder()
//                .id(delegateTask.getId())
////                .expedientId()
//                .procesId(delegateTask.getProcessInstanceId())
//                .nom(delegateTask.getName())
//                .titol(delegateTask.getDescription() != null ? delegateTask.getDescription() : delegateTask.getName())
////                .afagada(tascaAssignada)
//                .afagada(false)
//                .cancelada(false)
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
//                // TODO: Grup assignat
////                .grupAssignat()
//                .prioritat(delegateTask.getPriority())
//                // TODO: Responsables
////                .responsables()
//                .processDefinitionId(delegateTask.getProcessDefinitionId())
//                .build();
//        tascaClientService.createTascaV1(tascaDto);
//    }

//    @EventListener(condition = "#event.eventName=='take'")
//    public void onTaskTakeEvent(ExecutionEvent event) {
//        log.info("handle task event {} for task id {}", event.getEventName(), event.getId());
//    }
//
//    @EventListener(condition = "#event.eventType==HistoryEvent.TASK_EVENT_TYPE_COMPLETE")
//    public void onTaskTakeEvent(HistoryEvent event) {
//        log.info("handle task event {} for task id {}", event.getId());
//    }
}
