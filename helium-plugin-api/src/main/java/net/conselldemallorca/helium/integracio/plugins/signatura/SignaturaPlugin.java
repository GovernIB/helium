/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;



/**
 * Interfície per accedir a la funcionalitat de signatura digital en
 * servidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SignaturaPlugin {

	public RespostaValidacioSignatura verificarSignatura(
			byte[] document,
			byte[] signatura,
			boolean obtenirDadesCertificat) throws SignaturaPluginException;

}
