/**
 * 
 */
package net.conselldemallorca.helium.core.util;

/**
 * Emmagatzema l'entorn actual en un objecte de tipus ThreadLocal
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
