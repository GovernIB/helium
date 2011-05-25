/**
 * 
 */
package net.conselldemallorca.helium.core.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció en la inicialització
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InitializationException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public InitializationException(String msg) {
        super(msg);
    }

    public InitializationException(String msg, Throwable t) {
        super(msg, t);
    }

}
