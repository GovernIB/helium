/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.gesdoc;

import java.util.Date;

/**
 * Interfície per accedir al gestor documental.
 * 
 * @author Limit Tecnologies <limit@limit.es>
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

}
