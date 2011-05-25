/**
 * 
 */
package net.conselldemallorca.helium.core.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica un error en la cridada al plugin de custòdia
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CustodiaPluginException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public CustodiaPluginException(String msg) {
        super(msg);
    }

    public CustodiaPluginException(String msg, Throwable t) {
        super(msg, t);
    }

}
