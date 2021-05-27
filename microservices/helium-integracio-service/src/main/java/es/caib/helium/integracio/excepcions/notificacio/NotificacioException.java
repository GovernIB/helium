package es.caib.helium.integracio.excepcions.notificacio;

@SuppressWarnings("serial")
public class NotificacioException extends Exception {

	public NotificacioException() {
		super();
	}

	public NotificacioException(Throwable cause) {
		super(cause);
	}

	public NotificacioException(String message) {
		super(message);
	}

	public NotificacioException(String message, Throwable cause) {
		super(message, cause);
	}

}