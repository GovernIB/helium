package es.caib.helium.integracio.excepcions.notificacio;

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