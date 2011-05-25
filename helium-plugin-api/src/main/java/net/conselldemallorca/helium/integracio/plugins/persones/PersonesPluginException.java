/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.persones;


/**
 * Excepció que indica que han sorgit errors en el plugin de
 * consulta de persones
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PersonesPluginException extends Exception {

	private static final long serialVersionUID = 1L;

	public PersonesPluginException(String msg) {
        super(msg);
    }

    public PersonesPluginException(String msg, Throwable t) {
        super(msg, t);
    }

}
