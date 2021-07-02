/**
 * 
 */
package es.caib.helium.logic.intf.exception;

/**
 * Excepció que indica que han sorgit errors durant l'exportació
 * d'una definició de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExportException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExportException(String msg) {
        super(msg);
    }

    public ExportException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
