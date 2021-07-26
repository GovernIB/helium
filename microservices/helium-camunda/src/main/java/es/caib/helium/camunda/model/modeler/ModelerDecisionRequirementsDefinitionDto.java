package es.caib.helium.camunda.model.modeler;

import org.camunda.bpm.engine.repository.DecisionRequirementsDefinition;

public class ModelerDecisionRequirementsDefinitionDto {

    protected String id;
    protected String key;
    protected String category;
    protected String name;
    protected int version;
    protected String resource;
    protected String deploymentId;
    protected String tenantId;

    public static ModelerDecisionRequirementsDefinitionDto fromDecisionRequirementsDefinition(DecisionRequirementsDefinition definition) {
        ModelerDecisionRequirementsDefinitionDto dto = new ModelerDecisionRequirementsDefinitionDto();
        dto.id = definition.getId();
        dto.key = definition.getKey();
        dto.category = definition.getCategory();
        dto.name = definition.getName();
        dto.version = definition.getVersion();
        dto.resource = definition.getResourceName();
        dto.deploymentId = definition.getDeploymentId();
        dto.tenantId = definition.getTenantId();
        return dto;
    }
}
