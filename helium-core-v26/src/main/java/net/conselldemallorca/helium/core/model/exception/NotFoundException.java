/**
 * 
 */
package net.conselldemallorca.helium.core.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica que un objecte no s'ha trobat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotFoundException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public NotFoundException(String msg) {
        super(msg);
    }

    public NotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

}
