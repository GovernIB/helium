package es.caib.helium.camunda.mapper;

import es.caib.helium.camunda.model.DeploymentDto;
import es.caib.helium.camunda.model.WDeployment;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentWithDefinitions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {ProcessDefinitionMapper.class})
public interface DeploymentMapper {

    default WDeployment toWDeployment(Deployment deployment) {
        return toDeployment(deployment);
    }

    default WDeployment toWDeployment(DeploymentWithDefinitions deployment) {
        return toDeployment(deployment);
    }

    DeploymentDto toDeployment(Deployment deployment);
    @Mapping(source = "deployment.deployedProcessDefinitions", target = "processDefinitions", qualifiedByName = "toProcessDefinition")
    DeploymentDto toDeployment(DeploymentWithDefinitions deployment);

}
