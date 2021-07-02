/**
 * 
 */
package es.caib.helium.integracio.plugins.tramitacio;


/**
 * Excepció que indica que han sorgit errors en el plugin de
 * gestió documental
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TramitacioPluginException extends Exception {

	private static final long serialVersionUID = 1L;

	public TramitacioPluginException(String msg) {
        super(msg);
    }

    public TramitacioPluginException(String msg, Throwable t) {
        super(msg, t);
    }

}
