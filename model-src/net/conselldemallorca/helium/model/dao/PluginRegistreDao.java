/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import net.conselldemallorca.helium.integracio.plugins.registre.DadesRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistrePlugin;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistrePluginException;
import net.conselldemallorca.helium.model.exception.PluginException;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

/**
 * Dao per accedir a la funcionalitat del plugin de registre
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class PluginRegistreDao {

	private RegistrePlugin registrePlugin;



	public String[] registrarEntrada(
			DadesRegistre dadesRegistre) {
		try {
			return getRegistrePlugin().registrarEntrada(dadesRegistre);
		} catch (RegistrePluginException ex) {
			logger.error("Error al crear el registre d'entrada", ex);
			throw new PluginException("Error al crear el registre d'entrada", ex);
		}
	}

	public DadesRegistre consultarEntrada(String numero, String any) {
		try {
			return getRegistrePlugin().consultarEntrada(numero, any);
		} catch (RegistrePluginException ex) {
			logger.error("Error al consultar el registre d'entrada", ex);
			throw new PluginException("Error al consultar el registre d'entrada", ex);
		}
	}

	public String[] registrarSortida(DadesRegistre dadesRegistre) {
		try {
			return getRegistrePlugin().registrarSortida(dadesRegistre);
		} catch (RegistrePluginException ex) {
			logger.error("Error al crear el registre de sortida", ex);
			throw new PluginException("Error al crear el registre de sortida", ex);
		}
	}

	public DadesRegistre consultarSortida(String numero, String any) {
		try {
			return getRegistrePlugin().consultarSortida(numero, any);
		} catch (RegistrePluginException ex) {
			logger.error("Error al consultar el registre de sortida", ex);
			throw new PluginException("Error al consultar el registre de sortida", ex);
		}
	}

	public boolean isRegistreActiu() {
		String pluginClass = GlobalProperties.getInstance().getProperty("app.registre.plugin.class");
		return (pluginClass != null && pluginClass.length() > 0);
	}



	@SuppressWarnings("unchecked")
	private RegistrePlugin getRegistrePlugin() {
		if (registrePlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.registre.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class clazz = Class.forName(pluginClass);
					registrePlugin = (RegistrePlugin)clazz.newInstance();
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
