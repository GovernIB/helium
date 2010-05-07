/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;


/**
 * Excepció que indica un error en la signatura d'un document
 * 
 * @author Josep Gayà <josepg@limit.es>
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
