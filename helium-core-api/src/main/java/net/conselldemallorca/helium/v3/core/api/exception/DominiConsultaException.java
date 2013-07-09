/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

/**
 * Excepció que es llança quan el domini especificat no existeix.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DominiConsultaException extends RuntimeException {

	public DominiConsultaException() {
		super();
	}
	public DominiConsultaException(String message) {
		super(message);
	}
	public DominiConsultaException(Throwable cause) {
		super(cause);
	}
	public DominiConsultaException(String message, Throwable cause) {
		super(message, cause);
	}

}
