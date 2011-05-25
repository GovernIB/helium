/**
 * 
 */
package net.conselldemallorca.helium.core.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica que l'estat no és l'adequat per a realitzar
 * l'acció
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class IllegalStateException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public IllegalStateException(String msg) {
        super(msg);
    }

    public IllegalStateException(String msg, Throwable t) {
        super(msg, t);
    }

}
