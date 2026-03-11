package es.caib.helium.logic.intf.dto.engine;


import java.util.List;

public interface WDeployment {

    public String getId();
    public String getName();
    public String getCategory();

    public List<? extends WProcessDefinition> getProcessDefinitions();

}
