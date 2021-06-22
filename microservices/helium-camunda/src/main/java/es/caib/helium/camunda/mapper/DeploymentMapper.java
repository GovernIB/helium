package es.caib.helium.camunda.mapper;

import es.caib.helium.camunda.model.WDeployment;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentWithDefinitions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DtoFactory.class, ProcessDefinitionMapper.class})
public interface DeploymentMapper {

    public WDeployment toWDeployment(Deployment deployment);

    @Mapping(source = "deployment.deployedProcessDefinitions", target = "processDefinitions")
    WDeployment toWDeployment(DeploymentWithDefinitions deployment);

//    private List<WProcessDefinition> getProcessDefinitions(DeploymentWithDefinitions deployment) {
//        List<WProcessDefinition> processDefinitions = new ArrayList<>();
//
//        deployment.getDeployedProcessDefinitions().forEach(definition -> processDefinitions.add(
//                ProcessDefinitionDto.builder()
//                        .deploymentId(deployment.getId())
//                        .id(definition.getId())
//                        .name(definition.getName())
//                        .version(definition.getVersion())
//                        .category(definition.getCategory())
////                        .processDefinition(definition)
//                        .build()
//        ));
//
//        deployment.getDeployedCaseDefinitions().forEach(definition -> processDefinitions.add(
//                ProcessDefinitionDto.builder()
//                        .deploymentId(deployment.getId())
//                        .id(definition.getId())
//                        .name(definition.getName())
//                        .version(definition.getVersion())
//                        .category(definition.getCategory())
////                        .processDefinition(definition)
//                        .build()
//        ));
//
//        deployment.getDeployedDecisionDefinitions().forEach(definition -> processDefinitions.add(
//                ProcessDefinitionDto.builder()
//                        .deploymentId(deployment.getId())
//                        .id(definition.getId())
//                        .name(definition.getName())
//                        .version(definition.getVersion())
//                        .category(definition.getCategory())
////                        .processDefinition(definition)
//                        .build()
//        ));
//
//        return processDefinitions;
//    }

}
