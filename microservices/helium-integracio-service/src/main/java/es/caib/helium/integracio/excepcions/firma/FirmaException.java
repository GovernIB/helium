package es.caib.helium.integracio.excepcions.firma;

public class FirmaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FirmaException() {
		super();
	}

	public FirmaException(Throwable cause) {
		super(cause);
	}

	public FirmaException(String message) {
		super(message);
	}

	public FirmaException(String message, Throwable cause) {
		super(message, cause);
	}
}
