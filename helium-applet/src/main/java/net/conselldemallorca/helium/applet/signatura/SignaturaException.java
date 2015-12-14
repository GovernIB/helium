/**
 * 
 */
package net.conselldemallorca.helium.applet.signatura;


/**
 * Excepció que indica un error en la signatura d'un document
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SignaturaException extends Exception {

	private static final long serialVersionUID = 1L;

	public SignaturaException(String msg) {
        super(msg);
    }

    public SignaturaException(String msg, Throwable t) {
        super(msg, t);
    }

}
