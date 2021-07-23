package es.caib.helium.camunda.service;

import es.caib.helium.camunda.helper.CacheHelper;
import es.caib.helium.camunda.mapper.ProcessDefinitionMapper;
import es.caib.helium.camunda.model.WProcessDefinition;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.CallActivity;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    private final RepositoryService repositoryService;
    private final ProcessDefinitionMapper processDefinitionMapper;
    private final HistoryService historyService;
    private final CacheHelper cacheHelper;

    // TODO: CACHE!!

    @Override
    @Transactional(readOnly = true)
    public List<? extends WProcessDefinition> getProcessDefinitions(String deploymentId) {
        var processDefinitions = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .list();
        if (processDefinitions == null || processDefinitions.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No s'han trobat definicions de procés per al desplegament " + deploymentId);
        return processDefinitionMapper.toWProcessDefinitions(processDefinitions);

    }

    @Override
    @Transactional(readOnly = true)
    public WProcessDefinition getProcessDefinition(
            String deploymentId,
            String processDefinitionId) {
        return processDefinitionMapper.toWProcessDefinition(cacheHelper.getDefinicioProces(processDefinitionId));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "subProcessDefinitionCache", key = "#processDefinitionId")
    public List<WProcessDefinition> getSubProcessDefinitions(String deploymentId, String processDefinitionId) {
        List<WProcessDefinition> subprocessos = new ArrayList<>();
        BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
//        var subprocesses = modelInstance.getModelElementsByType(SubProcess.class);
        var callActivities = modelInstance.getModelElementsByType(CallActivity.class);

//        if (subprocesses != null) {
//            subprocesses.forEach(s -> subprocessos.add(
//                    processDefinitionMapper.toWProcessDefinition(
//                            repositoryService.createProcessDefinitionQuery()
//                                    .processDefinitionKey(s.getId())
//                                    .singleResult())));
//        }
        if (callActivities == null || callActivities.isEmpty()) {
            return null;
        }

        callActivities.forEach(c -> subprocessos.add(
                processDefinitionMapper.toWProcessDefinition(
                        repositoryService.createProcessDefinitionQuery()
                                .processDefinitionKey(c.getCalledElement())
                                .deploymentId(deploymentId)
//                                .latestVersion()
                                .singleResult())));
        return subprocessos;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "processDefinitionTasksCache", key = "#processDefinitionId")
    public List<String> getTaskNamesFromDeployedProcessDefinition(String deploymentId, String processDefinitionId) {
        List<String> tasques = new ArrayList<>();
        BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
        Collection<UserTask> userTasks = modelInstance.getModelElementsByType(UserTask.class);
        if (userTasks == null || userTasks.isEmpty()) {
            return null;
        }
        for (UserTask userTask: userTasks) {
            tasques.add(userTask.getName());
        }
        return tasques;
    }

    @Override
    @Transactional(readOnly = true)
    public String getStartTaskName(String processDefinitionId) {
        // TODO: Agafam la primera tasca després de l'start? Pot començar amb un fork...

//        String startTaskName = null;
//        BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
//        var startEvents = modelInstance.getModelElementsByType(StartEvent.class);
//        if (startEvents != null && startEvents.size() == 1) {
//            var startEvent = startEvents.stream().findFirst().get();
//            if (startEvent.getOutgoing() != null && startEvent.getOutgoing().size() == 1) {
//                var outgoingFlowId = startEvent.getOutgoing().stream().findFirst().get();
//                startTaskName = outgoingFlowId.getTarget().getName();
//            }
//        }
//        return startTaskName;
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public WProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();

        String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        return processDefinitionMapper.toWProcessDefinition(cacheHelper.getDefinicioProces(processDefinitionId));
    }


}
