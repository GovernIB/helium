package es.caib.helium.camunda.service;

import es.caib.helium.camunda.helper.CacheHelper;
import es.caib.helium.camunda.mapper.ProcessInstanceMapper;
import es.caib.helium.camunda.model.WProcessInstance;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.migration.MigrationPlan;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessInstanceServiceImpl implements ProcessInstanceService {

    private final TaskService taskService;
//    private final RepositoryService repositoryService;
//    private final ManagementService managementService;
    private final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final CacheHelper cacheHelper;
    private final ProcessInstanceMapper processInstanceMapper;


    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId) {
        List<WProcessInstance> wProcessInstances = new ArrayList<>();
//        var processInstances = runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinitionId).list();
        var processInstances = historyService.createHistoricProcessInstanceQuery()
                .unfinished()
                .processDefinitionId(processDefinitionId)
                .list();
        if (processInstances != null) {
            wProcessInstances = processInstances.stream()
                    .map(processInstanceMapper::toWProcessInstance)
                    .collect(Collectors.toList());
        }
        return wProcessInstances;
    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionName(String processDefinitionName) {
        List<WProcessInstance> wProcessInstances = new ArrayList<>();
        var processInstances = historyService.createHistoricProcessInstanceQuery()
                .unfinished()
                .processDefinitionName(processDefinitionName)
                .list();
        if (processInstances != null) {
            wProcessInstances = processInstances.stream()
                    .map(processInstanceMapper::toWProcessInstance)
                    .collect(Collectors.toList());
        }
        return wProcessInstances;
    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(String processDefinitionName, Long entornId) {
        List<WProcessInstance> wProcessInstances = new ArrayList<>();
        var processInstances = historyService.createHistoricProcessInstanceQuery()
                .unfinished()
                .processDefinitionName(processDefinitionName)
                .tenantIdIn(entornId.toString())
                .list();
        if (processInstances != null) {
            wProcessInstances = processInstances.stream()
                    .map(processInstanceMapper::toWProcessInstance)
                    .collect(Collectors.toList());
        }
        return wProcessInstances;
    }

    @Override
    public List<WProcessInstance> getProcessInstanceTree(String rootProcessInstanceId) {
        List<WProcessInstance> wProcessInstances = new ArrayList<>();
        var processInstances = runtimeService.createProcessInstanceQuery()
                .superProcessInstanceId(rootProcessInstanceId)
                .list();
        if (processInstances != null) {
            wProcessInstances = processInstances.stream()
                    .map(processInstanceMapper::toWProcessInstance)
                    .collect(Collectors.toList());
        }
        return wProcessInstances;
    }

    @Override
    public WProcessInstance getProcessInstance(String processInstanceId) {
        ProcessInstance processInstance;
//        var processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        try {
            processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No s'ha pogut obtenir la instància de procés: " + processInstanceId, ex);
        }
        if (processInstance == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Process instance: " + processInstanceId);
        }
        return processInstanceMapper.toWProcessInstance(processInstance);
    }

    @Override
    public WProcessInstance getRootProcessInstance(String processInstanceId) {
        ProcessInstance superProcessInstance = null;
        do {
            superProcessInstance = runtimeService.createProcessInstanceQuery().subProcessInstanceId(processInstanceId).singleResult();
            if (superProcessInstance != null) {
                processInstanceId = superProcessInstance.getId();
            }
        } while (superProcessInstance != null);

        if (superProcessInstance == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Root process instance: " + processInstanceId);
        }
        return processInstanceMapper.toWProcessInstance(superProcessInstance);
    }

    @Override
    public List<String> findRootProcessInstances(String actorId, List<String> processInstanceIds, boolean nomesMeves, boolean nomesTasquesPersonals, boolean nomesTasquesGrup) {
        // TODO: mirar per a qué s'utilitza
        // S'haurien d'agafar les tasques del MS d'expedietns i tasques!!
//        boolean nomesAmbPendents = true; // Mostrar sólo las pendientes
//        boolean personals = nomesTasquesPersonals && !nomesTasquesGrup;
//        boolean grup = !nomesTasquesPersonals && nomesTasquesGrup;
//        boolean tots = !nomesTasquesPersonals && !nomesTasquesGrup;
//
//        var taskQuery = taskService
//                .createTaskQuery()
//                .processInstanceIdIn(processInstanceIds.toArray(new String[0]));
//        if (nomesAmbPendents) {
//            taskQuery.active();
//        }
//        if (grup && !nomesMeves) {
//            taskQuery.taskCandidateUser(actorId);
//        }
//
//        if (personals) {
//            taskQuery.taskAssignee(actorId);
//        }
//        var tasks = taskQuery.list();
//        tasks.stream().forEach(t -> t.getProcessInstanceId());
        return null;
    }

    @Override
    public WProcessInstance startProcessInstanceById(String actorId, String processDefinitionId, Map<String, Object> variables) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, variables);

        // TODO: Indicar a Helium quina és la tasca inicial (si n'hi ha), o indicar a Helium que té una tasca inicial
        // Passar-ho com a paràmetre, de manera que aquí la podrem assignar
        //
//        var task = taskService.createTaskQuery()
//                .processInstanceId(processInstance.getId())
//                .taskName(startTaskName)
//                .active()
//                .singleResult();
//        task.setAssignee(actorId);
        return processInstanceMapper.toWProcessInstance(processInstance);
    }

    @Override
    public void signalProcessInstance(String processInstanceId, String transitionName) {
        final String finalTransitionName = (transitionName == null || transitionName.isBlank()) ? "default" : transitionName;
        var executions = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).active().list();
        executions.stream().forEach(ex -> runtimeService.signalEventReceived(finalTransitionName, ex.getId()));
    }

    @Override
    public void deleteProcessInstance(String processInstanceId) {
        runtimeService.deleteProcessInstance(processInstanceId, "Motiu no definit");
        historyService.deleteHistoricProcessInstance(processInstanceId);
    }

    @Override
    public void suspendProcessInstances(String[] processInstanceIds) {
        if (processInstanceIds != null) {
            Arrays.stream(processInstanceIds).forEach(pi -> runtimeService.suspendProcessInstanceById(pi));
        }
    }

    @Override
    public void resumeProcessInstances(String[] processInstanceIds) {
        if (processInstanceIds != null) {
            Arrays.stream(processInstanceIds).forEach(pi -> runtimeService.activateProcessInstanceById(pi));
        }
    }

    @Override
    public void changeProcessInstanceVersion(String processInstanceId, int newVersion) {

        HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        ProcessDefinition processDefinition = cacheHelper.getDefinicioProces(historicProcessInstance.getProcessDefinitionId());

        String processDefinitionId = processDefinition.getId();
        int processDefinitionVersion = processDefinition.getVersion();

//        BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
//        Collection<Activity> processDefinitionActivities = modelInstance.getModelElementsByType(Activity.class);

        MigrationPlan migrationPlan = runtimeService
                .createMigrationPlan(processDefinitionId + ":" + processDefinitionVersion, processDefinitionId + ":" + newVersion)
                .mapEqualActivities()
                // TODO: permetre passar un mapa per a mapejar activitats que han canviat de nom
//                .mapActivities("assessCreditWorthiness", "assessCreditWorthiness")
//                .mapActivities("validateAddress", "validatePostalAddress")
//                .mapActivities("archiveApplication", "archiveApplication")
                .build();

        runtimeService.newMigration(migrationPlan)
                .processInstanceIds(processInstanceId)
                .execute();
    }
}
