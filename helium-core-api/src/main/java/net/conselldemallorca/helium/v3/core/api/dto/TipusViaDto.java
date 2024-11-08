package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * Objecte que representa un municipi provinent d'una font externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TipusViaDto implements Serializable {

	private String codi;
	private String descripcio;

	public TipusViaDto() {
		
	}
	
	public TipusViaDto(
			String codi,
			String descripcio) {
		this.codi = codi;
		this.descripcio = descripcio;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	

	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

}
