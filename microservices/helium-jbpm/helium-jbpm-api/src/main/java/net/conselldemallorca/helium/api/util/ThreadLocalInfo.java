/**
 * 
 */
package net.conselldemallorca.helium.api.util;

/**
 * Emmagatzema informació diversa a dins el thread local.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ThreadLocalInfo {

	private static ThreadLocal<String> authorizationToken = new ThreadLocal<String>();

	public static void setAuthorizationToken(String token) {
		authorizationToken.set(token);
	}
	public static String getAuthorizationToken() {
		return authorizationToken.get();
	}
}
