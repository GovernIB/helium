/**
 * 
 */
package es.limit.helium.ws.util;

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
public class ClientPasswordCallback implements CallbackHandler {

	private String password;

	public ClientPasswordCallback() {
	}
	public ClientPasswordCallback(String password) {
		this.password = password;
	}

	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
		pc.setPassword(password);
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
