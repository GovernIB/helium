/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * DTO amb informació d'un interessat d'una anotació de distribució.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AnotacioInteressatDto implements Serializable {

	private Long id;
	private String adresa;
	private String canal;
	private String cp;
	private String documentNumero;
	private String documentTipus;
	private String email;
	private String llinatge1;
	private String llinatge2;
	private String nom;
	private String observacions;
	private String municipiCodi;
	private String paisCodi;
	private String provinciaCodi;
	private String municipi;
	private String pais;
	private String provincia;
	private String raoSocial;
	private String telefon;
	private String tipus;
	private AnotacioInteressatDto representant;
	private String organCodi;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAdresa() {
		return adresa;
	}
	public void setAdresa(String adresa) {
		this.adresa = adresa;
	}
	public String getCanal() {
		return canal;
	}
	public void setCanal(String canal) {
		this.canal = canal;
	}
	public String getCp() {
		return cp;
	}
	public void setCp(String cp) {
		this.cp = cp;
	}
	public String getDocumentNumero() {
		return documentNumero;
	}
	public void setDocumentNumero(String documentNumero) {
		this.documentNumero = documentNumero;
	}
	public String getDocumentTipus() {
		return documentTipus;
	}
	public void setDocumentTipus(String documentTipus) {
		this.documentTipus = documentTipus;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public String getMunicipiCodi() {
		return municipiCodi;
	}
	public void setMunicipiCodi(String municipiCodi) {
		this.municipiCodi = municipiCodi;
	}
	public String getPaisCodi() {
		return paisCodi;
	}
	public void setPaisCodi(String paisCodi) {
		this.paisCodi = paisCodi;
	}
	public String getProvinciaCodi() {
		return provinciaCodi;
	}
	public void setProvinciaCodi(String provinciaCodi) {
		this.provinciaCodi = provinciaCodi;
	}
	public String getMunicipi() {
		return municipi;
	}
	public void setMunicipi(String municipi) {
		this.municipi = municipi;
	}
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(String raoSocial) {
		this.raoSocial = raoSocial;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public AnotacioInteressatDto getRepresentant() {
		return representant;
	}
	public void setRepresentant(AnotacioInteressatDto representant) {
		this.representant = representant;
	}
	public String getOrganCodi() {
		return organCodi;
	}
	public void setOrganCodi(String organCodi) {
		this.organCodi = organCodi;
	}
	
	/** Retorna el nom segons si és persona, administració o 
	 * 
	 */
	public String getNomComplet() {
		String nomComplet = null;
		if ("PERSONA_FISICA".equals(tipus)) {
			StringBuilder nomCompletStrb = new StringBuilder();
			nomCompletStrb.append(nom);
			if (llinatge1 != null) {
				nomCompletStrb.append(" ").append(llinatge1);
			}
			if (llinatge2 != null) {
				nomCompletStrb.append(" ").append(llinatge2);
			}
			nomComplet = nomCompletStrb.toString();
		} else {
			nomComplet = raoSocial != null ? raoSocial : nom;
		}
			
		return nomComplet;
	}
	
	
	private static final long serialVersionUID = 3888652004198983250L;
}