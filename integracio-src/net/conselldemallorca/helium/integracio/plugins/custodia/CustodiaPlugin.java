/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

import java.util.List;

/**
 * Interfície del plugin de custòdia documental
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public interface CustodiaPlugin {

	public String addSignedDocument(DocumentCustodia document);
	public void deleteSignedDocument(String id);
	public DocumentCustodia getSignedDocument(String id);
	public List<SignaturaInfo> verifyDocument(String id);

}
