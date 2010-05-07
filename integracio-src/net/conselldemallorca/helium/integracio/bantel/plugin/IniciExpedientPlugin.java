/**
 * 
 */
package net.conselldemallorca.helium.integracio.bantel.plugin;

import net.conselldemallorca.helium.integracio.bantel.client.wsdl.TramiteBTE;

/**
 * Interfície per a iniciar un expedient donat un tràmit de SISTRA.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public interface IniciExpedientPlugin {

	public DadesIniciExpedient obtenirDadesInici(TramiteBTE tramit);

}
