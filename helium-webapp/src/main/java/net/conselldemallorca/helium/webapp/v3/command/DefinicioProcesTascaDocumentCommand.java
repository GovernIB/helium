/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;

import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;

/**
 * Command per afegir documents a les tasques de la definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DefinicioProcesTascaDocumentCommand {
	
	@NotNull(groups = {Creacio.class})
	private Long tascaId;
	@NotNull(groups = {Creacio.class})
	private Long documentId;
	private boolean required;
	private boolean readOnly;
	
	public Long getTascaId() {
		return tascaId;
	}
	public void setTascaId(Long tascaId) {
		this.tascaId = tascaId;
	}
	public Long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
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
	public static DocumentTascaDto asDocumentTascaDto(DefinicioProcesTascaDocumentCommand command) {
		DocumentTascaDto dto = new DocumentTascaDto();
		DocumentDto document = new DocumentDto();
		document.setId(command.getDocumentId());
		dto.setDocument(document);
		dto.setRequired(command.isRequired());
		dto.setReadOnly(command.isReadOnly());
		return dto;
	}	
	public interface Creacio {}
}