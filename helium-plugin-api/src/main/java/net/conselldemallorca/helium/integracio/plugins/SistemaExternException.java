/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins;

/**
 * Indica un error en l'accés al sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class SistemaExternException extends Exception {

	public SistemaExternException() {
		super();
	}

	public SistemaExternException(Throwable cause) {
		super(cause);
	}

	public SistemaExternException(String message) {
		super(message);
	}

	public SistemaExternException(String message, Throwable cause) {
		super(message, cause);
	}

}
