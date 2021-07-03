package es.caib.helium.logic.intf.dto;

public class EntornAreaDto {

	private Long id;
	private String codi;
	private String descripcio;
	private String nom;
	private Long entornId;
	private Long pareId;
	private Long tipusId;
	
	private EntornAreaDto pare;
	private EntornTipusAreaDto tipus;
	
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
	public Long getPareId() {
		return pareId;
	}
	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}
	public Long getTipusId() {
		return tipusId;
	}
	public void setTipusId(Long tipusId) {
		this.tipusId = tipusId;
	}
	public EntornAreaDto getPare() {
		return pare;
	}
	public void setPare(EntornAreaDto pare) {
		this.pare = pare;
	}
	public EntornTipusAreaDto getTipus() {
		return tipus;
	}
	public void setTipus(EntornTipusAreaDto tipus) {
		this.tipus = tipus;
	}
}	
