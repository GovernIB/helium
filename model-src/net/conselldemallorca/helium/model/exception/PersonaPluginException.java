/**
 * 
 */
package net.conselldemallorca.helium.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica un error en la cridada al plugin de persones
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class PersonaPluginException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public PersonaPluginException(String msg) {
        super(msg);
    }

    public PersonaPluginException(String msg, Throwable t) {
        super(msg, t);
    }

}
