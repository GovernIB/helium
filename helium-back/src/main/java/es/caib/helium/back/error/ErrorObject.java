/**
 *
 */
package es.caib.helium.back.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Objecte que conté la informació dels missatges d'error de l'API REST.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ErrorObject {

	protected final int status;
	protected final String message;
	protected Throwable throwable;
	protected String stackTrace;
	protected boolean notFound;
	protected boolean sistemaExtern;
	protected boolean accessDenied;
	protected AccessDeniedSource accessDeniedSource;

	public static enum AccessDeniedSource {
		SPRING,
		EJB
	}

}
