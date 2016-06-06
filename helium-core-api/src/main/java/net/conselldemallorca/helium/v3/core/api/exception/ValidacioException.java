package net.conselldemallorca.helium.v3.core.api.exception;


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
