package es.caib.helium.integracio.excepcions.arxiu;

public class ArxiuServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ArxiuServiceException() {
		super();
	}

	public ArxiuServiceException(Throwable cause) {
		super(cause);
	}

	public ArxiuServiceException(String message) {
		super(message);
	}

	public ArxiuServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
