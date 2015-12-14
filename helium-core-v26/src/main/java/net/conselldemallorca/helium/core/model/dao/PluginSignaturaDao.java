/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;
import net.conselldemallorca.helium.integracio.plugins.signatura.SignaturaPlugin;
import net.conselldemallorca.helium.integracio.plugins.signatura.SignaturaPluginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * Dao per accedir a la funcionalitat del plugin de signatura
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PluginSignaturaDao {

	private SignaturaPlugin signaturaPlugin;



	public RespostaValidacioSignatura verificarSignatura(
			byte[] document,
			byte[] signatura,
			boolean obtenirDadesCertificat) throws PluginException {
		try {
			return getSignaturaPlugin().verificarSignatura(
					document,
					signatura,
					obtenirDadesCertificat);
		} catch (SignaturaPluginException ex) {
			logger.error("Error al verificar la signatura", ex);
			throw new PluginException("Error al verificar la signatura", ex);
		}
	}



	@SuppressWarnings("rawtypes")
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
