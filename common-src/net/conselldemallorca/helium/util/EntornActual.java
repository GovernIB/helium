/**
 * 
 */
package net.conselldemallorca.helium.util;

/**
 * Emmagatzema el domini actual en un objecte de tipus ThreadLocal
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class EntornActual {

	private static ThreadLocal<Long> entornIdThreadLocal = new ThreadLocal<Long>();

	public static void setEntornId(Long entornId) {
		entornIdThreadLocal.set(entornId);
	}
	public static Long getEntornId() {
		return entornIdThreadLocal.get();
	}

}
