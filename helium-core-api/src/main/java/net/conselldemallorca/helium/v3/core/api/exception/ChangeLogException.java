/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

/**
 * Excepció que es llança quan el termini especificat no existeix.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ChangeLogException extends RuntimeException {

	public ChangeLogException(String message) {
		super(message);
	}
	public ChangeLogException(String message, Throwable cause) {
		super(message, cause);
	}
	public ChangeLogException(Throwable cause) {
		super(cause);
	}
}
