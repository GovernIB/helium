package es.caib.helium.integracio.excepcions.custodia;

public class CustodiaException extends Exception {

	private static final long serialVersionUID = 1L;

	public CustodiaException() {
		super();
	}

	public CustodiaException(Throwable cause) {
		super(cause);
	}

	public CustodiaException(String message) {
		super(message);
	}

	public CustodiaException(String message, Throwable cause) {
		super(message, cause);
	}
}
