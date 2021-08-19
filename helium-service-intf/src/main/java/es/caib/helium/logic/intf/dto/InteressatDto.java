/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import es.caib.helium.logic.intf.dto.DadesEnviamentDto.EntregaPostalTipus;
import es.caib.helium.logic.intf.integracio.notificacio.InteressatTipusEnum;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatDto {
	
	private Long id;
	private String codi;
	private String nif;
	private String dir3Codi;
	private String nom;
	private String llinatge1;  
	private String llinatge2;  

	private String email;  
	private String telefon;
	private Long expedientId;
	private InteressatTipusEnum tipus;
	
	private Boolean entregaPostal;
	private EntregaPostalTipus entregaTipus;
	private String linia1;
	private String linia2;
	private String codiPostal;
	private Boolean entregaDeh;
	private Boolean entregaDehObligat;

	public InteressatTipusEnum getTipus() {
		return tipus;
	}
	public void setTipus(InteressatTipusEnum tipus) {
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
	public String getDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(String dir3Codi) {
		this.dir3Codi = dir3Codi;
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

	public Boolean getEntregaPostal() {
		return entregaPostal != null ? entregaPostal : false;
	}
	public void setEntregaPostal(Boolean entregaPostal) {
		this.entregaPostal = entregaPostal;
	}
	public EntregaPostalTipus getEntregaTipus() {
		return entregaTipus;
	}
	public void setEntregaTipus(EntregaPostalTipus entregaTipus) {
		this.entregaTipus = entregaTipus;
	}
	public String getLinia1() {
		return linia1;
	}
	public void setLinia1(String linia1) {
		this.linia1 = linia1;
	}
	public String getLinia2() {
		return linia2;
	}
	public void setLinia2(String linia2) {
		this.linia2 = linia2;
	}
	public String getCodiPostal() {
		return codiPostal;
	}
	public void setCodiPostal(String codiPostal) {
		this.codiPostal = codiPostal;
	}
	public Boolean getEntregaDeh() {
		return entregaDeh != null ? entregaDeh : false;
	}
	public void setEntregaDeh(Boolean entregaDeh) {
		this.entregaDeh = entregaDeh;
	}
	public Boolean getEntregaDehObligat() {
		return entregaDehObligat != null ? entregaDehObligat : false;
	}
	public void setEntregaDehObligat(Boolean entregaDehObligat) {
		this.entregaDehObligat = entregaDehObligat;
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
		String codiDocument;
		if (tipus != null && InteressatTipusEnumDto.ADMINISTRACIO.equals(tipus))
			codiDocument = dir3Codi;
		else
			codiDocument = nif;
		return codiDocument + " - " + getFullNom();
	}

}
