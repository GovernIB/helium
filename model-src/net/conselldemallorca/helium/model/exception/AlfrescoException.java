/**
 * 
 */
package net.conselldemallorca.helium.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica un error en la cridada al plugin de custòdia
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class AlfrescoException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public AlfrescoException(String msg) {
        super(msg);
    }

    public AlfrescoException(String msg, Throwable t) {
        super(msg, t);
    }

}
