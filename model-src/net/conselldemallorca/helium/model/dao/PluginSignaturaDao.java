/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.signatura.InfoSignatura;
import net.conselldemallorca.helium.integracio.plugins.signatura.SignaturaPlugin;
import net.conselldemallorca.helium.integracio.plugins.signatura.SignaturaPluginException;
import net.conselldemallorca.helium.model.exception.PluginException;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

/**
 * Dao per accedir a la funcionalitat del plugin de signatura
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class PluginSignaturaDao {

	private SignaturaPlugin signaturaPlugin;



	public InfoSignatura verificarSignatura(
			byte[] document,
			byte[] signatura) throws PluginException {
		try {
			return getSignaturaPlugin().verificarSignatura(document, signatura);
		} catch (SignaturaPluginException ex) {
			logger.error("Error al verificar la signatura", ex);
			throw new PluginException("Error al verificar la signatura", ex);
		}
	}
	public List<InfoSignatura> verificarSignatura(
			byte[] documentSignat) throws PluginException {
		try {
			return getSignaturaPlugin().verificarSignatura(documentSignat);
		} catch (SignaturaPluginException ex) {
			logger.error("Error al verificar la signatura", ex);
			throw new PluginException("Error al verificar la signatura", ex);
		}
	}



	@SuppressWarnings("unchecked")
	private SignaturaPlugin getSignaturaPlugin() {
		if (signaturaPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.signatura.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class clazz = Class.forName(pluginClass);
					signaturaPlugin = (SignaturaPlugin)clazz.newInstance();
				} catch (Exception ex) {
					logger.error("No s'ha pogut crear la instància del plugin de signatura", ex);
					throw new PluginException("No s'ha pogut crear la instància del plugin de signatura", ex);
				}
			}
		}
		return signaturaPlugin;
	}

	private static final Log logger = LogFactory.getLog(PluginSignaturaDao.class);

}
