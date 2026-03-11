package es.caib.helium.commons.exception;


@SuppressWarnings("serial")
public class ValidacioException extends RuntimeException {

	public ValidacioException(String message) {
		super("Error de validació. " + message);
	}
	
	public ValidacioException(
			String message,
			Throwable cause) {
		super("Error de validació. " + message, cause);
	}

}
