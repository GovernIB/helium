/**
 * 
 */
package es.caib.helium.logic.intf.dto;

/**
 * DTO amb informació d'un valor d'una enumeració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnumeracioValorDto {

	private Long id;
	private String codi;
	private String nom;
	int ordre;

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
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

}
