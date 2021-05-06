package es.caib.helium.camunda.service;

import es.caib.helium.camunda.model.WProcessInstance;

import java.util.List;
import java.util.Map;

public class ProcessInstanceServiceImpl implements ProcessInstanceService {

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId) {
        return null;
    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionName(String processName) {
        return null;
    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(String processName, Long entornId) {
        return null;
    }

    @Override
    public List<WProcessInstance> getProcessInstanceTree(String rootProcessInstanceId) {
        return null;
    }

    @Override
    public WProcessInstance getProcessInstance(String processInstanceId) {
        return null;
    }

    @Override
    public WProcessInstance getRootProcessInstance(String processInstanceId) {
        return null;
    }

    @Override
    public List<String> findRootProcessInstances(String actorId, List<String> processInstanceIds, boolean nomesMeves, boolean nomesTasquesPersonals, boolean nomesTasquesGrup) {
        return null;
    }

    @Override
    public WProcessInstance startProcessInstanceById(String actorId, String processDefinitionId, Map<String, Object> variables) {
        return null;
    }

    @Override
    public void signalProcessInstance(String processInstanceId, String transitionName) {

    }

    @Override
    public void deleteProcessInstance(String processInstanceId) {

    }

    @Override
    public void suspendProcessInstances(String[] processInstanceIds) {

    }

    @Override
    public void resumeProcessInstances(String[] processInstanceIds) {

    }

    @Override
    public void changeProcessInstanceVersion(String processInstanceId, int newVersion) {

    }
}
