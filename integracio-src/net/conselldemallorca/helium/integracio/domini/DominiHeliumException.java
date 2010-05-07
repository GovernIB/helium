/**
 * 
 */
package net.conselldemallorca.helium.integracio.domini;


/**
 * Excepció que indica que han sorgit errors durant la
 * consulta a un domini
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DominiHeliumException extends Exception {

	private static final long serialVersionUID = 1L;

	public DominiHeliumException(String msg) {
        super(msg);
    }

    public DominiHeliumException(String msg, Throwable t) {
        super(msg, t);
    }

}
