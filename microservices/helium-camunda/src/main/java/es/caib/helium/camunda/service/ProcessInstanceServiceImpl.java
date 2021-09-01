package es.caib.helium.camunda.service;

import es.caib.helium.camunda.helper.CacheHelper;
import es.caib.helium.camunda.mapper.ProcessInstanceMapper;
import es.caib.helium.client.engine.model.WProcessInstance;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.migration.MigrationPlan;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final RepositoryService repositoryService;
//    private final ManagementService managementService;
    private final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final CacheHelper cacheHelper;
    private final ProcessInstanceMapper processInstanceMapper;


    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionName(String processDefinitionName) {
        List<WProcessInstance> wProcessInstances = new ArrayList<>();
        var processInstances = historyService.createHistoricProcessInstanceQuery()
//                .unfinished()
                .processDefinitionName(processDefinitionName)
                .orderByProcessInstanceStartTime().desc()
                .list();
        if (processInstances != null) {
            wProcessInstances = processInstances.stream()
                    .map(processInstanceMapper::toWProcessInstance)
                    .collect(Collectors.toList());
        }
        return wProcessInstances;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(
            String processDefinitionName,
            String entornId) {
        List<WProcessInstance> wProcessInstances = new ArrayList<>();
        var processInstances = historyService.createHistoricProcessInstanceQuery()
//                .unfinished()
                .processDefinitionName(processDefinitionName)
                .tenantIdIn(entornId)
                .orderByProcessInstanceStartTime().desc()
                .list();
        if (processInstances != null) {
            wProcessInstances = processInstances.stream()
                    .map(processInstanceMapper::toWProcessInstance)
                    .collect(Collectors.toList());
        }
        return wProcessInstances;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WProcessInstance> getProcessInstanceTree(String rootProcessInstanceId) {
        List<WProcessInstance> wProcessInstances = new ArrayList<>();
        var rootProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(rootProcessInstanceId)
                .singleResult();
        if (rootProcessInstance == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No s'ha trobat la instància de procés");

        afegirProcessInstances(rootProcessInstance, wProcessInstances);
        return wProcessInstances;
    }

    private void afegirProcessInstances(HistoricProcessInstance processInstance, List<WProcessInstance> llista) {

        llista.add(processInstanceMapper.toWProcessInstance(processInstance));

        var processInstanceFills = historyService.createHistoricProcessInstanceQuery()
                .superProcessInstanceId(processInstance.getId())
                .orderByProcessInstanceStartTime().desc()
                .list();
        if (processInstanceFills != null)
            processInstanceFills.forEach(p -> afegirProcessInstances(p, llista));
    }

    @Override
    @Transactional(readOnly = true)
    public WProcessInstance getProcessInstance(String processInstanceId) {
        HistoricProcessInstance processInstance = getHistoricProcessInstance(processInstanceId);
        return processInstanceMapper.toWProcessInstance(processInstance);
    }

    private HistoricProcessInstance getHistoricProcessInstance(String processInstanceId) {
        HistoricProcessInstance processInstance;
        try {
            processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No s'ha pogut obtenir la instància de procés: " + processInstanceId, ex);
        }
        if (processInstance == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Process instance: " + processInstanceId);
        }
        return processInstance;
    }

    @Override
    @Transactional(readOnly = true)
    public WProcessInstance getRootProcessInstance(String processInstanceId) {
        // Comprovam que existeixi la instància de procés
        HistoricProcessInstance processInstance = getHistoricProcessInstance(processInstanceId);

        HistoricProcessInstance superProcessInstance = null;
        do {
            superProcessInstance = historyService.createHistoricProcessInstanceQuery().subProcessInstanceId(processInstance.getId()).singleResult();
            if (superProcessInstance != null) {
                processInstance = superProcessInstance;
            }
        } while (superProcessInstance != null);

        return processInstanceMapper.toWProcessInstance(processInstance);
    }

    // Tasca utilitzada per a consulta d'expedients --> Això ha d'anar al MS d'expedients i tasques o al MS de dadesç!!
    // S'haurien d'agafar les tasques del MS d'expedietns i tasques!!
//    @Override
//    public List<String> findRootProcessInstances(String actorId, List<String> processInstanceIds, boolean nomesMeves, boolean nomesTasquesPersonals, boolean nomesTasquesGrup) {
//        // TODO: mirar per a qué s'utilitza
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
//        return null;
//    }

    @Override
    @Transactional
    public WProcessInstance startProcessInstanceById(String actorId, String processDefinitionId, Map<String, Object> variables) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, variables);

        // TODO: Indicar a Helium quina és la tasca inicial (si n'hi ha), o indicar a Helium que té una tasca inicial
        // Passar-ho com a paràmetre, de manera que aquí la podrem assignar

//        var activeTasks = taskService.createTaskQuery()
//                .processInstanceId(processInstance.getId())
//                .active()
//                .list();
//        activeTasks.stream()
//                .filter(t -> "swimlane".equalsIgnoreCase(t.getAssignee()))
//                .forEach(t -> taskService.setAssignee(t.getId(), actorId));
//        var task = taskService.createTaskQuery()
//                .processInstanceId(processInstance.getId())
//                .taskName(startTaskName)
//                .active()
//                .singleResult();
//        task.setAssignee(actorId);
        return processInstanceMapper.toWProcessInstance(processInstance);
    }

    @Override
    @Transactional
    public void signalProcessInstance(String processInstanceId, String signalName) {
        if (signalName != null) {
            var execution = runtimeService.createExecutionQuery()
                    .processInstanceId(processInstanceId)
                    .signalEventSubscriptionName(signalName)
                    .singleResult();
            if (execution == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No s'ha trobat cap execució subscrita al signal " + signalName);
            runtimeService.signalEventReceived(signalName, execution.getId());
        }
    }

    @Override
    public void messageProcessInstance(String processInstanceId, String messageName) {
        var execution = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .messageEventSubscriptionName(messageName)
                .singleResult();
        if (execution == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No s'ha trobat cap execució subscrita al missatge " + messageName);
        runtimeService.messageEventReceived(messageName, execution.getId());
    }

    @Override
    @Transactional
    public void deleteProcessInstance(String processInstanceId) {
        runtimeService.deleteProcessInstance(processInstanceId, "Motiu no definit");
        historyService.deleteHistoricProcessInstance(processInstanceId);
    }

    @Override
    @Transactional
    public void suspendProcessInstances(String[] processInstanceIds) {
        if (processInstanceIds != null) {
            Arrays.stream(processInstanceIds).forEach(pi -> runtimeService.suspendProcessInstanceById(pi));
        }
    }

    @Override
    @Transactional
    public void resumeProcessInstances(String[] processInstanceIds) {
        if (processInstanceIds != null) {
            Arrays.stream(processInstanceIds).forEach(pi -> runtimeService.activateProcessInstanceById(pi));
        }
    }

    @Override
    @Transactional
    public void changeProcessInstanceVersion(String processInstanceId, int newVersion) {

        ProcessDefinition oldProcessDefinition = null;
        ProcessDefinition newProcessDefinition = null;

        var historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        oldProcessDefinition = cacheHelper.getDefinicioProces(historicProcessInstance.getProcessDefinitionId());

        String processDefinitionId = oldProcessDefinition.getId();
        int processDefinitionVersion = oldProcessDefinition.getVersion();

        if (processDefinitionVersion != newVersion) {
            newProcessDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(oldProcessDefinition.getKey())
                    .processDefinitionVersion(newVersion)
                    .singleResult();
            if (newProcessDefinition == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No s'ha trobat la definició de procés amb versió " + newVersion);

//        BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
//        Collection<Activity> processDefinitionActivities = modelInstance.getModelElementsByType(Activity.class);

            MigrationPlan migrationPlan = runtimeService
//                .createMigrationPlan(processDefinitionId + ":" + processDefinitionVersion, processDefinitionId + ":" + newVersion)
                    .createMigrationPlan(oldProcessDefinition.getId(), newProcessDefinition.getId())
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
}
