/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.portasignatures;


/**
 * Excepció que indica que han sorgit errors en el plugin de
 * portasignatures
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class PortasignaturesPluginException extends Exception {

	private static final long serialVersionUID = 1L;

	public PortasignaturesPluginException(String msg) {
        super(msg);
    }

    public PortasignaturesPluginException(String msg, Throwable t) {
        super(msg, t);
    }

}
