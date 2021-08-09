package es.caib.helium.client.engine.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(as = DeploymentDto.class)
public interface WDeployment {

    public String getId();
    public String getName();
//    public String getTenantId();
    public String getCategory();

    public List<? extends WProcessDefinition> getProcessDefinitions();

}
