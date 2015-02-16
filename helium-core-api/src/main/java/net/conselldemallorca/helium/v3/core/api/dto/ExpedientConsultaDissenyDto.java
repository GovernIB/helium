/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

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

	public String getEstat() {
		return dadesExpedient.get(ExpedientCamps.EXPEDIENT_CAMP_ESTAT_JSP).getValorMostrar();
	}

	public String getComentariAnulat() {
		return expedient.getComentariAnulat();
	}
	public String getErrorDesc() {
		return expedient.getErrorDesc();
	}
	public String getErrorFull() {
		return expedient.getErrorFull();
	}	
	public String getProcessInstanceId() {
		return expedient.getProcessInstanceId();
	}
	public Long getId() {
		return expedient.getId();
	}
	public boolean isErrorsIntegracions() {
		return expedient.isErrorsIntegracions();
	}
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
	public boolean isAnulat() {
		return expedient.isAnulat();
	}	
	public boolean isAturat() {
		return expedient.isAturat();
	}
	public String getInfoAturat() {
		return expedient.getInfoAturat();
	}
	public boolean isPermisCreate() {
		return expedient.permisCreate;
	}
	public boolean isPermisRead() {
		return expedient.permisRead;
	}
	public boolean isPermisWrite() {
		return expedient.permisWrite;
	}
	public boolean isPermisDelete() {
		return expedient.permisDelete;
	}
	public boolean isPermisSupervision() {
		return expedient.permisSupervision;
	}
	public boolean isPermisReassignment() {
		return expedient.permisReassignment;
	}
	public boolean isPermisDesign() {
		return expedient.permisDesign;
	}
	public boolean isPermisOrganization() {
		return expedient.permisOrganization;
	}
	public boolean isPermisManage() {
		return expedient.permisManage;
	}
	public boolean isPermisAdministration() {
		return expedient.permisAdministration;
	}
}
