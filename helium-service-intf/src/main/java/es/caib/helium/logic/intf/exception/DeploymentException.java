/**
 * 
 */
package es.caib.helium.logic.intf.exception;

/**
 * Excepció que indica que han sorgit errors durant el
 * deplegament d'una definició de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DeploymentException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DeploymentException(String msg) {
        super(msg);
    }

    public DeploymentException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
