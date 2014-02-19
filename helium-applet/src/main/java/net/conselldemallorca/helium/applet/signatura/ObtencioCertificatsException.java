/**
 * 
 */
package net.conselldemallorca.helium.applet.signatura;


/**
 * Excepció que indica que l'estat no és l'adequat per a realitzar
 * l'acció
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
