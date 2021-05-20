/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Resposta a una anotació de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
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

}
