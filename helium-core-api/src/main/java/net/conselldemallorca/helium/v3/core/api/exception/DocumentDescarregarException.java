/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

/**
 * Excepció que es llança quan hi ha algun error al obtenir
 * un document d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentDescarregarException extends RuntimeException {

	public DocumentDescarregarException() {
		super();
	}

	public DocumentDescarregarException(String message) {
		super(message);
	}
	public DocumentDescarregarException(Throwable cause) {
		super(cause);
	}
	public DocumentDescarregarException(String message, Throwable cause) {
		super(message, cause);
	}

}
