package es.caib.helium.camunda.mapper;

import es.caib.helium.camunda.model.DeploymentDto;
import es.caib.helium.camunda.model.ProcessDefinitionDto;
import es.caib.helium.camunda.model.WDeployment;
import es.caib.helium.camunda.model.WProcessDefinition;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentWithDefinitions;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public abstract class DeploymentMapper {

    public WDeployment toWDeployment(Deployment deployment) {
        return DeploymentDto.builder()
                .id(deployment.getId())
                .name(deployment.getName())
                .tenantId(deployment.getTenantId())
                .build();
    };

    public WDeployment toWDeployment(DeploymentWithDefinitions deployment) {
        return DeploymentDto.builder()
                .id(deployment.getId())
                .name(deployment.getName())
                .tenantId(deployment.getTenantId())
                .processDefinitions(getProcessDefinitions(deployment))
                .build();
    };

    private List<WProcessDefinition> getProcessDefinitions(DeploymentWithDefinitions deployment) {
        List<WProcessDefinition> processDefinitions = new ArrayList<>();

        deployment.getDeployedProcessDefinitions().forEach(definition -> processDefinitions.add(
                ProcessDefinitionDto.builder()
                        .deploymentId(deployment.getId())
                        .id(definition.getId())
                        .name(definition.getName())
                        .version(definition.getVersion())
                        .category(definition.getCategory())
                        .processDefinition(definition)
                        .build()
        ));

        deployment.getDeployedCaseDefinitions().forEach(definition -> processDefinitions.add(
                ProcessDefinitionDto.builder()
                        .deploymentId(deployment.getId())
                        .id(definition.getId())
                        .name(definition.getName())
                        .version(definition.getVersion())
                        .category(definition.getCategory())
                        .processDefinition(definition)
                        .build()
        ));

        deployment.getDeployedDecisionDefinitions().forEach(definition -> processDefinitions.add(
                ProcessDefinitionDto.builder()
                        .deploymentId(deployment.getId())
                        .id(definition.getId())
                        .name(definition.getName())
                        .version(definition.getVersion())
                        .category(definition.getCategory())
                        .processDefinition(definition)
                        .build()
        ));

        return processDefinitions;
    }

}
