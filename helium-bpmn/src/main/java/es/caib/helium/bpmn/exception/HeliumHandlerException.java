/**
 *
 */
package es.caib.helium.bpmn.exception;

/**
 * Excepció per a validacions amb classes delegades
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumHandlerException extends RuntimeException {

	public HeliumHandlerException() {
		super();
	}
	public HeliumHandlerException(String message, Throwable cause) {
		super(message, cause);
	}
	public HeliumHandlerException(String message) {
		super(message);
	}
	public HeliumHandlerException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = 1L;

}
