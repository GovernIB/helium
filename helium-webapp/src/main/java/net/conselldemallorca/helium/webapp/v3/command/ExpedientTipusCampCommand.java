/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCampCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCampCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusCamp;

/**
 * Command per editar la informació de les varialbes dels tipus d'expedient 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusCamp(groups = {Creacio.class, Modificacio.class})
public class ExpedientTipusCampCommand {
	
	private Long expedientTipusId;
	private Long id;
	private Long agrupacioId;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 64, groups = {Creacio.class})
	private String codi;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private CampTipusDto tipus;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String etiqueta;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String observacions;
	private Long dominiId;
	private boolean multiple;
	private boolean ocult;
	/** No retrocedir valor */
	private boolean ignored;

		
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getAgrupacioId() {
		return agrupacioId;
	}
	public void setAgrupacioId(Long agrupacioId) {
		this.agrupacioId = agrupacioId;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public CampTipusDto getTipus() {
		return tipus;
	}
	public void setTipus(CampTipusDto tipus) {
		this.tipus = tipus;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public Long getDominiId() {
		return dominiId;
	}
	public void setDominiId(Long dominiId) {
		this.dominiId = dominiId;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public boolean isOcult() {
		return ocult;
	}
	public void setOcult(boolean ocult) {
		this.ocult = ocult;
	}
	public boolean isIgnored() {
		return ignored;
	}
	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	
	public static CampDto asCampDto(ExpedientTipusCampCommand command) {
		CampDto dto = new CampDto();
		dto.setId(command.getId());
		if(command.getAgrupacioId() != null) {
			CampAgrupacioDto agrupacioDto = new CampAgrupacioDto();
			agrupacioDto.setId(command.getAgrupacioId());
			dto.setAgrupacio(agrupacioDto);
		}
		dto.setCodi(command.getCodi());
		dto.setEtiqueta(command.getEtiqueta());
		dto.setTipus(command.getTipus());
		dto.setObservacions(command.getObservacions());
		dto.setDominiId(command.getDominiId());
		dto.setMultiple(command.isMultiple());
		dto.setOcult(command.isOcult());
		dto.setIgnored(command.isIgnored());		
		
		return dto;
	}

	public interface Creacio {}
	public interface Modificacio {}
}
