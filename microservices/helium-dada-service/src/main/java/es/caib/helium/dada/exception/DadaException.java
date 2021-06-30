package es.caib.helium.dada.exception;

@SuppressWarnings("serial")
public class DadaException extends Exception {

	public DadaException() {
		super();
	}

	public DadaException(Throwable cause) {
		super(cause);
	}

	public DadaException(String message) {
		super(message);
	}

	public DadaException(String message, Throwable cause) {
		super(message, cause);
	}
}
