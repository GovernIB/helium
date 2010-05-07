/**
 * 
 */
package net.conselldemallorca.helium.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica que han sorgit errors durant el
 * deplegament d'una definició de procés
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DeploymentException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public DeploymentException(String msg) {
        super(msg);
    }

    public DeploymentException(String msg, Throwable t) {
        super(msg, t);
    }

}
