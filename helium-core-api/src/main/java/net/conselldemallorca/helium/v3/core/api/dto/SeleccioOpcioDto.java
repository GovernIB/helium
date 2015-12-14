/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;



/**
 * DTO amb informació d'una opció d'un camp de formulari
 * de tipus selecció.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SeleccioOpcioDto {

	private String codi;
	private String nom;

	public SeleccioOpcioDto() {
	}
	public SeleccioOpcioDto(String codi, String nom) {
		this.codi = codi;
		this.nom = nom;
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
		this.nom = nom.replaceAll("\\p{Cntrl}", "").trim();
	}

}
