/**
 * 
 */
package net.conselldemallorca.helium.core.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica un error en la cridada a un plugin
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PluginException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public PluginException(String msg) {
        super(msg);
    }

    public PluginException(String msg, Throwable t) {
        super(msg, t);
    }

}
