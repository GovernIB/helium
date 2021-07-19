package es.caib.helium.camunda.mapper;

import es.caib.helium.camunda.model.*;
import org.springframework.stereotype.Component;

@Component
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

    public WTaskInstance createWTaskInstance() {
        return new TaskInstanceDto();
    }

    public WToken createWToken() {
        return new ExecutionDto();
    }

}
