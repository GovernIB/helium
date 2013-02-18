/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

/**
 * Excepció que es llança quan hi ha algun error al generar
 * un document mitjançant una plantilla.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentGenerarException extends RuntimeException {

	public DocumentGenerarException() {
		super();
	}

	public DocumentGenerarException(String message) {
		super(message);
	}
	public DocumentGenerarException(Throwable cause) {
		super(cause);
	}
	public DocumentGenerarException(String message, Throwable cause) {
		super(message, cause);
	}

}
