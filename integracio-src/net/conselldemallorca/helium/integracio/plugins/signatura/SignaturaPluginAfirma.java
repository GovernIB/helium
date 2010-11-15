/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;

import java.util.List;


/**
 * Implementació del plugin de signatura amb AFirma.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class SignaturaPluginAfirma implements SignaturaPlugin {

	public InfoSignatura verificarSignatura(
			byte[] document,
			byte[] signatura) throws SignaturaPluginException {
		return null;
	}

	public List<InfoSignatura> verificarSignatura(
			byte[] documentsignat) throws SignaturaPluginException {
		return null;
	}

}
