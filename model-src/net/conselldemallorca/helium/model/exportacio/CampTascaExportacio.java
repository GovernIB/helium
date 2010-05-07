/**
 * 
 */
package net.conselldemallorca.helium.model.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'un camp de formulari per exportar
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class CampTascaExportacio implements Serializable {

	private String campCodi;
	private boolean readFrom;
	private boolean writeTo;
	private boolean required;
	private boolean readOnly;
	private int order;



	public CampTascaExportacio(
			String campCodi,
			boolean readFrom,
			boolean writeTo,
			boolean required,
			boolean readOnly,
			int order) {
		this.campCodi = campCodi;
		this.readFrom = readFrom;
		this.writeTo = writeTo;
		this.required = required;
		this.readOnly = readOnly;
		this.order = order;
	}

	public String getCampCodi() {
		return campCodi;
	}
	public void setCampCodi(String campCodi) {
		this.campCodi = campCodi;
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
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}



	private static final long serialVersionUID = 1L;

}
