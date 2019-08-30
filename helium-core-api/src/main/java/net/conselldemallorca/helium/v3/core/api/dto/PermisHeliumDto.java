/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

/**
 * Informació d'un permís Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisHeliumDto {

	private String codi;
	private String descripcio;

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
