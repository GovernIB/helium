package es.caib.helium.camunda.service;

import es.caib.helium.camunda.model.WDeployment;
import es.caib.helium.camunda.model.WProcessDefinition;

import java.util.List;

public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    @Override
    public WProcessDefinition getProcessDefinition(String deploymentId, String processDefinitionId) {
        return null;
    }

    @Override
    public List<WProcessDefinition> getSubProcessDefinitions(String deploymentId, String processDefinitionId) {
        return null;
    }

    @Override
    public List<String> getTaskNamesFromDeployedProcessDefinition(WDeployment dpd, String processDefinitionId) {
        return null;
    }

    @Override
    public String getStartTaskName(String processDefinitionId) {
        return null;
    }

    @Override
    public WProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) {
        return null;
    }
}
