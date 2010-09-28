/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;


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

	public InfoSignatura verificarSignatura(String id) throws SignaturaPluginException {
		return null;
	}

	public boolean isVerificacioAmbId() {
		return false;
	}

}
