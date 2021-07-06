/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import java.util.Set;

/**
 * DTO amb informació d'una àrea.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AreaDto {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private Set<AreaDto> fills;
	private Set<AreaMembreDto> membres;

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
	public Set<AreaDto> getFills() {
		return fills;
	}
	public void setFills(Set<AreaDto> fills) {
		this.fills = fills;
	}
	public Set<AreaMembreDto> getMembres() {
		return membres;
	}
	public void setMembres(Set<AreaMembreDto> membres) {
		this.membres = membres;
	}

}
