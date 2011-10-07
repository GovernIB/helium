/**
 * 
 */
package net.conselldemallorca.helium.core.model.dto;

import java.util.Map;


/**
 * DTO amb informació d'una fila de la consulta
 * avançada d'expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientConsultaDissenyDto {

	private ExpedientDto expedient;
	private Map<String, DadaIndexadaDto> dadesExpedient;



	public ExpedientDto getExpedient() {
		return expedient;
	}
	public void setExpedient(ExpedientDto expedient) {
		this.expedient = expedient;
	}
	public Map<String, DadaIndexadaDto> getDadesExpedient() {
		return dadesExpedient;
	}
	public void setDadesExpedient(Map<String, DadaIndexadaDto> dadesExpedient) {
		this.dadesExpedient = dadesExpedient;
	}

}
