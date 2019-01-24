/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.notificacio;

/**
 * Informació retornada per la consulta de l'estat d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaConsultaEstatNotificacio {

	private NotificacioEstat estat;

	public NotificacioEstat getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEstat estat) {
		this.estat = estat;
	}

}
