/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.signatura.InfoSignatura;

/**
 * Interfície del plugin de custòdia documental
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public interface CustodiaPlugin {

	/**
	 * Afegeix una signatura a un document
	 * 
	 * @param id
	 * @param arxiuNom
	 * @param tipusDocument
	 * @param signatura
	 * @return
	 */
	public String addSignature(
			String documentId,
			String arxiuNom,
			String tipusDocument,
			byte[] signatura) throws CustodiaPluginException;

	/**
	 * Obté les signatures per a un document
	 * 
	 * @param id
	 * @return
	 */
	public List<byte[]> getSignatures(String id) throws CustodiaPluginException;

	/**
	 * Retorna l'arxiu amb les signatures en el cas de que les signatures es guardin
	 * adjuntes a dins l'arxiu que es signa
	 * @param id
	 * @return
	 * @throws CustodiaPluginException
	 */
	public byte[] getSignaturesAmbArxiu(String id) throws CustodiaPluginException;

	/**
	 * Esborra totes les signatures d'un document
	 * 
	 * @param id
	 */
	public void deleteSignatures(String id) throws CustodiaPluginException;

	/**
	 * Retorna informació de les signatures d'un document
	 * 
	 * @param id
	 */
	public List<InfoSignatura> infoSignatures(String id) throws CustodiaPluginException;

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

}
