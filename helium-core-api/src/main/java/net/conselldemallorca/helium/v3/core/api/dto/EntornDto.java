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

	// Opcions de visualització de la capçalera
	private String colorFons;
	private String colorLletra;

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

	public String getColorFons() {
		return colorFons;
	}
	public void setColorFons(String colorFons) {
		this.colorFons = colorFons;
	}
	public String getColorLletra() {
		return colorLletra;
	}
	public void setColorLletra(String colorLletra) {
		this.colorLletra = colorLletra;
	}

	private static final long serialVersionUID = 2677183498182144912L;

}
