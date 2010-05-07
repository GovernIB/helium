/**
 * 
 */
package net.conselldemallorca.helium.integracio.dadesexp.service;

import java.util.List;

import javax.jws.WebService;

import net.conselldemallorca.helium.integracio.domini.ParellaCodiValor;

/**
 * Notificació de dades d'un expedient a un sistema extern
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@WebService
public interface DadesExpedient {

	public void notificacio(String codi, List<ParellaCodiValor> dades);

}
