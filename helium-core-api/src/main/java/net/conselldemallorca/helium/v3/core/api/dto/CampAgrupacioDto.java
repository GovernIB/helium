/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * DTO amb informació d'una agrupació de camps.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CampAgrupacioDto extends HeretableDto {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private int ordre;

	public CampAgrupacioDto() {
		
	}
	
	public CampAgrupacioDto(Long id, String codi, String nom, String descripcio, int ordre) {
		super();
		this.id = id;
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.ordre = ordre;
	}
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
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	private static final long serialVersionUID = -1156854629101439726L;
}
