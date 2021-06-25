package es.caib.helium.camunda.service;

import es.caib.helium.camunda.mapper.ProcessDefinitionMapper;
import es.caib.helium.camunda.model.WProcessDefinition;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.SubProcess;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
    public WProcessDefinition getProcessDefinition(
            String deploymentId,
            String processDefinitionId) {
        return processDefinitionMapper.toWProcessDefinition(cacheHelper.getDefinicioProces(processDefinitionId));
    }

    @Override
    @Cacheable(value = "subProcessDefinitionCache", key = "#processDefinitionId")
    public List<WProcessDefinition> getSubProcessDefinitions(String deploymentId, String processDefinitionId) {
        List<WProcessDefinition> subprocessos = new ArrayList<>();
        BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
        Collection<SubProcess> subprocesses = modelInstance.getModelElementsByType(SubProcess.class);
        if (subprocessos == null || subprocessos.isEmpty()) {
            return null;
        }
        for (SubProcess subprocess: subprocesses) {
            String key = subprocess.getId();
            subprocessos.add(processDefinitionMapper.toWProcessDefinition(
                    repositoryService.createProcessDefinitionQuery()
                            .processDefinitionKey(subprocess.getId())
                            .singleResult()));
        }
        return subprocessos;
    }

    @Override
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
    public String getStartTaskName(String processDefinitionId) {
        // TODO: Agafam la primera tasca després de l'start? Pot començar amb un fork...
        //      O miram alguna altre cosa?

        return null;
    }

    @Override
    public WProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();

        String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        return processDefinitionMapper.toWProcessDefinition(cacheHelper.getDefinicioProces(processDefinitionId));
    }



}
