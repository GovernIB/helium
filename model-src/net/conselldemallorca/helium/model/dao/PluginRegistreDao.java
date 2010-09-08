/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import net.conselldemallorca.helium.integracio.plugins.registre.RegistrePlugin;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistrePluginException;
import net.conselldemallorca.helium.integracio.plugins.registre.SeientRegistral;
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
			SeientRegistral dadesRegistre) {
		try {
			return getRegistrePlugin().registrarEntrada(dadesRegistre);
		} catch (RegistrePluginException ex) {
			logger.error("Error al crear el registre d'entrada", ex);
			throw new PluginException("Error al crear el registre d'entrada", ex);
		}
	}

	public SeientRegistral consultarEntrada(String oficina, String numero, String any) {
		try {
			return getRegistrePlugin().consultarEntrada(oficina, numero, any);
		} catch (RegistrePluginException ex) {
			logger.error("Error al consultar el registre d'entrada", ex);
			throw new PluginException("Error al consultar el registre d'entrada", ex);
		}
	}

	public String[] registrarSortida(SeientRegistral dadesRegistre) {
		try {
			return getRegistrePlugin().registrarSortida(dadesRegistre);
		} catch (RegistrePluginException ex) {
			logger.error("Error al crear el registre de sortida", ex);
			throw new PluginException("Error al crear el registre de sortida", ex);
		}
	}

	public SeientRegistral consultarSortida(String oficina, String numero, String any) {
		try {
			return getRegistrePlugin().consultarSortida(oficina, numero, any);
		} catch (RegistrePluginException ex) {
			logger.error("Error al consultar el registre de sortida", ex);
			throw new PluginException("Error al consultar el registre de sortida", ex);
		}
	}

	public String getNomOficina(String codi) {
		try {
			return getRegistrePlugin().getNomOficina(codi);
		} catch (RegistrePluginException ex) {
			logger.error("Error al consultar el nom de l'oficina", ex);
			throw new PluginException("Error al consultar el nom de l'oficina", ex);
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
