/**
 * 
 */
package net.conselldemallorca.helium.util;

import net.conselldemallorca.helium.model.hibernate.Expedient;

/**
 * Emmagatzema l'expedient que s'està iniciant en un objecte de tipus ThreadLocal
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ExpedientIniciant {

	private static ThreadLocal<Expedient> expedientThreadLocal = new ThreadLocal<Expedient>();

	public static void setExpedient(Expedient expedient) {
		expedientThreadLocal.set(expedient);
	}
	public static Expedient getExpedient() {
		return expedientThreadLocal.get();
	}

}
