/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import java.util.List;

/**
 * Resposta a una anotació de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaNotificacio {

	public enum NotificacioEstat {
		PENDENT,
		ENVIADA,
		FINALITZADA,
		REGISTRADA,
		PROCESSADA
	}
	
	private String identificador;
	private NotificacioEstat estat;
	private List<ReferenciaNotificacio> referencies;

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
	public List<ReferenciaNotificacio> getReferencies() {
		return referencies;
	}
	public void setReferencies(List<ReferenciaNotificacio> referencies) {
		this.referencies = referencies;
	}
}
