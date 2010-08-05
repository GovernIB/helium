/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.custodia.CustodiaPlugin;
import net.conselldemallorca.helium.integracio.plugins.custodia.DocumentCustodia;
import net.conselldemallorca.helium.integracio.plugins.custodia.SignaturaInfo;
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.exception.CustodiaPluginException;
import net.conselldemallorca.helium.model.exception.PluginException;
import net.conselldemallorca.helium.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.model.hibernate.Expedient;
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



	public String afegirDocumentAmbSignatura(
			Expedient expedient,
			DefinicioProces definicioProces,
			String varDocumentCodi,
			DocumentDto document,
			String nomArxiuSignat,
			Object signatura) throws CustodiaPluginException {
		try {
			DocumentCustodia documentCustodia = new DocumentCustodia();
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
			documentCustodia.setSignature(signatura);
			return getCustodiaPlugin().addSignedDocument(
					documentCustodia);
		} catch (Exception ex) {
			throw new CustodiaPluginException("No s'ha pogut guardar el document a la custòdia", ex);
		}
	}

	public void deleteDocumentAmbSignatura(String id) throws CustodiaPluginException {
		try {
			getCustodiaPlugin().deleteSignedDocument(id);
		} catch (Exception ex) {
			throw new CustodiaPluginException("No s'ha pogut esborrar el document de la custòdia");
		}
	}

	public DocumentDto getDocumentAmbSignatura(String id) throws CustodiaPluginException {
		try {
			DocumentCustodia documentCustodia = getCustodiaPlugin().getSignedDocument(id);
			DocumentDto resposta = new DocumentDto();
			resposta.setArxiuNom(documentCustodia.getSignedFileName());
			resposta.setArxiuContingut(documentCustodia.getSignedFileContent());
			return resposta;
		} catch (Exception ex) {
			throw new CustodiaPluginException("No s'ha pogut obtenir el document de la custòdia");
		}
	}

	public List<SignaturaInfo> verificarDocumentAmbSignatura(String id) throws CustodiaPluginException {
		try {
			return getCustodiaPlugin().verifyDocument(id);
		} catch (Exception ex) {
			throw new CustodiaPluginException("No s'ha pogut verificar el document de la custòdia");
		}
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
