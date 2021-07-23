package es.caib.helium.camunda.model.modeler;

import lombok.Data;
import org.camunda.bpm.engine.repository.ProcessDefinition;

@Data
public class ModelerProcessDefinitionDto {

    protected String id;
    protected String key;
    protected String category;
    protected String description;
    protected String name;
    protected int version;
    protected String resource;
    protected String deploymentId;
    protected String diagram;
    protected boolean suspended;
    protected String tenantId;
    protected String versionTag;
    protected Integer historyTimeToLive;
    protected boolean isStartableInTasklist;

    public static ModelerProcessDefinitionDto fromProcessDefinition(ProcessDefinition definition) {
        ModelerProcessDefinitionDto dto = new ModelerProcessDefinitionDto();
        dto.id = definition.getId();
        dto.key = definition.getKey();
        dto.category = definition.getCategory();
        dto.description = definition.getDescription();
        dto.name = definition.getName();
        dto.version = definition.getVersion();
        dto.resource = definition.getResourceName();
        dto.deploymentId = definition.getDeploymentId();
        dto.diagram = definition.getDiagramResourceName();
        dto.suspended = definition.isSuspended();
        dto.tenantId = definition.getTenantId();
        dto.versionTag = definition.getVersionTag();
        dto.historyTimeToLive = definition.getHistoryTimeToLive();
        dto.isStartableInTasklist = definition.isStartableInTasklist();
        return dto;
    }

}
