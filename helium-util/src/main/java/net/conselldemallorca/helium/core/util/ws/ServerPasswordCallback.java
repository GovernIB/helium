/**
 * 
 */
package net.conselldemallorca.helium.core.util.ws;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

/**
 * Callback per a autenticació amb WS-Security
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServerPasswordCallback implements CallbackHandler {

	private String userName;
	private String password;

	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
		if (pc.getIdentifier().equals(userName)) {
			if (!pc.getPassword().equals(password))
				throw new IOException("usuari/contrasenya incorrectes");
		}
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
