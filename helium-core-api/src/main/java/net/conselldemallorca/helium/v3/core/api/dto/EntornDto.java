/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * DTO amb informació d'un entorn.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntornDto extends ControlPermisosDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private boolean actiu;

	private int permisCount = 0;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public boolean isActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}
	public int getPermisCount() {
		return permisCount;
	}
	public void setPermisCount(int permisCount) {
		this.permisCount = permisCount;
	}

	private static final long serialVersionUID = 2677183498182144912L;

}
