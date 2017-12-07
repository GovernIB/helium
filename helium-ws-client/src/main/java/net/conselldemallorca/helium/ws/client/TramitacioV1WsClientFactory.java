/**
 * 
 */
package net.conselldemallorca.helium.ws.client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import net.conselldemallorca.helium.ws.tramitacio.v1.TramitacioService;


/**
 * Utilitat per a instanciar clients per al servei d'enviament
 * de contingut a bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TramitacioV1WsClientFactory {

	public static TramitacioService getWsClient(
			String url,
			String userName,
			String password) throws MalformedURLException {
		URL wsdlResource = TramitacioV1WsClientFactory.class.getClassLoader().getResource(
        		"net/conselldemallorca/helium/ws/client/TramitacioV1.wsdl");
		return new WsClientHelper<TramitacioService>().generarClientWs(
				wsdlResource,
				url,
				new QName(
						"http://conselldemallorca.net/helium/ws/tramitacio/v1",
						"TramitacioServiceImplService"),
				userName,
				password,
				TramitacioService.class);
	}

}
