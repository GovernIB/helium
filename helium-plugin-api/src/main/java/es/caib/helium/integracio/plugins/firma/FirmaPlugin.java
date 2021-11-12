/**
 * 
 */
package es.caib.helium.integracio.plugins.firma;

import es.caib.helium.client.integracio.firma.model.FirmaResposta;
import es.caib.helium.integracio.plugins.SistemaExternException;

/**
 * Interfície per accedir a la funcionalitat de firma en servidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FirmaPlugin {

	FirmaResposta firmar(
			String id,
			String nom,
			String motiu,
			byte[] contingut, 
			String mime,
			String tipusDocumental,
			
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
