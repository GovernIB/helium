/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

/**
 * Excepció que es llança quan la tasca especificada no existeix.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TascaNotFoundException() {
		super();
	}

	public TascaNotFoundException(String msg) {
        super(msg);
    }

    public TascaNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

}
