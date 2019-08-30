/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

/**
 * Command per afegir un camp a una tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CampTascaCommand {

	private Long tascaId;
	private Long campId;
	private boolean readFrom;
	private boolean writeTo;
	private boolean required;
	private boolean readOnly;



	public CampTascaCommand() {}

	public Long getTascaId() {
		return tascaId;
	}
	public void setTascaId(Long tascaId) {
		this.tascaId = tascaId;
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

}
