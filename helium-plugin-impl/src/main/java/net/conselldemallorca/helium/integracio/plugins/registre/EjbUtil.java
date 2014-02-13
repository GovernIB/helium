/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;

/**
 * @author Josep Gayà
 *
 */
public class EjbUtil {

	private final static String HTTP_PROTOCOL = "http";	
	private final static String JNP_INITIAL_CONTEXT_FACTORY = "org.jnp.interfaces.NamingContextFactory";
	private final static String HTTP_INITIAL_CONTEXT_FACTORY = "org.jboss.naming.HttpNamingContextFactory";
	private final static String URL_PKG_PREFIXES = "org.jboss.naming:org.jnp.interfaces";



	public static InitialContext getInitialContext(
			boolean local,
			String url) throws Exception {
		Properties environment = null;
		javax.naming.InitialContext initialContext = null;
		if (local) {
			initialContext = new javax.naming.InitialContext(environment);
			return initialContext;
		}	
		environment = new Properties();
		environment.put(Context.PROVIDER_URL, url);
		environment.put(Context.INITIAL_CONTEXT_FACTORY, getInitialContextFactory(url));
		environment.put(Context.URL_PKG_PREFIXES, URL_PKG_PREFIXES);
		initialContext = new javax.naming.InitialContext(environment);
		return initialContext;
	}
	public static Object lookupHome(
			String jndi,
			boolean local,
			String url,
			Class<?> narrowTo) throws Exception {
		return lookupHome(
				jndi,
				local,
				url,
				narrowTo,
				null,
				null);
	}
	public static Object lookupHome(
			String jndi,
			boolean local,
			String url,
			Class<?> narrowTo,
			String userName,
			String password) throws Exception {
		// Obtain initial context
		InitialContext initialContext = getInitialContext(local, url);
		LoginContext lc = null;
		try {
			if (userName != null && userName.length() != 0) {
				lc = new LoginContext(
						"client-login",
						new UsernamePasswordCallbackHandler(userName, password) {
							
						});
				lc.login();
			}
			Object objRef = initialContext.lookup(jndi);
			// only narrow if necessary
			if (narrowTo.isInstance(java.rmi.Remote.class))
				return javax.rmi.PortableRemoteObject.narrow(objRef, narrowTo);
			else
				return objRef;
		} finally {
			if (lc != null)
				lc.logout();
			initialContext.close();
		}
	}
	private static String getInitialContextFactory(String url) {
		return url.startsWith( HTTP_PROTOCOL ) ? HTTP_INITIAL_CONTEXT_FACTORY : JNP_INITIAL_CONTEXT_FACTORY;
	}

	public static class UsernamePasswordCallbackHandler implements CallbackHandler {
		private String username;
		private String password;
		public UsernamePasswordCallbackHandler(String username, String password) {
			this.username = username;
			this.password = password;
		}
		public void handle(
				Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			for (int i = 0; i < callbacks.length; i++) {
				if (callbacks[i] instanceof NameCallback) {
					NameCallback ncb = (NameCallback) callbacks[i];
					ncb.setName(username);
				} else if (callbacks[i] instanceof PasswordCallback) {
					PasswordCallback pcb = (PasswordCallback) callbacks[i];
					pcb.setPassword(password.toCharArray());
				} else {
					throw new UnsupportedCallbackException(
							callbacks[i],
							"Unrecognized Callback");
				}
			}
		}
	}

}
