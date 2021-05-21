package es.caib.helium.integracio.excepcions.portafirmes;

public class PortaFirmesException extends Exception {

	private static final long serialVersionUID = 1L;

	public PortaFirmesException(String message) {
		super(message);
	}

	public PortaFirmesException(String message, Throwable t) {
		super(message, t);
	}
}
