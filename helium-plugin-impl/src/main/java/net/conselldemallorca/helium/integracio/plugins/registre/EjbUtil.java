/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.security.auth.login.LoginContext;

import es.caib.loginModule.auth.ControladorSesion;

/**
 * Utilitats per a accedir a EJBs remots.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public class EjbUtil {

	private final static String HTTP_PROTOCOL = "http";	
	private final static String JNP_INITIAL_CONTEXT_FACTORY = "org.jnp.interfaces.NamingContextFactory";
	private final static String HTTP_INITIAL_CONTEXT_FACTORY = "org.jboss.naming.HttpNamingContextFactory";
	private final static String URL_PKG_PREFIXES = "org.jboss.naming:org.jnp.interfaces";



	public Object lookupHome(
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
	public Object lookupHome(
			String jndi,
			boolean local,
			String url,
			Class<?> narrowTo,
			String userName,
			String password) throws Exception {
		LoginContext lc = null;
		InitialContext initialContext = null;
		try {
			if (userName != null && userName.length() != 0) {
				/*lc = new LoginContext(
						"client-login",
						new UsernamePasswordCallbackHandler(userName, password));
				lc.login();*/
				ControladorSesion controlador = new ControladorSesion();
				controlador.autenticar(userName, password);
			}
			initialContext = getInitialContext(local, url);
			Object objRef = initialContext.lookup(jndi);
			if (narrowTo.isInstance(java.rmi.Remote.class)) {
				return javax.rmi.PortableRemoteObject.narrow(objRef, narrowTo);
			} else {
				return objRef;
			}
		} finally {
			if (lc != null)
				lc.logout();
			if (initialContext != null)
				initialContext.close();
		}
	}

	private static InitialContext getInitialContext(
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
	private static String getInitialContextFactory(String url) {
		return url.startsWith( HTTP_PROTOCOL ) ? HTTP_INITIAL_CONTEXT_FACTORY : JNP_INITIAL_CONTEXT_FACTORY;
	}

	/*public static class UsernamePasswordCallbackHandler implements CallbackHandler {
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
	}*/

}
