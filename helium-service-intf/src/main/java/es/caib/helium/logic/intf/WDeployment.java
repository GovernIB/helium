package es.caib.helium.logic.intf;

import java.util.List;

public interface WDeployment {

	public String getId();
	public String getName();
	public String getCategory();

//	public String getKey();
//	public int getVersion();
	
	public List<WProcessDefinition> getProcessDefinitions();
	
}
