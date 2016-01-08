/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

/**
 * Excepció que indica que l'expedient que s'està intentant iniciar ja existeix
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientRepetitException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExpedientRepetitException(String msg) {
        super(msg);
    }

    public ExpedientRepetitException(String msg, Throwable t) {
        super(msg, t);
    }

}
