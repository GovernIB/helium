/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

/**
 * Command per afegir un document a una tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaTascaCommand {

	private Long documentId;
	private Long tascaId;
	private boolean required;



	public FirmaTascaCommand() {}

	public Long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public Long getTascaId() {
		return tascaId;
	}
	public void setTascaId(Long tascaId) {
		this.tascaId = tascaId;
	}

	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}

}
