/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.firma;

import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;

/**
 * Interfície per accedir a la funcionalitat de firma en servidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FirmaPlugin {

	public byte[] firmar(
			FirmaTipus firmaTipus,
			String motiu,
			String arxiuNom,
			byte[] arxiuContingut) throws SistemaExternException;

}
