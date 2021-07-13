package es.caib.helium.client.engine.model;

import java.util.List;

public interface WDeployment {

    public String getId();
    public String getName();
    public String getTenantId();
    public String getCategory();

    public List<WProcessDefinition> getProcessDefinitions();

}
