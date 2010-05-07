/**
 * 
 */
package net.conselldemallorca.helium.integracio.dadesexp.service;

import java.util.List;

import javax.jws.WebService;

import net.conselldemallorca.helium.integracio.domini.ParellaCodiValor;

/**
 * Implementació de proves pel servei de notificació de dades d'un
 * expedient
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@WebService(endpointInterface = "net.conselldemallorca.helium.integracio.dadesexp.service.DadesExpedient")
public class DadesExpedientExempleImpl implements DadesExpedient {

	public void notificacio(String codi, List<ParellaCodiValor> dades) {
		System.out.println("NOTIFICACIÓ");
		System.out.println("    codi: " + codi);
		for (ParellaCodiValor parella: dades) {
			System.out.println("    dada[" + parella.getCodi() + "]: " + parella.getValor());
		}
	}

}
