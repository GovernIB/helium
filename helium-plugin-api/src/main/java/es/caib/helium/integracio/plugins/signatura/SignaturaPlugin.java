/**
 * 
 */
package es.caib.helium.integracio.plugins.signatura;
import es.caib.helium.client.model.RespostaValidacioSignatura;


/**
 * Interfície per accedir a la funcionalitat de signatura digital en
 * servidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SignaturaPlugin {

	RespostaValidacioSignatura verificarSignatura(
			byte[] document,
			byte[] signatura,
			boolean obtenirDadesCertificat) throws SignaturaPluginException;

}
