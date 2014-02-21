/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
			logger.info(">>> EJBUTIL 0");
			if (userName != null && userName.length() != 0) {
				logger.info(">>> EJBUTIL 1");
				/* Realment serveix de res això */
				URL securityConfURL = getClass().getClassLoader().getResource("security.conf");
				System.setProperty(
						"java.security.auth.login.config",
						securityConfURL.toString());
				@SuppressWarnings({ "restriction", "unused" })
				Configuration config = new com.sun.security.auth.login.ConfigFile();
				/* /Realment serveix de res això */
				logger.info(">>> EJBUTIL 2");
				lc = new LoginContext(
						"client-login",
						new UsernamePasswordCallbackHandler(userName, password));
				logger.info(">>> EJBUTIL 3");
				lc.login();
				logger.info(">>> EJBUTIL 4");
			}
			logger.info(">>> EJBUTIL 5");
			initialContext = getInitialContext(local, url);
			logger.info(">>> EJBUTIL 6");
			Object objRef = initialContext.lookup(jndi);
			logger.info(">>> EJBUTIL 7");
			if (narrowTo.isInstance(java.rmi.Remote.class)) {
				logger.info(">>> EJBUTIL 8");
				return javax.rmi.PortableRemoteObject.narrow(objRef, narrowTo);
			} else {
				logger.info(">>> EJBUTIL 9");
				return objRef;
			}
		} finally {
			logger.info(">>> EJBUTIL 10");
			if (lc != null)
				lc.logout();
			logger.info(">>> EJBUTIL 11");
			if (initialContext != null)
				initialContext.close();
			logger.info(">>> EJBUTIL 12");
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

	private static final Log logger = LogFactory.getLog(EjbUtil.class);

}
