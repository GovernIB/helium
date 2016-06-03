/**
 * 
 */
package net.conselldemallorca.helium.ws.client;

import java.net.MalformedURLException;

import javax.xml.namespace.QName;

import net.conselldemallorca.helium.integracio.tramitacio.TramitacioService;

/**
 * Utilitat per a instanciar clients per al servei d'enviament
 * de contingut a bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TramitacioWsClientFactory {

	public static TramitacioService getWsClient(
			String url,
			String userName,
			String password) throws MalformedURLException {
		return new WsClientHelper<TramitacioService>().generarClientWs(
				url,
				new QName(
						"http://tramitacio.integracio.helium.conselldemallorca.net/",
						"TramitacioService"),
				userName,
				password,
				TramitacioService.class);
	}

}
