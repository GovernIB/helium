/**
 * 
 */
package es.caib.helium.integracio.plugins.gis;


/**
 * Excepció que indica que han sorgit errors en el plugin del GIS
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GisPluginException extends Exception {

	private static final long serialVersionUID = 1L;

	public GisPluginException(String msg) {
        super(msg);
    }

    public GisPluginException(String msg, Throwable t) {
        super(msg, t);
    }

}
