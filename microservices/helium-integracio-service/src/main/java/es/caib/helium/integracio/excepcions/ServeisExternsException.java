package es.caib.helium.integracio.excepcions;

public class ServeisExternsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServeisExternsException() {
		super();
	}
	
	public ServeisExternsException(String missatge) {
		super(missatge);
	}

	public ServeisExternsException(String missatge, Throwable t) {
		super(missatge, t);
	}
	
}
