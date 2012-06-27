/**
 * 
 */
package net.conselldemallorca.helium.ws.tramitacio.v1;


/**
 * Excepció que indica que han sorgit errors durant la
 * tramitació de l'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TramitacioException extends Exception {

	private static final long serialVersionUID = 1L;

	public TramitacioException(String msg) {
        super(msg);
    }

    public TramitacioException(String msg, Throwable t) {
        super(msg, t);
    }

}
