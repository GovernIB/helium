/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura.afirma;


/**
 * Excepció que indica que han sorgit errors en la petició
 * a aFirma
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AfirmaException extends Exception {

	public AfirmaException(String msg) {
	    super(msg);
	}
	public AfirmaException(String msg, Throwable t) {
	    super(msg, t);
	}

	private static final long serialVersionUID = 1L;

}
