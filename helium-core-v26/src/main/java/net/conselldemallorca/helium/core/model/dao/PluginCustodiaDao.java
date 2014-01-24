/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.custodia.CustodiaPlugin;
import net.conselldemallorca.helium.integracio.plugins.custodia.CustodiaPluginException;
import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * Dao per accedir a la funcionalitat del plugin de custòdia documental
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PluginCustodiaDao {

	private CustodiaPlugin custodiaPlugin;



	public String afegirSignatura(
			Long documentId,
			String gesdocId,
			String nomArxiuSignat,
			String codiTipusCustodia,
			byte[] signatura) throws PluginException {
		try {
			return getCustodiaPlugin().addSignature(
					documentId.toString(),
					gesdocId,
					nomArxiuSignat,
					codiTipusCustodia,
					signatura);
		} catch (CustodiaPluginException ex) {
			logger.error("Error al guardar la signatura dins la custòdia", ex);
			throw new PluginException("Error al guardar la signatura dins la custòdia", ex);
		}
	}

	public List<byte[]> obtenirSignatures(String documentId) throws PluginException {
		try {
			return getCustodiaPlugin().getSignatures(documentId);
		} catch (CustodiaPluginException ex) {
			logger.error("Error al obtenir les signatures de la custòdia", ex);
			throw new PluginException("Error al obtenir les signatures de la custòdia", ex);
		}
	}

	public byte[] obtenirSignaturesAmbArxiu(String documentId) throws PluginException {
		try {
			return getCustodiaPlugin().getSignaturesAmbArxiu(documentId);
		} catch (CustodiaPluginException ex) {
			logger.error("Error al obtenir l'arxiu amb les signatures de la custòdia", ex);
			throw new PluginException("Error al obtenir l'arxiu amb les signatures de la custòdia", ex);
		}
	}

	public void esborrarSignatures(String documentId) throws PluginException {
		try {
			getCustodiaPlugin().deleteSignatures(documentId);
		} catch (CustodiaPluginException ex) {
			logger.error("Error al esborrar les signatures de la custòdia", ex);
			throw new PluginException("Error al esborrar les signatures de la custòdia", ex);
		}
	}

	public List<RespostaValidacioSignatura> dadesValidacioSignatura(String documentId) throws PluginException {
		try {
			return getCustodiaPlugin().dadesValidacioSignatura(documentId);
		} catch (CustodiaPluginException ex) {
			logger.error("Error al obtenir informació de les signatures de la custòdia", ex);
			throw new PluginException("Error al obtenir informació de les signatures de la custòdia", ex);
		}
	}

	public boolean potObtenirInfoSignatures() {
		return getCustodiaPlugin().potObtenirInfoSignatures();
	}

	public boolean isValidacioImplicita() {
		return getCustodiaPlugin().isValidacioImplicita();
	}

	public String getUrlComprovacioSignatura(String id) throws PluginException {
		try {
			return getCustodiaPlugin().getUrlComprovacioSignatura(id);
		} catch (CustodiaPluginException ex) {
			logger.error("Error al generar la url de comprovació de document", ex);
			throw new PluginException("Error al generar la url de comprovació de document", ex);
		}
	}

	public boolean isCustodiaActiu() {
		String pluginClass = GlobalProperties.getInstance().getProperty("app.custodia.plugin.class");
		return (pluginClass != null && !"".equals(pluginClass));
	}



	@SuppressWarnings("rawtypes")
	private CustodiaPlugin getCustodiaPlugin() {
		if (custodiaPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.custodia.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class clazz = Class.forName(pluginClass);
					custodiaPlugin = (CustodiaPlugin)clazz.newInstance();
				} catch (Exception ex) {
					logger.error("No s'ha pogut crear la instància del plugin de custòdia", ex);
					throw new PluginException("No s'ha pogut crear la instància del plugin de custòdia", ex);
				}
			}
		}
		return custodiaPlugin;
	}

	private static final Log logger = LogFactory.getLog(PluginCustodiaDao.class);

}
