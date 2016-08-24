/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;

/**
 * Command per afegir camps a les tasques de la definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DefinicioProcesTascaVariableCommand {
	
	@NotNull(groups = {Creacio.class})
	private Long TascaId;
	@NotNull(groups = {Creacio.class})
	private Long campId;
	private boolean readFrom;
	private boolean writeTo;
	private boolean required;
	private boolean readOnly;
	
	public Long getTascaId() {
		return TascaId;
	}
	public void setTascaId(Long tascaId) {
		TascaId = tascaId;
	}
	public Long getCampId() {
		return campId;
	}
	public void setCampId(Long campId) {
		this.campId = campId;
	}
	public boolean isReadFrom() {
		return readFrom;
	}
	public void setReadFrom(boolean readFrom) {
		this.readFrom = readFrom;
	}
	public boolean isWriteTo() {
		return writeTo;
	}
	public void setWriteTo(boolean writeTo) {
		this.writeTo = writeTo;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public static CampTascaDto asCampTascaDto(DefinicioProcesTascaVariableCommand command) {
		CampTascaDto dto = new CampTascaDto();
		CampDto camp = new CampDto();
		camp.setId(command.getCampId());
		dto.setCamp(camp);
		dto.setReadFrom(command.isReadFrom());
		dto.setWriteTo(command.isWriteTo());
		dto.setRequired(command.isRequired());
		dto.setReadOnly(command.isReadOnly());
		return dto;
	}	
	public interface Creacio {}
}
