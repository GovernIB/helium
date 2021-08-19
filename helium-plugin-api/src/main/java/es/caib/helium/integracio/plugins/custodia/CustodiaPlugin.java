/**
 * 
 */
package es.caib.helium.integracio.plugins.custodia;

import es.caib.helium.client.model.RespostaValidacioSignatura;
import java.util.List;

/**
 * Interfície del plugin de custòdia documental
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CustodiaPlugin {

	/**
	 * Afegeix una signatura a un document
	 * 
	 * @param id
	 * @param gesdocId
	 * @param arxiuNom
	 * @param tipusDocument
	 * @param signatura
	 * @return
	 */
	public String addSignature(
			String id,
			String gesdocId,
			String arxiuNom,
			String tipusDocument,
			byte[] signatura,
			Long entornId) throws CustodiaPluginException;

	/**
	 * Obté les signatures per a un document
	 * 
	 * @param id
	 * @return
	 */
	public List<byte[]> getSignatures(String id, Long entornId) throws CustodiaPluginException;

	/**
	 * Retorna l'arxiu amb les signatures en el cas de que les signatures es guardin
	 * adjuntes a dins l'arxiu que es signa
	 * @param id
	 * @return
	 * @throws CustodiaPluginException
	 */
	public byte[] getSignaturesAmbArxiu(String id, Long entornId) throws CustodiaPluginException;

	/**
	 * Esborra totes les signatures d'un document
	 * 
	 * @param id
	 */
	public void deleteSignatures(String id, Long entornId) throws CustodiaPluginException;

	/**
	 * Retorna informació de les signatures d'un document
	 * 
	 * @param id
	 */
	public List<RespostaValidacioSignatura> dadesValidacioSignatura(String id, Long entornId) throws CustodiaPluginException;

	/**
	 * Indica si la implementació del plugin és capaç de retornar informació
	 * de les signatures.
	 */
	public boolean potObtenirInfoSignatures();

	/**
	 * Indica si la validació de les signatures és implícita a l'hora
	 * de guardar la signatura o s'ha de fer expressament.
	 */
	public boolean isValidacioImplicita();

	/**
	 * Retorna la url de verificació de documents signats. Si retorna null
	 * el plugin no suporta aquesta característica.
	 * 
	 * @return
	 * @throws CustodiaPluginException
	 */
	public String getUrlComprovacioSignatura(String id, Long entornId) throws CustodiaPluginException;

}
