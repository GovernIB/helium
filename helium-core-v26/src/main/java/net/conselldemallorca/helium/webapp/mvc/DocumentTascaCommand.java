/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

/**
 * Command per afegir un document a una tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentTascaCommand {

	private Long documentId;
	private Long tascaId;
	private boolean required;
	private boolean readOnly;



	public DocumentTascaCommand() {}

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

	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

}
