/**
 * 
 */
package net.conselldemallorca.helium.ws.client;

import java.net.MalformedURLException;

import javax.xml.namespace.QName;

import net.conselldemallorca.helium.integracio.forms.GuardarFormulari;

/**
 * Utilitat per a instanciar clients per al servei d'enviament
 * de contingut a bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GuardarFormulariWsClientFactory {

	public static GuardarFormulari getWsClient(
			String url,
			String userName,
			String password) throws MalformedURLException {
		return new WsClientHelper<GuardarFormulari>().generarClientWs(
				url,
				new QName(
						"http://forms.integracio.helium.conselldemallorca.net/",
						"GuardarFormulariImplService"),
				userName,
				password,
				GuardarFormulari.class);
	}

}
