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
	
	/** Tracta el contingut i retorna una definició de procés.
	 * 
	 * @param zipInputStream
	 * @return
	 * @throws Exception
	 */
	public WProcessDefinition parse(ZipInputStream zipInputStream) throws Exception;
	
	/** Retorna la llista d'arxius de la definició de procés. */
	public Map<String, byte[]> getFiles();
	
}
