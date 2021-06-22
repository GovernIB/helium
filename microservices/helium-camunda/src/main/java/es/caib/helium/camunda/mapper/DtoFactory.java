package es.caib.helium.camunda.mapper;

import es.caib.helium.camunda.model.DeploymentDto;
import es.caib.helium.camunda.model.ProcessDefinitionDto;
import es.caib.helium.camunda.model.ProcessInstanceDto;
import es.caib.helium.camunda.model.WDeployment;
import es.caib.helium.camunda.model.WProcessDefinition;
import es.caib.helium.camunda.model.WProcessInstance;

public class DtoFactory {

    public WDeployment createWDeployment() {
        return new DeploymentDto();
    }

    public WProcessDefinition createWProcessDefinition() {
        return new ProcessDefinitionDto();
    }

    public WProcessInstance createWProcessInstance() {
        return new ProcessInstanceDto();
    }
}
