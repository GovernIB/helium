/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;


/**
 * DTO amb informació d'un rol de perís definit a Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisRolDto extends ControlPermisosDto implements Serializable {
	

	public PermisRolDto(String codi, String descripcio) {
		this.codi = codi;
		this.descripcio = descripcio;
	}

	public PermisRolDto() {}
	
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

	@Override
	public String toString() {
		return "PermisRolDto [codi=" + codi + ", descripcio=" + descripcio + "]";
	}

	private static final long serialVersionUID = -9207907579002520198L;

}