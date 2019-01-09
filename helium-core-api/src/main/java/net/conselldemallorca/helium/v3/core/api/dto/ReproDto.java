/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * DTO amb informació d'un termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReproDto {

	private Long id;
	private String nom;
	private String valors;
	private String tascaCodi;
	
	public String getTascaCodi() {
		return tascaCodi;
	}
	public void setTascaCodi(String tascaCodi) {
		this.tascaCodi = tascaCodi;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getValors() {
		return valors;
	}
	public void setValors(String valors) {
		this.valors = valors;
	}

}
