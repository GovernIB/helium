package es.caib.helium.commons.exception;


@SuppressWarnings("serial")
public class InUseException extends RuntimeException {

	public InUseException(String message) {
		super(message);
	}
	
	public InUseException(
			String message,
			Throwable cause) {
		super(message, cause);
	}

}
