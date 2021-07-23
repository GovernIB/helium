package es.caib.helium.camunda.model.modeler;

import lombok.Data;
import org.camunda.bpm.engine.repository.CaseDefinition;
import org.camunda.bpm.engine.repository.DecisionDefinition;
import org.camunda.bpm.engine.repository.DecisionRequirementsDefinition;
import org.camunda.bpm.engine.repository.DeploymentWithDefinitions;
import org.camunda.bpm.engine.repository.ProcessDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ModelerDeploymentWithDefinitionsDto extends ModelerDeploymentDto {

    protected Map<String, ModelerProcessDefinitionDto> deployedProcessDefinitions;
    protected Map<String, ModelerCaseDefinitionDto> deployedCaseDefinitions;
    protected Map<String, ModelerDecisionDefinitionDto> deployedDecisionDefinitions;
    protected Map<String, ModelerDecisionRequirementsDefinitionDto> deployedDecisionRequirementsDefinitions;

    public static ModelerDeploymentWithDefinitionsDto fromDeployment(DeploymentWithDefinitions deployment) {
        ModelerDeploymentWithDefinitionsDto dto = new ModelerDeploymentWithDefinitionsDto();
        dto.id = deployment.getId();
        dto.name = deployment.getName();
        dto.source = deployment.getSource();
        dto.deploymentTime = deployment.getDeploymentTime();
        dto.tenantId = deployment.getTenantId();
        initDeployedResourceLists(deployment, dto);
        return dto;
    }

    private static void initDeployedResourceLists(DeploymentWithDefinitions deployment, ModelerDeploymentWithDefinitionsDto dto) {
        List<ProcessDefinition> deployedProcessDefinitions = deployment.getDeployedProcessDefinitions();
        if (deployedProcessDefinitions != null) {
            dto.deployedProcessDefinitions = new HashMap<>();
            for (ProcessDefinition processDefinition : deployedProcessDefinitions)
                dto.deployedProcessDefinitions
                        .put(processDefinition.getId(), ModelerProcessDefinitionDto.fromProcessDefinition(processDefinition));
        }
        List<CaseDefinition> deployedCaseDefinitions = deployment.getDeployedCaseDefinitions();
        if (deployedCaseDefinitions != null) {
            dto.deployedCaseDefinitions = new HashMap<>();
            for (CaseDefinition caseDefinition : deployedCaseDefinitions)
                dto.deployedCaseDefinitions
                        .put(caseDefinition.getId(), ModelerCaseDefinitionDto.fromCaseDefinition(caseDefinition));
        }
        List<DecisionDefinition> deployedDecisionDefinitions = deployment.getDeployedDecisionDefinitions();
        if (deployedDecisionDefinitions != null) {
            dto.deployedDecisionDefinitions = new HashMap<>();
            for (DecisionDefinition decisionDefinition : deployedDecisionDefinitions)
                dto.deployedDecisionDefinitions
                        .put(decisionDefinition.getId(), ModelerDecisionDefinitionDto.fromDecisionDefinition(decisionDefinition));
        }
        List<DecisionRequirementsDefinition> deployedDecisionRequirementsDefinitions = deployment.getDeployedDecisionRequirementsDefinitions();
        if (deployedDecisionRequirementsDefinitions != null) {
            dto.deployedDecisionRequirementsDefinitions = new HashMap<>();
            for (DecisionRequirementsDefinition drd : deployedDecisionRequirementsDefinitions)
                dto.deployedDecisionRequirementsDefinitions
                        .put(drd.getId(), ModelerDecisionRequirementsDefinitionDto.fromDecisionRequirementsDefinition(drd));
        }
    }
}
