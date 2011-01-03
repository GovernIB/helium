/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.custodia.CustodiaPlugin;
import net.conselldemallorca.helium.integracio.plugins.custodia.CustodiaPluginException;
import net.conselldemallorca.helium.integracio.plugins.signatura.InfoSignatura;
import net.conselldemallorca.helium.model.exception.PluginException;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

/**
 * Dao per accedir a la funcionalitat del plugin de custòdia documental
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class PluginCustodiaDao {

	private CustodiaPlugin custodiaPlugin;



	public String afegirSignatura(
			String documentId,
			String nomArxiuSignat,
			String codiTipusCustodia,
			byte[] signatura) throws PluginException {
		try {
			/*DocumentCustodia documentCustodia = new DocumentCustodia();
			documentCustodia.setId(document.getId().toString());
			documentCustodia.setEntornCodi(expedient.getEntorn().getCodi());
			documentCustodia.setExpedientTipusCodi(expedient.getTipus().getCodi());
			documentCustodia.setProcessDefinitionKey(definicioProces.getJbpmKey());
			documentCustodia.setDocumentCodi(varDocumentCodi);
			documentCustodia.setOriginalFileName(document.getArxiuNom());
			documentCustodia.setOriginalFileContent(document.getArxiuContingut());
			documentCustodia.setSignedFileName(nomArxiuSignat);
			documentCustodia.setContentType(document.getContentType());
			documentCustodia.setCustodiaCodi(document.getCustodiaCodi());
			documentCustodia.setSignature(signatura);*/
			return getCustodiaPlugin().addSignature(
					documentId,
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

	public List<InfoSignatura> infoSignatures(String documentId) throws PluginException {
		try {
			return getCustodiaPlugin().infoSignatures(documentId);
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

	public boolean isCustodiaActiu() {
		String pluginClass = GlobalProperties.getInstance().getProperty("app.custodia.plugin.class");
		return (pluginClass != null && !"".equals(pluginClass));
	}



	@SuppressWarnings("unchecked")
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
