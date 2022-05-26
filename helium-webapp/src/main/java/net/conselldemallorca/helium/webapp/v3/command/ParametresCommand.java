/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

/**
 * Command que representa el formulari d'administració de configuració paràmetres de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ParametresCommand {

	boolean redireccionar;
	boolean propagarEsborratExpedients;

	public boolean isRedireccionar() {
		return redireccionar;
	}
	public void setRedireccionar(boolean redireccionar) {
		this.redireccionar = redireccionar;
	}
	public boolean isPropagarEsborratExpedients() {
		return propagarEsborratExpedients;
	}
	public void setPropagarEsborratExpedients(boolean propagarEsborratExpedients) {
		this.propagarEsborratExpedients = propagarEsborratExpedients;
	}
	

}
