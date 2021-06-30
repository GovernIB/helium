package es.caib.helium.logic.intf.dto;

public class EntornTipusAreaDto {

	private Long id;
	private String codi;
	private String descripcio;
	private String nom;
	private Long entornId;
	
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
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public Long getEntornId() {
		return entornId;
	}
	public void setEntornId(Long entornId) {
		this.entornId = entornId;
	}
}
