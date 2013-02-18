/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

/**
 * Excepció que es llança si hi ha algun error crindant al plugin.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class SistemaExternException extends RuntimeException {

	public SistemaExternException(String message) {
		super(message);
	}
	public SistemaExternException(String message, Throwable cause) {
		super(message, cause);
	}
	public SistemaExternException(Throwable cause) {
		super(cause);
	}

}
