/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatDto {
	
	private Long id;
	private String codi;
	private String nif;
	private String nom;
	private String llinatge1;  
	private String llinatge2;  

	private String email;  
	private String telefon;
	private Long expedientId;
	
	private InteressatTipusEnumDto tipus;
	
	

	public InteressatTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(InteressatTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
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
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	} 

	public String getFullNom() {
		StringBuilder fullNom = new StringBuilder(nom);
		if (llinatge1 != null)
			fullNom.append(" ").append(llinatge1);
		if (llinatge2 != null)
			fullNom.append(" ").append(llinatge2);
		return fullNom.toString();
	}
	
	public String getFullInfo() {
		return nif + " - " + getFullNom();
	}

}
