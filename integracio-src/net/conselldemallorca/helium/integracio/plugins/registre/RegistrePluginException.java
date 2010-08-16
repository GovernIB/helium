/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;


/**
 * Excepció que indica que han sorgit errors en el plugin de
 * registre
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class RegistrePluginException extends Exception {

	private static final long serialVersionUID = 1L;

	public RegistrePluginException(String msg) {
        super(msg);
    }

    public RegistrePluginException(String msg, Throwable t) {
        super(msg, t);
    }

}
