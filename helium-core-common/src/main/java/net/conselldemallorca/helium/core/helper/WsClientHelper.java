/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import org.springframework.stereotype.Component;

import es.caib.emiserv.logic.intf.extern.domini.DominiHelium;
import net.conselldemallorca.helium.core.util.ws.WsClientUtils;
import net.conselldemallorca.helium.v3.core.ws.formext.IniciFormulari;

/**
 * Helper per a obtenir els clients de serveis web externs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class WsClientHelper {

	public enum WsClientAuth {
		NONE,
		BASIC,
		USERNAMETOKEN
	}

	public DominiHelium getDominiService(
			String serviceUrl,
			WsClientAuth auth,
			String username,
			String password,
			Integer timeout) {
		return (DominiHelium)WsClientUtils.getWsClientProxy(
				DominiHelium.class,
				serviceUrl,
				username,
				password,
				getAuthAsString(auth),
				false,
				false,
				true,
				timeout);
	}

	/*public IniciFormulari getIniciFormulariService(
			String serviceUrl,
			WsClientAuth auth,
			String username,
			String password) throws Exception {
		Service service = Service.create(
				new URL(serviceUrl),
				new QName(
						"http://forms.integracio.helium.conselldemallorca.net/",
						"IniciFormulariService"));
		IniciFormulari port = (IniciFormulari)service.getPort(
				new QName(
						"http://forms.integracio.helium.conselldemallorca.net/",
						"IniciFormulariPort"),
				IniciFormulari.class);
		if (username != null && !username.isEmpty()) {
			BindingProvider bp = (BindingProvider)port;
			bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		}
		return port;
	}*/
	public IniciFormulari getIniciFormulariService(
			String serviceUrl,
			WsClientAuth auth,
			String username,
			String password) {
		return (IniciFormulari)WsClientUtils.getWsClientProxy(
				IniciFormulari.class,
				serviceUrl,
				username,
				password,
				getAuthAsString(auth),
				false,
				false,
				true,
				null);
	}



	private String getAuthAsString(WsClientAuth auth) {
		String authStr;
		switch(auth) {
		case BASIC:
			authStr = "BASIC";
			break;
		case USERNAMETOKEN:
			authStr = "USERNAMETOKEN";
			break;
		default:
			authStr = "NONE";
			break;
		}
		return authStr;
	}

}
