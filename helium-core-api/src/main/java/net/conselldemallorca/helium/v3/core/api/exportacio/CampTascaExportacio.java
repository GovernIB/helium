/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'un camp de formulari per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CampTascaExportacio implements Serializable {

	private String campCodi;
	private boolean readFrom;
	private boolean writeTo;
	private boolean required;
	private boolean readOnly;
	private int order;
	private int ampleCols;
	private int buitCols;
	/** Indica si el camp està lligat al tipus d'expedient.*/
	private boolean tipusExpedient;



	public CampTascaExportacio(
			String campCodi,
			boolean readFrom,
			boolean writeTo,
			boolean required,
			boolean readOnly,
			int order,
			int ampleCols,
			int buitCols,
			boolean tipusExpedient) {
		this.campCodi = campCodi;
		this.readFrom = readFrom;
		this.writeTo = writeTo;
		this.required = required;
		this.readOnly = readOnly;
		this.order = order;
		this.ampleCols = ampleCols;
		this.buitCols = buitCols;
		this.tipusExpedient = tipusExpedient;
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
	public int getAmpleCols() {
		return ampleCols;
	}

	public void setAmpleCols(int ampleCols) {
		this.ampleCols = ampleCols;
	}

	public int getBuitCols() {
		return buitCols;
	}

	public void setBuitCols(int buitCols) {
		this.buitCols = buitCols;
	}

	public boolean isTipusExpedient() {
		return tipusExpedient;
	}
	public void setTipusExpedient(boolean tipusExpedient) {
		this.tipusExpedient = tipusExpedient;
	}

	private static final long serialVersionUID = -8791553619883705667L;
}
