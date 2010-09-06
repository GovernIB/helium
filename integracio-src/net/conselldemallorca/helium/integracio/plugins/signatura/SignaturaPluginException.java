/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;


/**
 * Excepció que indica que han sorgit errors en el plugin de
 * custòdia documental
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class SignaturaPluginException extends Exception {

	private static final long serialVersionUID = 1L;

	public SignaturaPluginException(String msg) {
        super(msg);
    }

    public SignaturaPluginException(String msg, Throwable t) {
        super(msg, t);
    }

}
