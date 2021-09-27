package es.caib.helium.camunda.service;

import es.caib.helium.camunda.helper.ThreadLocalInfo;
import es.caib.helium.camunda.mapper.TaskInstanceMapper;
import es.caib.helium.client.engine.bridge.WorkflowBridgeClientService;
import es.caib.helium.client.engine.model.CampTascaRest;
import es.caib.helium.client.engine.model.CampTipus;
import es.caib.helium.client.engine.model.WTaskInstance;
import es.caib.helium.client.expedient.tasca.TascaClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ActivityTypes;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.persistence.entity.SuspensionState;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.task.IdentityLinkType;
import org.camunda.bpm.engine.task.Task;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskInstanceServiceImpl implements TaskInstanceService {

    public final static String CANCEL_REASON = "Cancel task";

    private final TaskService taskService;
    private final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final TaskInstanceMapper taskInstanceMapper;
    private final ActionService actionService;
    private final WorkflowBridgeClientService workflowBridgeClientService;
    private final TascaClientService tascaClientService;

    @Override
    public WTaskInstance getTaskById(String taskId) {
        var task = historyService
                .createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .singleResult();
//        var task = getTask(taskId);
        return taskInstanceMapper.toWTaskInstanceWithDetails(task);
    }

    @Override
    public List<WTaskInstance> findTaskInstancesByProcessInstanceId(String processInstanceId) {
        List<WTaskInstance> wTaskInstances = new ArrayList<>();
        var tasks = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        if (tasks != null) {
            wTaskInstances = tasks.stream()
                    .map(taskInstanceMapper::toWTaskInstanceWithDetails)
                    .collect(Collectors.toList());
        }
        return wTaskInstances;
    }

    @Override
    public String getTaskInstanceIdByExecutionTokenId(String executionTokenId) {
        List<WTaskInstance> wTaskInstances = new ArrayList<>();
        var activeTasks = taskService
                .createTaskQuery()
                .executionId(executionTokenId)
                .active()
                .orderByTaskCreateTime().asc()
                .list();
        if (activeTasks == null || activeTasks.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not found. tokenId: " + executionTokenId);
        }
        // TODO: Comprovar si retorna un Long!!
        return activeTasks.get(0).getId();
    }

    @Override
    public WTaskInstance takeTaskInstance(String taskId, String actorId) {
        taskService.claim(taskId, actorId);
        return taskInstanceMapper.toWTaskInstance(getTask(taskId));
//        taskService.setAssignee(taskId, actorId);
    }

    @Override
    public WTaskInstance releaseTaskInstance(String taskId) {
        ThreadLocalInfo.setReleaseTaskThreadLocal(true);
        taskService.claim(taskId, null);
        return taskInstanceMapper.toWTaskInstance(getTask(taskId));
    }

    @Override
    public WTaskInstance startTaskInstance(String taskId) {
        // TODO: No existeix el concepte de Task Start
        return taskInstanceMapper.toWTaskInstance(getTask(taskId));
    }

    @Override
    public void completeTaskInstance(String taskId, String outcome) {
        // TODO: No es pot indicar una transició de sortida
        var task = getTask(taskId);
//        taskService.complete(taskId, runtimeService.getVariables(task.getExecutionId()));
        List<CampTascaRest> campsTasca = new ArrayList<>();
        try {
            campsTasca = workflowBridgeClientService.findCampsPerTaskInstance(
                    task.getProcessInstanceId(),
                    task.getProcessDefinitionId(),
                    task.getName());
        } catch (Exception ex) {
            log.error("No ha estat possible obtenir els camps de la tasca.", ex);
        }

        var variableNames = campsTasca.stream()
                .filter(c -> c.isWriteTo() && !CampTipus.ACCIO.equals(c.getCamp().getTipus()))
                .map(c -> c.getCamp().getCodi())
                .collect(Collectors.toList());
        if (variableNames != null && !variableNames.isEmpty()) {
            taskService.complete(taskId, taskService.getVariables(taskId, variableNames));
        } else {
            taskService.complete(taskId);
        }

    }

    @Override
    public WTaskInstance cancelTaskInstance(String taskId) {
        Task task = getTask(taskId);
        var activity = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .activityType(ActivityTypes.TASK_USER_TASK)
                .activityName(task.getName())
                .singleResult();
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No s'ha trobat una intància de la tasca " + taskId);
        }

        runtimeService.createProcessInstanceModification(task.getProcessInstanceId())
//                .cancelAllForActivity(taskId);
                .cancelActivityInstance(activity.getId())
                .execute();
        return taskInstanceMapper.toWTaskInstance(task);
    }

    @Override
    @Transactional
    public WTaskInstance suspendTaskInstance(String taskId) {
        // TODO: No existeix el concepte de suspendre una tasca.
        //       Es pot suspende el processInstance, però no la tasca
        TaskEntity task = (TaskEntity) getTask(taskId);
        if (SuspensionState.ACTIVE.getStateCode() == task.getSuspensionState()) {
            task.setSuspensionState(SuspensionState.SUSPENDED.getStateCode());
            taskService.saveTask(task);
        }
        return taskInstanceMapper.toWTaskInstance(task);
    }

    @Override
    @Transactional
    public WTaskInstance resumeTaskInstance(String taskId) {
        // TODO idem suspendre
        TaskEntity task = (TaskEntity) getTask(taskId);
        if (task.isSuspended()) {
            task.setSuspensionState(SuspensionState.ACTIVE.getStateCode());
            taskService.saveTask(task);
        }
        return taskInstanceMapper.toWTaskInstance(task);
    }

    @Override
    public WTaskInstance reassignTaskInstance(String taskId, String expressionLanguage, String expression, Long entornId) {
        var task = getTask(taskId);
        if (expression.toLowerCase().startsWith("user(")) {
            removeCandidates(taskId);   // Eliminam la assignació actual
//            if (expression.contains(",")) {
//                List.of(expression.substring(5, expression.length() - 1).split(",")).forEach(g -> taskService.addCandidateUser(taskId, g));
//            } else {
            taskService.setAssignee(taskId, expression.substring(5, expression.length() - 1));
//            }
        } else if (expression.toLowerCase().startsWith("group(")) {
            removeCandidates(taskId);   // Eliminam la assignació actual
            // TODO: Afegir la opció de group(xxx)-->member(yyy) per a cercar el càrrec yyy dins el grup xxx
            // TODO: Validar grup existeix (plugin usuaris)
            if (expression.contains(",")) {
                var grups = List.of(expression.substring(6, expression.length() - 1).split(","));
                grups.forEach(g -> taskService.addCandidateGroup(taskId, g));
//                tascaClientService.setGrupsV1(taskId, grups);
            } else {
                var grup = expression.substring(6, expression.length() - 1);
                taskService.addCandidateGroup(taskId, grup);
//                tascaClientService.setGrupsV1(taskId, List.of(grup));
            }
        } else {
            // TODO: Ara amb expressió només assignam usuaris.
            //  Hauriem de comprovar si el codi indicat correspon a un usuari o un grup,
            //  i si és un usuari, que efectivament existeix
            var destinataris = actionService.evaluateExpression(
                    null,
                    task.getProcessInstanceId(),
                    expressionLanguage,
                    expression,
                    null);
            if (destinataris instanceof String) {
                String usuari = (String) destinataris;
                removeCandidates(taskId);
                taskService.setAssignee(taskId, usuari);
            } else if (destinataris instanceof String[]) {
                var usuaris = List.of((String[]) destinataris);
                removeCandidates(taskId);
                usuaris.forEach(g -> taskService.addCandidateUser(taskId, g));
//                tascaClientService.setResponsablesV1(taskId, usuaris);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'expressió no retorna un codi d'usuari, o un array de codis d'usuaris");
            }
        }
        return taskInstanceMapper.toWTaskInstance(task);
    }

    private void removeCandidates(String taskId) {
        ThreadLocalInfo.setReleaseTaskThreadLocal(true);
        taskService.setAssignee(taskId, null);
        taskService.getIdentityLinksForTask(taskId).stream()
                .filter(i -> IdentityLinkType.CANDIDATE.equals(i.getType()))
                .forEach(c -> {
                    if (c.getUserId() != null) {
                        taskService.deleteCandidateUser(taskId, c.getUserId());
                    } else {
                        taskService.deleteCandidateGroup(taskId, c.getGroupId());
                    }
                });
//        tascaClientService.deleteResponsablesV1(taskId);
    }

    @Override
    public void setTaskInstanceActorId(String taskId, String actorId) {
        var task = getTask(taskId);
        taskService.setAssignee(taskId, actorId);
//        task.setAssignee(actorId);
    }

    @Override
    public void setTaskInstancePooledActors(String taskId, String[] pooledActors) {
        var task = getTask(taskId);
        Arrays.stream(pooledActors).forEach(a -> taskService.addCandidateUser(taskId, a));
    }

//    @Override
//    public void delegateTaskInstance(WTaskInstance task, String actorId, String comentari, boolean supervisada) {
//        // TODO: Camunda ofereix la delegació. Però no dóna opció de supervisió...
//        taskService.delegateTask(task.getId(), actorId);
//        taskService.createComment(task.getId(), task.getProcessInstanceId(), comentari);
//    }
//
//    @Override
//    public DelegationInfo getDelegationTaskInstanceInfo(String taskId, boolean includeActors) {
//        // TODO: La informació de delegació actualment es desa a Heliium.
//        //       S'ha de mirar a veure si la hem d'obtenir aquó, o no fa falta
//        var task = getTask(taskId);
//        DelegationInfo delegationInfo = DelegationInfo.builder()
//                .sourceTaskId(taskId)
//                .targetTaskId(taskId)
//                .start(task.getCreateTime())
//                .supervised(true)
//                .usuariDelegador(task.getOwner())
//                .usuariDelegat(task.getAssignee())
//                .build();
//        return null;
//    }
//
//    @Override
//    public void cancelDelegationTaskInstance(WTaskInstance task) {
//        taskService.resolveTask(task.getId());
//    }

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
