/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;

import java.util.List;


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

	public List<InfoSignatura> verificarSignatura(
			byte[] documentsignat) throws SignaturaPluginException;

}
