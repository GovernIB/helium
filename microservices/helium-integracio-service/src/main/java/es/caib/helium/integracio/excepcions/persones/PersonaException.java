package es.caib.helium.integracio.excepcions.persones;

public class PersonaException extends Exception {

	private static final long serialVersionUID = 1L;

	public PersonaException(String message) {
		super(message);
	}

	public PersonaException(String message, Throwable t) {
		super(message, t);
	}
}
