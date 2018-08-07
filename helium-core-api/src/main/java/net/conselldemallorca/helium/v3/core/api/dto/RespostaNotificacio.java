/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Map;

/**
 * Resposta a una anotació de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaNotificacio {

	public enum NotificacioEstat {
		PENDENT,
		ENVIADA,
		FINALITZADA
	}
	
	private String identificador;
	private NotificacioEstat estat;
	private Map<String, String> referencies;

	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public NotificacioEstat getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEstat estat) {
		this.estat = estat;
	}
	public Map<String, String> getReferencies() {
		return referencies;
	}
	public void setReferencies(Map<String, String> referencies) {
		this.referencies = referencies;
	}
}
