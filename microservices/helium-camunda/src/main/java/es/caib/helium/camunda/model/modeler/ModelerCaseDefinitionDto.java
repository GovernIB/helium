package es.caib.helium.camunda.model.modeler;

import lombok.Data;
import org.camunda.bpm.engine.repository.CaseDefinition;

@Data
public class ModelerCaseDefinitionDto {

    protected String id;
    protected String key;
    protected String category;
    protected String name;
    protected int version;
    protected String resource;
    protected String deploymentId;
    protected String tenantId;
    protected Integer historyTimeToLive;


    public static ModelerCaseDefinitionDto fromCaseDefinition(CaseDefinition definition) {
        ModelerCaseDefinitionDto dto = new ModelerCaseDefinitionDto();
        dto.id = definition.getId();
        dto.key = definition.getKey();
        dto.category = definition.getCategory();
        dto.name = definition.getName();
        dto.version = definition.getVersion();
        dto.resource = definition.getResourceName();
        dto.deploymentId = definition.getDeploymentId();
        dto.tenantId = definition.getTenantId();
        dto.historyTimeToLive = definition.getHistoryTimeToLive();
        return dto;
    }
}
