/**
 * 
 */
package net.conselldemallorca.helium.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica que han sorgit errors durant la
 * consulta a un domini
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DominiException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public DominiException(String msg) {
        super(msg);
    }

    public DominiException(String msg, Throwable t) {
        super(msg, t);
    }

}
