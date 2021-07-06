/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.helium.logic.intf.dto.ConsultaCampDto;
import es.caib.helium.logic.intf.dto.ConsultaCampDto.TipusConsultaCamp;
import es.caib.helium.logic.intf.dto.ConsultaCampDto.TipusParamConsultaCamp;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusConsultaParamCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusConsultaParamCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.Codi;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusConsultaParam;

/**
 * Command per afegir camps a les consultes dels tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusConsultaParam(groups = {Creacio.class, Modificacio.class})
public class ExpedientTipusConsultaParamCommand {
	
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private Long expedientTipusId;
	
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private Long consultaId;
	
	private Long id;

	@NotNull(groups = {Creacio.class, Modificacio.class})
	private TipusConsultaCamp tipus;

	@Codi(groups={Creacio.class, Modificacio.class})
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 64, groups = {Creacio.class, Modificacio.class})
	private String campCodi;
	
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 64, groups = {Creacio.class, Modificacio.class})
	private String campDescripcio;
	
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private TipusParamConsultaCamp paramTipus;
	
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public TipusConsultaCamp getTipus() {
		return tipus;
	}
	public void setTipus(TipusConsultaCamp tipus) {
		this.tipus = tipus;
	}
	public String getCampCodi() {
		return campCodi;
	}
	public void setCampCodi(String campCodi) {
		this.campCodi = campCodi;
	}
	public String getCampDescripcio() {
		return campDescripcio;
	}
	public void setCampDescripcio(String campDescripcio) {
		this.campDescripcio = campDescripcio;
	}
	public TipusParamConsultaCamp getParamTipus() {
		return paramTipus;
	}
	public void setParamTipus(TipusParamConsultaCamp paramTipus) {
		this.paramTipus = paramTipus;
	}
	
	public static ConsultaCampDto asConsultaCampDto(ExpedientTipusConsultaParamCommand command) {
		ConsultaCampDto dto = new ConsultaCampDto();
		dto.setId(command.getId());
		dto.setTipus(command.getTipus());
		dto.setCampCodi(command.getCampCodi());
		dto.setCampDescripcio(command.getCampDescripcio());
		dto.setParamTipus(command.getParamTipus());
		return dto;
	}
	
	public interface Creacio {}
	public interface Modificacio {}
}
