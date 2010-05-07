/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;

import java.io.InputStream;
import java.net.URL;

/**
 * Interfície del plugin de signatura digital
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public interface SignaturaPlugin {

	public void testSigner() throws NecessitaActualitzarException;
	public String[] getCertList(String params) throws ObtencioCertificatsException;
	public Object sign(InputStream inputDocument, String certName, String password, String params) throws SignaturaException, ContrasenyaIncorrectaException;
	public URL getUrlActualitzacio();

}
