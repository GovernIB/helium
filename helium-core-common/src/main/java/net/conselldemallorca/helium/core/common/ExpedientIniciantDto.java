/**
 * 
 */
package net.conselldemallorca.helium.core.common;

import net.conselldemallorca.helium.core.model.hibernate.Expedient;

/**
 * Emmagatzema l'expedient que s'està iniciant en un objecte de tipus ThreadLocal
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientIniciantDto {

	private static ThreadLocal<Expedient> expedientThreadLocal = new ThreadLocal<Expedient>();

	public static void setExpedient(Expedient expedient) {
		expedientThreadLocal.set(expedient);
	}
	public static Expedient getExpedient() {
		return expedientThreadLocal.get();
	}

}
