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
	private String campCodi;
	@NotNull(groups = {Creacio.class})
	private TipusConsultaCamp tipus;
	/** Indica l'origna de les variables. Si -2 és una propietat de l'expedient, si -1 és una variable del tipus d'expedient,
	 * si no indica l'id de la definició de procés. */
	private Long origen; 

	
	public static final long ORIGEN_EXPEDIENT = -2;
	public static final long ORIGEN_TIPUS_EXPEDIENT = -1;	
	
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
	public String getCampCodi() {
		return campCodi;
	}
	public void setCampCodi(String campCodi) {
		this.campCodi = campCodi;
	}
	public TipusConsultaCamp getTipus() {
		return tipus;
	}
	public void setTipus(TipusConsultaCamp tipus) {
		this.tipus = tipus;
	}
	public Long getOrigen() {
		return origen;
	}
	public void setOrigen(Long origen) {
		this.origen = origen;
	}
	public static ConsultaCampDto asConsultaCampDto(ExpedientTipusConsultaVarCommand command) {
		ConsultaCampDto dto = new ConsultaCampDto();
		dto.setTipus(command.getTipus());
		dto.setCampCodi(command.getCampCodi());
		return dto;
	}
	
	public interface Creacio {}
	public interface Modificacio {}
}
