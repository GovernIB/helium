package net.conselldemallorca.helium.core.api;

public interface WProcessDefinition {

	public String getDeploymentId();
	public String getId();
	public String getKey();
	public String getName();
	public int getVersion();
	public String getCategory();
	
	public Object getProcessDefinition();
	
}
