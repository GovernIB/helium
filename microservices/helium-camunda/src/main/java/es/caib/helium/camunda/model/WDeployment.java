package es.caib.helium.camunda.model;

import java.util.List;

public interface WDeployment {

    public String getId();
    public String getName();
    public String getTenantId();
    public String getCategory();

    public List<? extends WProcessDefinition> getProcessDefinitions();

}
