/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;


/**
 * Excepció que indica que han sorgit errors en el plugin de
 * custòdia documental
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CustodiaPluginException extends Exception {

	private static final long serialVersionUID = 1L;

	public CustodiaPluginException(String msg) {
        super(msg);
    }

    public CustodiaPluginException(String msg, Throwable t) {
        super(msg, t);
    }

}
