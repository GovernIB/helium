/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

/**
 * Excepció que es llança si hi ha algun error crindant al plugin.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class PluginException extends RuntimeException {

	public PluginException(String message) {
		super(message);
	}
	public PluginException(String message, Throwable cause) {
		super(message, cause);
	}
	public PluginException(Throwable cause) {
		super(cause);
	}

}
