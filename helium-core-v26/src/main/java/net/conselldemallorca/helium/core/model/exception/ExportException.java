/**
 * 
 */
package net.conselldemallorca.helium.core.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica que han sorgit errors durant l'exportació
 * d'una definició de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExportException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public ExportException(String msg) {
        super(msg);
    }

    public ExportException(String msg, Throwable t) {
        super(msg, t);
    }

}
