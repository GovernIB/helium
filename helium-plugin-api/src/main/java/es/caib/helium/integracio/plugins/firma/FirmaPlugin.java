/**
 * 
 */
package es.caib.helium.integracio.plugins.firma;

import es.caib.helium.integracio.plugins.SistemaExternException;
import es.caib.helium.client.integracio.firma.enums.FirmaTipus;

/**
 * Interfície per accedir a la funcionalitat de firma en servidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FirmaPlugin {

	byte[] firmar(
			FirmaTipus firmaTipus,
			String motiu,
			String arxiuNom,
			byte[] arxiuContingut,
			Long tamany,
			Long entornId,
			String expedientIdentificador,
			String expedientNumero,
			Long expedientTipusId,
			String expedientTipusCodi,
			String expedientTipusNom,
			String codiDocument
			) throws SistemaExternException;

}
