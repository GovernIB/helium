/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura.applet;



/**
 * Excepció que indica que la contrasenya del certificat no és correcta
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContrasenyaIncorrectaException extends Exception {

	private static final long serialVersionUID = 1L;

	public ContrasenyaIncorrectaException(String msg) {
		super(msg);
	}

	public ContrasenyaIncorrectaException(String msg, Throwable t) {
		super(msg, t);
	}

}
