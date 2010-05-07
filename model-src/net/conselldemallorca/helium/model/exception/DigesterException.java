/**
 * 
 */
package net.conselldemallorca.helium.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica que no s'ha pogut fer el digest d'una contrasenya
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DigesterException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public DigesterException(String msg) {
        super(msg);
    }

    public DigesterException(String msg, Throwable t) {
        super(msg, t);
    }

}
