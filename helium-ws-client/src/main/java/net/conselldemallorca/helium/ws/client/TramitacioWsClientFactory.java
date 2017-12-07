/**
 * 
 */
package net.conselldemallorca.helium.ws.client;

import java.net.MalformedURLException;
import java.net.URL;

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
			String url) throws MalformedURLException {
		URL wsdlResource = TramitacioV1WsClientFactory.class.getClassLoader().getResource(
        		"net/conselldemallorca/helium/ws/client/Tramitacio.wsdl");
		return new WsClientHelper<TramitacioService>().generarClientWs(
				wsdlResource,
				url,
				new QName(
						"http://tramitacio.integracio.helium.conselldemallorca.net/",
						"TramitacioService"),
				null,
				null,
				TramitacioService.class);
	}

}
