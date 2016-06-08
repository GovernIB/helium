/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.exception;

import org.jbpm.JbpmException;

/**
 * Excepció per a validacions amb classes delegades
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumHandlerException extends JbpmException {

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
