package net.conselldemallorca.helium.api.service;

import java.util.Map;
import java.util.zip.ZipInputStream;

public interface WProcessDefinition {

	public String getDeploymentId();
	public String getId();
	public String getKey();
	public String getName();
	public int getVersion();
	public String getCategory();
	
//	public Object getProcessDefinition();
//	public WProcessDefinition parse(ZipInputStream zipInputStream) throws Exception;
//	public Map<String, byte[]> getFiles();
	
}
