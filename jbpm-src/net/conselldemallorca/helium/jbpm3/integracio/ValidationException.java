/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.jbpm.JbpmException;

/**
 * Excepció per a validacions amb classes delegades
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ValidationException extends JbpmException {

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
