/**
 * 
 */
package es.caib.helium.integracio.plugins.firma;

import es.caib.helium.commons.exception.SistemaExternException;

/**
 * Interfície per accedir a la funcionalitat de firma en servidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FirmaPlugin {

	public FirmaResposta firmar(
			String id,
			String nom,
			String motiu,
			byte[] contingut, 
			String mime,
			String tipusDocumental) throws SistemaExternException;

}
