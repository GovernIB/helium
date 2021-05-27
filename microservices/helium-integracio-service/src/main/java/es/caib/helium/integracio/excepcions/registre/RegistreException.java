package es.caib.helium.integracio.excepcions.registre;

public class RegistreException extends Exception {

	private static final long serialVersionUID = 1L;

	public RegistreException(String msg) {
        super(msg);
    }

    public RegistreException(String msg, Throwable t) {
        super(msg, t);
    }
}
