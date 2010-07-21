/**
 * 
 */
package net.conselldemallorca.helium.integracio.gesdoc;


/**
 * Excepció que indica que han sorgit errors en el plugin de
 * gestió documental
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class GestioDocumentalPluginException extends Exception {

	private static final long serialVersionUID = 1L;

	public GestioDocumentalPluginException(String msg) {
        super(msg);
    }

    public GestioDocumentalPluginException(String msg, Throwable t) {
        super(msg, t);
    }

}
