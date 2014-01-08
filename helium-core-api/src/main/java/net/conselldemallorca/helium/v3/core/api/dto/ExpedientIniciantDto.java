/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

/**
 * Emmagatzema l'expedient que s'està iniciant en un objecte de tipus ThreadLocal
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientIniciantDto {

	private static ThreadLocal<ExpedientDto> expedientThreadLocal = new ThreadLocal<ExpedientDto>();

	public static void setExpedient(ExpedientDto expedient) {
		expedientThreadLocal.set(expedient);
	}
	public static ExpedientDto getExpedient() {
		return expedientThreadLocal.get();
	}

}
