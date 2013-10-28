/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

/**
 * DTO amb informació d'un entorn.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntornDto {
	public EntornDto(Long id, String codi, String nom) {
		super();
		this.id = id;
		this.codi = codi;
		this.nom = nom;
	}

	public EntornDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private boolean actiu;
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
}
