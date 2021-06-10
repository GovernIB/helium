package es.caib.helium.integracio.excepcions.arxiu;

public class ArxiuException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ArxiuException() {
		super();
	}

	public ArxiuException(Throwable cause) {
		super(cause);
	}

	public ArxiuException(String message) {
		super(message);
	}

	public ArxiuException(String message, Throwable cause) {
		super(message, cause);
	}
}
