/**
 * 
 */
package net.conselldemallorca.helium.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica un error en la cridada al backoffice de SISTRA
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class SistraBackofficeException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public SistraBackofficeException(String msg) {
        super(msg);
    }

    public SistraBackofficeException(String msg, Throwable t) {
        super(msg, t);
    }

}
