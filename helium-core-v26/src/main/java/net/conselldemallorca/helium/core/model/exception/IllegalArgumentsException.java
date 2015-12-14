/**
 * 
 */
package net.conselldemallorca.helium.core.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica que els paràmetres rebuts no són correctes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class IllegalArgumentsException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public IllegalArgumentsException(String msg) {
        super(msg);
    }

    public IllegalArgumentsException(String msg, Throwable t) {
        super(msg, t);
    }

}
