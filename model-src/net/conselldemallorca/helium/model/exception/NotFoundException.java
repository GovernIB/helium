/**
 * 
 */
package net.conselldemallorca.helium.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica que un objecte no s'ha trobat
 * 
 * @author Josep Gayà <josepg@limit.es>
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
