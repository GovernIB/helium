/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;


/**
 * Excepció que indica que l'estat no és l'adequat per a realitzar
 * l'acció
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ObtencioCertificatsException extends Exception {

	private static final long serialVersionUID = 1L;

	public ObtencioCertificatsException(String msg) {
        super(msg);
    }

    public ObtencioCertificatsException(String msg, Throwable t) {
        super(msg, t);
    }

}
