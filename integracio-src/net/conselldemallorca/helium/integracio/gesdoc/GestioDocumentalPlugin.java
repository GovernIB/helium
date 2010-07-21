/**
 * 
 */
package net.conselldemallorca.helium.integracio.gesdoc;

import java.util.Date;
import java.util.List;

/**
 * Interfície per accedir al gestor documental.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public interface GestioDocumentalPlugin {

	/**
	 * Crea un document
	 * 
	 * @param expedientNumero
	 * @param expedientTipus
	 * @param documentCodi
	 * @param documentDescripcio
	 * @param documentData
	 * @param documentArxiuNom
	 * @param documentArxiuContingut
	 * @return
	 * @throws GestioDocumentalPluginException
	 */
	public String createDocument(
			String expedientNumero,
			String expedientTipus,
			String documentCodi,
			String documentDescripcio,
			Date documentData,
			String documentArxiuNom,
			byte[] documentArxiuContingut) throws GestioDocumentalPluginException;

	/**
	 * Retorna el contingut d'un document
	 * 
	 * @param documentId
	 * @return
	 * @throws GestioDocumentalPluginException
	 */
	public byte[] retrieveDocument(String documentId) throws GestioDocumentalPluginException;

	/**
	 * Esborra un document
	 * 
	 * @param documentId
	 * @throws GestioDocumentalPluginException
	 */
	public void deleteDocument(String documentId) throws GestioDocumentalPluginException;

	/**
	 * Afegeix una signatura a un document
	 * 
	 * @param documentId
	 * @param signature
	 * @throws GestioDocumentalPluginException
	 */
	public void addSignatureToDocument(
			String documentId,
			byte[] signature) throws GestioDocumentalPluginException;

	/**
	 * Obté les signatures d'un document
	 * 
	 * @param documentId
	 * @return
	 * @throws GestioDocumentalPluginException
	 */
	public List<byte[]> getSignaturesFromDocument(String documentId) throws GestioDocumentalPluginException;

	/**
	 * Canvia la vista d'un document
	 * 
	 * @param documentId
	 * @param view
	 * @throws GestioDocumentalPluginException
	 */
	public void setDocumentView(
			String documentId,
			byte[] view) throws GestioDocumentalPluginException;

	/**
	 * Obté la vista d'un document
	 * 
	 * @param documentId
	 * @return
	 * @throws GestioDocumentalPluginException
	 */
	public byte[] getDocumentView(String documentId) throws GestioDocumentalPluginException;

}
