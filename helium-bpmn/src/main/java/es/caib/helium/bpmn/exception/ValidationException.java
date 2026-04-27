/**
 *
 */
package es.caib.helium.bpmn.exception;

/**
 * Excepció per a validacions amb classes delegades
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidationException extends RuntimeException {

	public ValidationException() {
		super();
	}
	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}
	public ValidationException(String message) {
		super(message);
	}
	public ValidationException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = 1L;

}
