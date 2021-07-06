/**
 * 
 */
package es.caib.helium.logic.intf.integracio.notificacio;

/**
 * Indica un error en l'accés al sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class NotificacioPluginException extends Exception {

	public NotificacioPluginException() {
		super();
	}

	public NotificacioPluginException(Throwable cause) {
		super(cause);
	}

	public NotificacioPluginException(String message) {
		super(message);
	}

	public NotificacioPluginException(String message, Throwable cause) {
		super(message, cause);
	}

}
