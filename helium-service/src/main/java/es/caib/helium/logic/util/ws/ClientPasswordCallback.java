/**
 * 
 */
package es.caib.helium.logic.util.ws;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

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
