package es.caib.helium.camunda.mapper;

import es.caib.helium.client.engine.model.DeploymentDto;
import es.caib.helium.client.engine.model.WDeployment;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentWithDefinitions;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel="spring", uses = {ProcessDefinitionMapper.class})
public abstract class DeploymentMapper {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessDefinitionMapper processDefinitionMapper;

    public WDeployment toWDeployment(Deployment deployment) {
        return toDeployment(deployment);
    }
    public WDeployment toWDeployment(DeploymentWithDefinitions deployment) {
        return toDeployment(deployment);
    }
    public WDeployment toWDeploymentWithDefinitions(Deployment deployment) {
        return toDeploymentWithDefinitions(deployment);
    }

    abstract DeploymentDto toDeployment(Deployment deployment);

    @Mapping(source = "deployedProcessDefinitions", target = "processDefinitions", qualifiedByName = "toProcessDefinition")
    abstract DeploymentDto toDeployment(DeploymentWithDefinitions deployment);

    @BeanMapping( qualifiedByName = "withDefinitions")
    abstract DeploymentDto toDeploymentWithDefinitions(Deployment deployment);

    @AfterMapping
    @Named("withDefinitions")
    void addDefinicions(Deployment deployment, @MappingTarget DeploymentDto.DeploymentDtoBuilder deploymentDto) {
        var processDefinitions = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .list();
        if (processDefinitions != null && !processDefinitions.isEmpty()) {
           deploymentDto.processDefinitions(processDefinitionMapper.toProcessDefinitions(processDefinitions));
        }
//        var caseDefinitions = repositoryService.createCaseDefinitionQuery()
//                .deploymentId(deployment.getId())
//                .list();
//        if (caseDefinitions != null && !caseDefinitions.isEmpty()) {
//            deploymentDto.setCaseDefinitions(processDefinitionMapper.toCaseDefinitions(caseDefinitions));
//        }
//        var decisionDefinitions = repositoryService.createDecisionDefinitionQuery()
//                .deploymentId(deployment.getId())
//                .list();
//        if (decisionDefinitions != null && !decisionDefinitions.isEmpty()) {
//            deploymentDto.setDecisionDefinitions(processDefinitionMapper.toDecisionDefinitions(decisionDefinitions));
//        }
//        var decisionRequirementsDefinitions = repositoryService.createDecisionRequirementsDefinitionQuery()
//                .deploymentId(deployment.getId())
//                .list();
//        if (decisionRequirementsDefinitions != null && !decisionRequirementsDefinitions.isEmpty()) {
//            deploymentDto.setDecisionRequirementsDefinitions(processDefinitionMapper.toDecisionRequirementsDefinitions(decisionRequirementsDefinitions));
//        }
    }

}
