package es.caib.helium.camunda.model.modeler;

import lombok.Data;
import org.camunda.bpm.engine.repository.DecisionDefinition;

@Data
public class ModelerDecisionDefinitionDto {

    protected String id;
    protected String key;
    protected String category;
    protected String name;
    protected int version;
    protected String resource;
    protected String deploymentId;
    protected String tenantId;
    protected String decisionRequirementsDefinitionId;
    protected String decisionRequirementsDefinitionKey;
    protected Integer historyTimeToLive;
    protected String versionTag;

    public static ModelerDecisionDefinitionDto fromDecisionDefinition(DecisionDefinition definition) {
        ModelerDecisionDefinitionDto dto = new ModelerDecisionDefinitionDto();
        dto.id = definition.getId();
        dto.key = definition.getKey();
        dto.category = definition.getCategory();
        dto.name = definition.getName();
        dto.version = definition.getVersion();
        dto.resource = definition.getResourceName();
        dto.deploymentId = definition.getDeploymentId();
        dto.decisionRequirementsDefinitionId = definition.getDecisionRequirementsDefinitionId();
        dto.decisionRequirementsDefinitionKey = definition.getDecisionRequirementsDefinitionKey();
        dto.tenantId = definition.getTenantId();
        dto.historyTimeToLive = definition.getHistoryTimeToLive();
        dto.versionTag = definition.getVersionTag();
        return dto;
    }
}
