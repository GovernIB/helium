/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

/**
 * DTO amb informació d'una integració amb un sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class IntegracioDto {

	private String codi;
	private String descripcio;

	public IntegracioDto() {
	}
	public IntegracioDto(String codi, String descripcio) {
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
