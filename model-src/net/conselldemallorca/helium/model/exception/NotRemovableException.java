/**
 * 
 */
package net.conselldemallorca.helium.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica que un objecte no es pot desassociar
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class NotRemovableException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public NotRemovableException(String msg) {
        super(msg);
    }

    public NotRemovableException(String msg, Throwable t) {
        super(msg, t);
    }

}
