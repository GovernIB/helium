/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.integracio.gesdoc.GestioDocumentalPlugin;
import net.conselldemallorca.helium.integracio.gesdoc.GestioDocumentalPluginException;
import net.conselldemallorca.helium.model.exception.PluginException;
import net.conselldemallorca.helium.model.hibernate.Expedient;
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
public class PluginGestioDocumentalDao {

	private GestioDocumentalPlugin gestioDocumentalPlugin;



	public String createDocument(
			Expedient expedient,
			String documentCodi,
			String documentDescripcio,
			Date documentData,
			String documentArxiuNom,
			byte[] documentArxiuContingut) {
		try {
			return getGestioDocumentalPlugin().createDocument(
					expedient.getNumeroDefault(),
					expedient.getEntorn().getCodi() + "#" + expedient.getTipus().getCodi(),
					documentCodi,
					documentDescripcio,
					documentData,
					documentArxiuNom,
					documentArxiuContingut);
		} catch (GestioDocumentalPluginException ex) {
			logger.error("Error al guardar el document a la gestió documental", ex);
			throw new PluginException("Error al guardar el document a la gestió documental", ex);
		}
	}

	public byte[] retrieveDocument(String documentId) {
		try {
			return getGestioDocumentalPlugin().retrieveDocument(documentId);
		} catch (GestioDocumentalPluginException ex) {
			logger.error("Error al obtenir el document de la gestió documental", ex);
			throw new PluginException("Error al obtenir el document de la gestió documental", ex);
		}
	}

	public void deleteDocument(String documentId) {
		try {
			getGestioDocumentalPlugin().deleteDocument(documentId);
		} catch (GestioDocumentalPluginException ex) {
			logger.error("Error al esborrar el document de la gestió documental", ex);
			throw new PluginException("Error al esborrar el document de la gestió documental", ex);
		}
	}

	public void setDocumentView(
			String documentId,
			byte[] view) {
		try {
			getGestioDocumentalPlugin().setDocumentView(documentId, view);
		} catch (GestioDocumentalPluginException ex) {
			logger.error("Error al introduir la vista del document a la gestió documental", ex);
			throw new PluginException("Error al introduir la vista del document a la gestió documental", ex);
		}
	}

	public byte[] getDocumentView(
			String documentId) {
		try {
			return getGestioDocumentalPlugin().getDocumentView(documentId);
		} catch (GestioDocumentalPluginException ex) {
			logger.error("Error al obtenir la vista del document de la gestió documental", ex);
			throw new PluginException("Error al obtenir la vista del document de la gestió documental", ex);
		}
	}

	public void addSignatureToDocument(
			String documentId,
			byte[] signature) {
		try {
			getGestioDocumentalPlugin().addSignatureToDocument(documentId, signature);
		} catch (GestioDocumentalPluginException ex) {
			logger.error("Error al afegir la signatura del document a la gestió documental", ex);
			throw new PluginException("Error al afegir la signatura del document a la gestió documental", ex);
		}
	}

	public List<byte[]> getSignaturesFromDocument(
			String documentId) {
		try {
			return getGestioDocumentalPlugin().getSignaturesFromDocument(documentId);
		} catch (GestioDocumentalPluginException ex) {
			logger.error("Error al obtenir la signatura del document de la gestió documental", ex);
			throw new PluginException("Error al obtenir la signatura del document de la gestió documental", ex);
		}
	}

	public boolean isGestioDocumentalActiu() {
		String pluginClass = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.class");
		if (pluginClass != null && pluginClass.length() > 0) {
			String alfrescoActiu = GlobalProperties.getInstance().getProperty("app.docstore.alfresco.actiu");
			if ("true".equals(alfrescoActiu))
				pluginClass = "net.conselldemallorca.helium.integracio.gesdoc.GestioDocumentalPluginAlfrescoCaib";
		}
		return (pluginClass != null && pluginClass.length() > 0);
	}



	@SuppressWarnings("unchecked")
	private GestioDocumentalPlugin getGestioDocumentalPlugin() throws GestioDocumentalPluginException {
		if (gestioDocumentalPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				String alfrescoActiu = GlobalProperties.getInstance().getProperty("app.docstore.alfresco.actiu");
				if ("true".equals(alfrescoActiu))
					pluginClass = "net.conselldemallorca.helium.integracio.gesdoc.GestioDocumentalPluginAlfrescoCaib";
			}
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class clazz = Class.forName(pluginClass);
					gestioDocumentalPlugin = (GestioDocumentalPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new GestioDocumentalPluginException("No s'ha pogut crear la instància del plugin de gestió documental", ex);
				}
			}
		}
		return gestioDocumentalPlugin;
	}

	private static final Log logger = LogFactory.getLog(PluginGestioDocumentalDao.class);

}
