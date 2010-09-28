/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;


/**
 * Interfície per accedir a la funcionalitat de signatura digital en
 * servidor.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public interface SignaturaPlugin {

	public InfoSignatura verificarSignatura(
			byte[] document,
			byte[] signatura) throws SignaturaPluginException;

	public boolean isVerificacioAmbId();

}
