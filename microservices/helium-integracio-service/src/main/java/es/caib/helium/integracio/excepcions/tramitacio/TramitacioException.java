package es.caib.helium.integracio.excepcions.tramitacio;

public class TramitacioException extends Exception {


	private static final long serialVersionUID = 1L;

	public TramitacioException(String msg) {
        super(msg);
    }

    public TramitacioException(String msg, Throwable t) {
        super(msg, t);
    }
}
