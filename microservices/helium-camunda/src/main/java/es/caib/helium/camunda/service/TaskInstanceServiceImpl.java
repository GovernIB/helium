package es.caib.helium.camunda.service;

import es.caib.helium.camunda.mapper.TaskInstanceMapper;
import es.caib.helium.camunda.model.DelegationInfo;
import es.caib.helium.camunda.model.WTaskInstance;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskInstanceServiceImpl implements TaskInstanceService {

    private final TaskService taskService;
    private final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final TaskInstanceMapper taskInstanceMapper;
    private final ActionService actionService;

    @Override
    public WTaskInstance getTaskById(String taskId) {
//        var htask = historyService
//                .createHistoricTaskInstanceQuery()
//                .taskId(taskId)
//                .singleResult();
        Task task = getTask(taskId);
        return taskInstanceMapper.toWTaskInstance(task);
    }

    @Override
    public List<WTaskInstance> findTaskInstancesByProcessInstanceId(String processInstanceId) {
        List<WTaskInstance> wTaskInstances = new ArrayList<>();
        var tasks = taskService
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
        if (tasks != null) {
            wTaskInstances = tasks.stream()
                    .map(taskInstanceMapper::toWTaskInstance)
                    .collect(Collectors.toList());
        }
        return wTaskInstances;
    }

    @Override
    public String getTaskInstanceIdByExecutionTokenId(String executionTokenId) {
        List<WTaskInstance> wTaskInstances = new ArrayList<>();
        var task = taskService
                .createTaskQuery()
                .executionId(executionTokenId)
                .active()
                .singleResult();
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not found. tokenId: " + executionTokenId);
        }
        // TODO: Comprovar si retorna un Long!!
        return task.getId();
    }

    @Override
    public void takeTaskInstance(String taskId, String actorId) {
//        taskService.claim(taskId, actorId);
        taskService.setAssignee(taskId, actorId);
    }

    @Override
    public void releaseTaskInstance(String taskId) {
        taskService.claim(taskId, null);
    }

    @Override
    public WTaskInstance startTaskInstance(String taskId) {
        // TODO: No existeix el concepte de Task Start
        return taskInstanceMapper.toWTaskInstance(getTask(taskId));
    }

    @Override
    public void completeTaskInstance(String taskId, String outcome) {
        // TODO: No es pot indicar una transició de sortida
        taskService.complete(taskId);
//        return null;
    }

    @Override
    public WTaskInstance cancelTaskInstance(String taskId) {
        Task task = getTask(taskId);
        runtimeService.createProcessInstanceModification(task.getProcessInstanceId())
//                .cancelAllForActivity(taskId);
                .cancelActivityInstance(taskId)
                .execute();
        return taskInstanceMapper.toWTaskInstance(task);
    }

    @Override
    public WTaskInstance suspendTaskInstance(String taskId) {
        // TODO: No existeix el concepte de suspendre una tasca.
        //       Es pot suspende el processInstance, però no la tasca
        return taskInstanceMapper.toWTaskInstance(getTask(taskId));
    }

    @Override
    public WTaskInstance resumeTaskInstance(String taskId) {
        // TODO idem suspendre
        return taskInstanceMapper.toWTaskInstance(getTask(taskId));
    }

    @Override
    public WTaskInstance reassignTaskInstance(String taskId, String expression, Long entornId) {
        // TODO: Necessitam fer codi per a les expressions d'assignació? o Camunda ja té alguna cosa
        var task = getTask(taskId);
        String actorId = (String)actionService.evaluateExpression(
                null,
                task.getProcessInstanceId(),
                "javascript",
                expression,
                null);
        taskService.setAssignee(taskId, actorId);
        task.setAssignee(actorId);
        return taskInstanceMapper.toWTaskInstance(task);
    }

    @Override
    public void delegateTaskInstance(WTaskInstance task, String actorId, String comentari, boolean supervisada) {
        // TODO: Camunda ofereix la delegació. Però no dóna opció de supervisió...
        taskService.delegateTask(task.getId(), actorId);
        taskService.createComment(task.getId(), task.getProcessInstanceId(), comentari);
    }

    @Override
    public DelegationInfo getDelegationTaskInstanceInfo(String taskId, boolean includeActors) {
        // TODO: La informació de delegació actualment es desa a Heliium.
        //       S'ha de mirar a veure si la hem d'obtenir aquó, o no fa falta
        var task = getTask(taskId);
        DelegationInfo delegationInfo = DelegationInfo.builder()
                .sourceTaskId(taskId)
                .targetTaskId(taskId)
                .start(task.getCreateTime())
//                .end()
                .supervised(true)
                .usuariDelegador(task.getOwner())
                .usuariDelegat(task.getAssignee())
                .build();
        return null;
    }

    @Override
    public void cancelDelegationTaskInstance(WTaskInstance task) {
        taskService.resolveTask(task.getId());
    }

    @Override
    public void updateTaskInstanceInfoCache(String taskId, String titol, String infoCache) {
        // TODO: Mirar si s'utilitza!!
    }

    @Override
    public List<String> findStartTaskOutTransitions(String processDefinitionId, String taskName) {
        return new ArrayList<>();
    }

    @Override
    public List<String> findTaskInstanceTransitions(String taskInstanceId) {
        // TODO: Mirar si existeixen Conditional Flows de sortida configurats segons el valor d'una variable
        //      o si el següent node és un decicion que depen del valor d'una variable
        //      --> S'hauria de configurar a Helium, a la tasca!!!??
        return new ArrayList<>();
    }


    private Task getTask(String taskId) {
        var task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Task: " + taskId);
        }
        return task;
    }
}
