/**
 * 
 */
package es.caib.helium.logic.helper;

import es.caib.helium.logic.intf.extern.domini.DominiHelium;
import es.caib.helium.logic.intf.extern.formulari.IniciFormulari;
import es.caib.helium.logic.util.ws.WsClientUtils;
import org.springframework.stereotype.Component;

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
