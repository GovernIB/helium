/**
 * 
 */
package net.conselldemallorca.helium.core.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica que han sorgit errors durant el
 * deplegament d'una definició de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
