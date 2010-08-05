/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import net.conselldemallorca.helium.model.exception.PluginException;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

/**
 * Dao per accedir a la funcionalitat del plugin de gestió documental
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class PluginRegistreDao {

	private Object registrePlugin;



	public String registrarEntrada() {
		return null;
	}

	public String consultarEntrada(String numRegistre) {
		return null;
	}

	public String registrarSortida() {
		return null;
	}

	public String consultarSortida(String numRegistre) {
		return null;
	}

	public boolean isRegistreActiu() {
		String pluginClass = GlobalProperties.getInstance().getProperty("app.registre.plugin.class");
		return (pluginClass != null && pluginClass.length() > 0);
	}



	@SuppressWarnings({ "unused", "unchecked" })
	private Object getRegistrePlugin() {
		if (registrePlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.registre.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class clazz = Class.forName(pluginClass);
					registrePlugin = (Object)clazz.newInstance();
				} catch (Exception ex) {
					logger.error("No s'ha pogut crear la instància del plugin de registre", ex);
					throw new PluginException("No s'ha pogut crear la instància del plugin de registre", ex);
				}
			}
		}
		return registrePlugin;
	}

	private static final Log logger = LogFactory.getLog(PluginRegistreDao.class);

}
