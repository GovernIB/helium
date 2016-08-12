/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;

import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto.TipusConsultaCamp;

/**
 * Command per afegir camps a les consultes dels tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusConsultaVarCommand {
	
	private Long expedientTipusId;
	@NotNull(groups = {Creacio.class})
	private Long consultaId;
	@NotNull(groups = {Creacio.class})
	private Long campId;
	@NotNull(groups = {Creacio.class})
	private TipusConsultaCamp tipus;
	
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public Long getConsultaId() {
		return consultaId;
	}
	public void setConsultaId(Long consultaId) {
		this.consultaId = consultaId;
	}
	public Long getCampId() {
		return campId;
	}
	public void setCampId(Long campId) {
		this.campId = campId;
	}
	public TipusConsultaCamp getTipus() {
		return tipus;
	}
	public void setTipus(TipusConsultaCamp tipus) {
		this.tipus = tipus;
	}
	public static ConsultaCampDto asConsultaCampDto(ExpedientTipusConsultaVarCommand command) {
		ConsultaCampDto dto = new ConsultaCampDto();
		dto.setTipus(command.getTipus());
		dto.setCampId(command.getCampId());
		return dto;
	}
	
	public interface Creacio {}
	public interface Modificacio {}
}
