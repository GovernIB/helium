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
public class TramitacioValidacioException extends RuntimeException {

	public TramitacioValidacioException() {
		super();
	}

	public TramitacioValidacioException(String message, Throwable cause) {
		super(message, cause);
	}

	public TramitacioValidacioException(String message) {
		super(message);
	}

	public TramitacioValidacioException(Throwable cause) {
		super(cause);
	}
	
}
