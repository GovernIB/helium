package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;


/**
 * DTO amb informació d'un camp d'una tasca de la
 * definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CampTascaDto implements Serializable {

	private Long id;
	private boolean readFrom;
	private boolean writeTo;
	private boolean required;
	private boolean readOnly;
	private int order;
	private int ampleCols;
	private int buitCols;

	private CampDto camp;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public CampDto getCamp() {
		return camp;
	}
	public void setCamp(CampDto camp) {
		this.camp = camp;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public CampTascaDto() {}
	public CampTascaDto(
			CampDto camp,
			boolean readFrom,
			boolean writeTo,
			boolean required,
			boolean readOnly,
			int order,
			int ampleCols,
			int buitCols) {
		this.camp = camp;
		this.readFrom = readFrom;
		this.writeTo = writeTo;
		this.required = required;
		this.readOnly = readOnly;
		this.order = order;
		this.ampleCols = ampleCols;
		this.buitCols = buitCols;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((camp == null) ? 0 : camp.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CampTascaDto other = (CampTascaDto) obj;
		if (camp == null) {
			if (other.camp != null)
				return false;
		} else if (!camp.equals(other.camp))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
