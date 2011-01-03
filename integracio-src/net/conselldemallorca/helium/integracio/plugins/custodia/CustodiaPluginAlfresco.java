/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.gesdoc.AlfrescoUtils;
import net.conselldemallorca.helium.integracio.plugins.signatura.InfoSignatura;
import net.conselldemallorca.helium.model.dao.DaoProxy;
import net.conselldemallorca.helium.model.exception.CustodiaPluginException;
import net.conselldemallorca.helium.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.model.hibernate.DocumentStore.DocumentFont;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.alfresco.webservice.types.Reference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementació del plugin de custodia documental que guarda
 * les signatures dins alfresco. Aquest plugin està pensat per ser
 * utilitzat amb el plugin de gestió documental per Alfresco.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class CustodiaPluginAlfresco implements CustodiaPlugin {

	private static final String SIGNATURE_DATA_NAME = "signature";

	private AlfrescoUtils alfrescoUtils;



	public CustodiaPluginAlfresco() {
		alfrescoUtils = new AlfrescoUtils(
				GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.apiurl"),
				GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.user"),
				GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.pass"));
	}

	public String addSignature(
			String documentId,
			String arxiuNom,
			String tipusDocument,
			byte[] signatura) throws CustodiaPluginException {
		try {
			alfrescoUtils.startAlfrescoSession();
			DocumentStore docStore = DaoProxy.getInstance().getDocumentStoreDao().getById(new Long(documentId), false);
			if (!docStore.getFont().equals(DocumentFont.ALFRESCO))
				throw new CustodiaPluginException("El document, la signatura del qual es vol custodiar, no està guardat a dins Alfresco");
			Reference parent = alfrescoUtils.getParent(
					alfrescoUtils.getReferenceForUuid(docStore.getReferenciaFont()));
			if (isSignaturaFileAttached()) {
				try {
					alfrescoUtils.deleteContentFromParent(parent, SIGNATURE_DATA_NAME);
				} catch (IOException ex) {
					// Si no existeix es llança aquesta excepció
				}
				alfrescoUtils.createContent(
						parent,
						SIGNATURE_DATA_NAME,
						null,
						null,
						signatura);
			} else {
				int index = 0;
				boolean found;
				do {
					index++;
					found = false;
					try {
						alfrescoUtils.retrieveContentFromParent(parent, SIGNATURE_DATA_NAME + index);
						found = true;
					} catch (IOException ex) {
						// Si no existeix es llança aquesta excepció
					}
				} while (found);
				alfrescoUtils.createContent(
						parent,
						SIGNATURE_DATA_NAME + index,
						null,
						null,
						signatura);
			}
			return docStore.getReferenciaFont();
		} catch (Exception ex) {
			logger.error("No s'ha pogut custodiar la signatura", ex);
			throw new CustodiaPluginException("No s'ha pogut custodiar la signatura", ex);
		} finally {
			alfrescoUtils.endAlfrescoSession();
		}
	}

	public List<byte[]> getSignatures(String id) throws CustodiaPluginException {
		try {
			alfrescoUtils.startAlfrescoSession();
			Reference parent = alfrescoUtils.getParent(
					alfrescoUtils.getReferenceForUuid(id));
			List<byte[]> resposta = new ArrayList<byte[]>();
			if (isSignaturaFileAttached()) {
				resposta.add(
						alfrescoUtils.retrieveContentFromParent(parent, SIGNATURE_DATA_NAME));
			} else {
				int index = 0;
				boolean found;
				do {
					index++;
					found = false;
					try {
						byte[] sc = alfrescoUtils.retrieveContentFromParent(parent, SIGNATURE_DATA_NAME + index);
						resposta.add(sc);
						found = true;
					} catch (IOException ex) {
						// Si no existeix es llança aquesta excepció
					}
				} while (found);
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'han pogut obtenir les signatures", ex);
			throw new CustodiaPluginException("No s'han pogut obtenir les signatures", ex);
		} finally {
			alfrescoUtils.endAlfrescoSession();
		}
	}

	public byte[] getSignaturesAmbArxiu(String id) throws CustodiaPluginException {
		try {
			alfrescoUtils.startAlfrescoSession();
			Reference parent = alfrescoUtils.getParent(
					alfrescoUtils.getReferenceForUuid(id));
			return alfrescoUtils.retrieveContentFromParent(parent, SIGNATURE_DATA_NAME);
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir l'arxiu amb les signatures", ex);
			throw new CustodiaPluginException("No s'ha pogut obtenir l'arxiu amb les signatures", ex);
		}
	}

	public void deleteSignatures(String id) throws CustodiaPluginException {
		try {
			alfrescoUtils.startAlfrescoSession();
			Reference parent = alfrescoUtils.getParent(
					alfrescoUtils.getReferenceForUuid(id));
			if (isSignaturaFileAttached()) {
				alfrescoUtils.deleteContentFromParent(parent, SIGNATURE_DATA_NAME);
			} else {
				int index = 0;
				boolean found;
				do {
					index++;
					found = false;
					try {
						alfrescoUtils.deleteContentFromParent(parent, SIGNATURE_DATA_NAME + index);
						found = true;
						index++;
					} catch (IOException ex) {
						// Si no existeix l'arxiu es llança aquesta excepció
					}
				} while (found);
			}
		} catch (Exception ex) {
			logger.error("No s'han pogut esborrar les signatures", ex);
			throw new CustodiaPluginException("No s'han pogut esborrar les signatures", ex);
		} finally {
			alfrescoUtils.endAlfrescoSession();
		}
	}

	public List<InfoSignatura> infoSignatures(String id) throws CustodiaPluginException {
		throw new CustodiaPluginException("Aquest plugin no té implementada la funcionalitat de validació de signatures");
	}

	public boolean potObtenirInfoSignatures() {
		return false;
	}
	public boolean isValidacioImplicita() {
		return false;
	}



	private boolean isSignaturaFileAttached() {
		String fileAttached = GlobalProperties.getInstance().getProperty("app.signatura.plugin.file.attached");
		return "true".equalsIgnoreCase(fileAttached);
	}

	private static final Log logger = LogFactory.getLog(CustodiaPluginAlfresco.class);

}
