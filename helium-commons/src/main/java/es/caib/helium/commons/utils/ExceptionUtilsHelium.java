package es.caib.helium.commons.utils;

/**
 * Classe amb mètodes comuns per tractar excepcions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExceptionUtilsHelium {

	/** Comprova si l'excepció està causada per un tipus d'excepció.
	 * 
	 * @param exception Excepció a comprovar.
	 * @param exceptionClass Classe per veure si extén o implementa.
	 * 
	 * @return True si l'excepció implementa, extén o està causada.
	 */
	public static boolean isCausedBy(Throwable exception, Class<? extends Throwable> exceptionClass) {
		Throwable current = exception;
		while (current != null) {
			if (exceptionClass.isAssignableFrom(current.getClass())) {
				return true;
			}

			current = current.getCause();
		}
		return false;
	}

}
