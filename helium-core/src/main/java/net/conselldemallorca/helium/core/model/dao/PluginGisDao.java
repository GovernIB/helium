/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.net.URL;
import java.util.List;

import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.gis.DadesExpedient;
import net.conselldemallorca.helium.integracio.plugins.gis.GisPlugin;
import net.conselldemallorca.helium.integracio.plugins.gis.GisPluginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus persona
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class PluginGisDao {

	private GisPlugin gisPlugin;
	private boolean pluginAvaluat = false;

	public URL getUrlVisorPlugin() { 
		try {
			return getGisPlugin().getUrlVisor();
		} catch (GisPluginException ex) {
			logger.error("Error al obtenir la url del visor", ex);
			throw new PluginException("Error al obtenir la url del visor", ex);
		}	
	}

	public String getXMLExpedientsPlugin(List<DadesExpedient> expedients) { 
		try {
			return getGisPlugin().getXMLExpedients(expedients);
		} catch (GisPluginException ex) {
			logger.error("Error al obtenir l'xml dels expedients per al visor", ex);
			throw new PluginException("Error al obtenir l'xml dels expedients per al visor", ex);
		}
	}

	@SuppressWarnings("rawtypes")
	protected GisPlugin getGisPlugin() {
		try {
			if (pluginAvaluat)
				return gisPlugin;
			String pluginClass = GlobalProperties.getInstance().getProperty("app.gis.plugin.class");
			if (pluginClass != null) {
				Class clazz = Class.forName(pluginClass);
				gisPlugin = (GisPlugin)clazz.newInstance();
			}
			pluginAvaluat = true;
			return gisPlugin;
		} catch (Exception ex) {
			throw new PluginException("No s'ha pogut crear la instància del plugin", ex);
		}
	}

	private static final Log logger = LogFactory.getLog(PluginGisDao.class);

}
