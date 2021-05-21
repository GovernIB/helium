package es.caib.helium.integracio.excepcions.persones;

public class PersonaServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public PersonaServiceException(String message) {
		super(message);
	}

	public PersonaServiceException(String message, Throwable t) {
		super(message, t);
	}
}
