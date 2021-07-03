/**
 * 
 */
package es.caib.helium.integracio.plugins.gesdoc;


/**
 * Excepció que indica que han sorgit errors en el plugin de
 * gestió documental
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
