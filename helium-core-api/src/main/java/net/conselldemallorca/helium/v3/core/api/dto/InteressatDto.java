/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalTipus;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatDto {//MARTA
	
	private Long id;
	private String codi;
	//private String nif;
	private String documentIdent;

	private String dir3Codi;
	private String nom;
	private String llinatge1;  
	private String llinatge2;  

	private String email;  
	private String telefon;
	private Long expedientId;
	private InteressatTipusEnumDto tipus;
	
	private Boolean entregaPostal;
	private EntregaPostalTipus entregaTipus;
	private String linia1;
	private String linia2;
	private String codiPostal;
	private Boolean entregaDeh;
	private Boolean entregaDehObligat;
	
	private String observacions;
	private InteressatDocumentTipusEnumDto tipusDocIdent;
	private String codiDire;
	private String direccio;
	private String raoSocial;
    private Boolean es_representant = false;
	private String municipiCodi;
	private String paisCodi;
	private String provinciaCodi;
	private String municipi;
	private String pais;
	private String provincia;
    private InteressatDto representat; //només existeix quan es_representant=true
    private InteressatDto representant; //només existeix quan es_representant=false



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
			codiDocument = documentIdent;
		return codiDocument + " - " + getFullNom();
	}
	public String getDocumentIdent() {
		return documentIdent;
	}
	public void setDocumentIdent(String documentIdent) {
		this.documentIdent = documentIdent;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public InteressatDocumentTipusEnumDto getTipusDocIdent() {
		return tipusDocIdent;
	}
	public void setTipusDocIdent(InteressatDocumentTipusEnumDto tipusDocIdent) {
		this.tipusDocIdent = tipusDocIdent;
	}
	public String getCodiDire() {
		return codiDire;
	}
	public void setCodiDire(String codiDire) {
		this.codiDire = codiDire;
	}
	public String getDireccio() {
		return direccio;
	}
	public void setDireccio(String direccio) {
		this.direccio = direccio;
	}
	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(String raoSocial) {
		this.raoSocial = raoSocial;
	}
	public Boolean getEs_representant() {
		return es_representant != null ? es_representant : false;
	}
	public void setEs_representant(Boolean es_representant) {
		this.es_representant = es_representant;
	}
//	public boolean isEs_representant() {
//		return es_representant;
//	}
	public void setEs_representant(boolean es_representant) {
		this.es_representant = es_representant;
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
	public InteressatDto getRepresentat() {
		return representat;
	}
	public void setRepresentat(InteressatDto representat) {
		this.representat = representat;
	}
	public InteressatDto getRepresentant() {
		return representant;
	}
	public void setRepresentant(InteressatDto representant) {
		this.representant = representant;
	}
	

}
