/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.gis;

import java.net.URL;
import java.util.List;

/**
 * Interfície per a la connexió amb un sistema GIS
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GisPlugin {

	/**
	 * Retorna la url del visor del GIS
	 * 
	 * @return la url del visor GIS
	 */
	public URL getUrlVisor() throws GisPluginException;

	/**
	 * Retorna el missatge XML a enviar al visor GIS
	 * 
	 * @param la llista dels expedients a enviar
	 * @return l'XML per enviar al visor GIS, amb la informació dels expedients
	 */
	public String getXMLExpedients(List<DadesExpedient> expedients) throws GisPluginException;
	
}
