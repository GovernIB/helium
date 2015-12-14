/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

/**
 * Excepció que es llança quan hi ha algun error el convertir
 * un document en un altre format.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentConvertirException extends RuntimeException {

	public DocumentConvertirException() {
		super();
	}

	public DocumentConvertirException(String message) {
		super(message);
	}
	public DocumentConvertirException(Throwable cause) {
		super(cause);
	}
	public DocumentConvertirException(String message, Throwable cause) {
		super(message, cause);
	}

}
