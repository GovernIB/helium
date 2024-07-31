package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.NonNull;

public class ExpedientDocumentPinbalDto implements Serializable {
	
	private static final long serialVersionUID = 2766735288507498173L;
	
	private Long expedientId;
	private String processInstanceId;
	private Long documentId;
	private String documentNom;
	private String documentCodi;
	private Long interessatId;
	private boolean commandValidat = false;
	
	private String finalitat;
	private PinbalConsentimentEnum consentiment;

	private String nomServei;	
	private PinbalServeiEnumDto codiServei;
	private boolean pinbalServeiDocPermesDni;
	private boolean pinbalServeiDocPermesNif;
	private boolean pinbalServeiDocPermesCif;
	private boolean pinbalServeiDocPermesNie;
	private boolean pinbalServeiDocPermesPas;
	
	private String comunitatAutonomaCodi;
	private String provinciaCodi;
	private String municipiCodi;
	private Date dataConsulta;
	private Date dataNaixement;
	private boolean consentimentTipusDiscapacitat = false;
	private String numeroTitol;
	private String codiNacionalitat = "724";
	private String paisNaixament = "724";
	private String provinciaNaixament;
	private String poblacioNaixament;
	private String codiPoblacioNaixament;
	private Sexe sexe;
	private String nomPare;
	private String nomMare;
	private String telefon;
	private String email;
	
	private Integer nombreAnysHistoric;
	
	private Integer exercici;
	
	private TipusPassaportEnum tipusPassaport;
	private Date dataCaducidad;
	private Date dataExpedicion;
	private String numeroSoporte;
	
	
	private Integer curs;

	
	private String registreCivil;
	private String tom;
	private String pagina;
	private Date dataRegistre;
	private boolean ausenciaSegundoApellido;
	private String municipiRegistreSVDRRCCDEFUNCIONWS01;
	private String municipiNaixamentSVDRRCCDEFUNCIONWS01;
	private String municipiNaixamentSVDRRCCMATRIMONIOWS01;
	private String municipiRegistreSVDRRCCMATRIMONIOWS01;
	private String municipiRegistreSVDRRCCNACIMIENTOWS01;
	private String municipiNaixamentSVDRRCCNACIMIENTOWS01;
	private String municipiNaixamentSVDDELSEXWS01;
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public Long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}
	public String getDocumentNom() {
		return documentNom;
	}
	public void setDocumentNom(String documentNom) {
		this.documentNom = documentNom;
	}
	public String getDocumentCodi() {
		return documentCodi;
	}
	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	public Long getInteressatId() {
		return interessatId;
	}
	public void setInteressatId(Long interessatId) {
		this.interessatId = interessatId;
	}
	public String getFinalitat() {
		return finalitat;
	}
	public void setFinalitat(String finalitat) {
		this.finalitat = finalitat;
	}
	public String getNomServei() {
		return nomServei;
	}
	public void setNomServei(String nomServei) {
		this.nomServei = nomServei;
	}
	public PinbalServeiEnumDto getCodiServei() {
		return codiServei;
	}
	public void setCodiServei(PinbalServeiEnumDto codiServei) {
		this.codiServei = codiServei;
	}
	public boolean isPinbalServeiDocPermesDni() {
		return pinbalServeiDocPermesDni;
	}
	public void setPinbalServeiDocPermesDni(boolean pinbalServeiDocPermesDni) {
		this.pinbalServeiDocPermesDni = pinbalServeiDocPermesDni;
	}
	public boolean isPinbalServeiDocPermesNif() {
		return pinbalServeiDocPermesNif;
	}
	public void setPinbalServeiDocPermesNif(boolean pinbalServeiDocPermesNif) {
		this.pinbalServeiDocPermesNif = pinbalServeiDocPermesNif;
	}
	public boolean isPinbalServeiDocPermesCif() {
		return pinbalServeiDocPermesCif;
	}
	public void setPinbalServeiDocPermesCif(boolean pinbalServeiDocPermesCif) {
		this.pinbalServeiDocPermesCif = pinbalServeiDocPermesCif;
	}
	public boolean isPinbalServeiDocPermesNie() {
		return pinbalServeiDocPermesNie;
	}
	public void setPinbalServeiDocPermesNie(boolean pinbalServeiDocPermesNie) {
		this.pinbalServeiDocPermesNie = pinbalServeiDocPermesNie;
	}
	public boolean isPinbalServeiDocPermesPas() {
		return pinbalServeiDocPermesPas;
	}
	public void setPinbalServeiDocPermesPas(boolean pinbalServeiDocPermesPas) {
		this.pinbalServeiDocPermesPas = pinbalServeiDocPermesPas;
	}
	public PinbalConsentimentEnum getConsentiment() {
		return consentiment;
	}
	public void setConsentiment(PinbalConsentimentEnum consentiment) {
		this.consentiment = consentiment;
	}
	public String getComunitatAutonomaCodi() {
		return comunitatAutonomaCodi;
	}
	public void setComunitatAutonomaCodi(String comunitatAutonomaCodi) {
		this.comunitatAutonomaCodi = comunitatAutonomaCodi;
	}
	public String getProvinciaCodi() {
		return provinciaCodi;
	}
	public void setProvinciaCodi(String provinciaCodi) {
		this.provinciaCodi = provinciaCodi;
	}
	public String getMunicipiCodi() {
		return municipiCodi;
	}
	public void setMunicipiCodi(String municipiCodi) {
		this.municipiCodi = municipiCodi;
	}
	public Date getDataConsulta() {
		return dataConsulta;
	}
	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}
	public Date getDataNaixement() {
		return dataNaixement;
	}
	public void setDataNaixement(Date dataNaixement) {
		this.dataNaixement = dataNaixement;
	}
	public boolean isConsentimentTipusDiscapacitat() {
		return consentimentTipusDiscapacitat;
	}
	public void setConsentimentTipusDiscapacitat(boolean consentimentTipusDiscapacitat) {
		this.consentimentTipusDiscapacitat = consentimentTipusDiscapacitat;
	}
	public String getNumeroTitol() {
		return numeroTitol;
	}
	public void setNumeroTitol(String numeroTitol) {
		this.numeroTitol = numeroTitol;
	}
	public String getCodiNacionalitat() {
		return codiNacionalitat;
	}
	public void setCodiNacionalitat(String codiNacionalitat) {
		this.codiNacionalitat = codiNacionalitat;
	}
	public String getPaisNaixament() {
		return paisNaixament;
	}
	public void setPaisNaixament(String paisNaixament) {
		this.paisNaixament = paisNaixament;
	}
	public String getProvinciaNaixament() {
		return provinciaNaixament;
	}
	public void setProvinciaNaixament(String provinciaNaixament) {
		this.provinciaNaixament = provinciaNaixament;
	}
	public String getPoblacioNaixament() {
		return poblacioNaixament;
	}
	public void setPoblacioNaixament(String poblacioNaixament) {
		this.poblacioNaixament = poblacioNaixament;
	}
	public String getCodiPoblacioNaixament() {
		return codiPoblacioNaixament;
	}
	public void setCodiPoblacioNaixament(String codiPoblacioNaixament) {
		this.codiPoblacioNaixament = codiPoblacioNaixament;
	}
	public Sexe getSexe() {
		return sexe;
	}
	public void setSexe(Sexe sexe) {
		this.sexe = sexe;
	}
	public String getNomPare() {
		return nomPare;
	}
	public void setNomPare(String nomPare) {
		this.nomPare = nomPare;
	}
	public String getNomMare() {
		return nomMare;
	}
	public void setNomMare(String nomMare) {
		this.nomMare = nomMare;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getNombreAnysHistoric() {
		return nombreAnysHistoric;
	}
	public void setNombreAnysHistoric(Integer nombreAnysHistoric) {
		this.nombreAnysHistoric = nombreAnysHistoric;
	}
	public Integer getExercici() {
		return exercici;
	}
	public void setExercici(Integer exercici) {
		this.exercici = exercici;
	}
	public TipusPassaportEnum getTipusPassaport() {
		return tipusPassaport;
	}
	public void setTipusPassaport(TipusPassaportEnum tipusPassaport) {
		this.tipusPassaport = tipusPassaport;
	}
	public Date getDataCaducidad() {
		return dataCaducidad;
	}
	public void setDataCaducidad(Date dataCaducidad) {
		this.dataCaducidad = dataCaducidad;
	}
	public Date getDataExpedicion() {
		return dataExpedicion;
	}
	public void setDataExpedicion(Date dataExpedicion) {
		this.dataExpedicion = dataExpedicion;
	}
	public String getNumeroSoporte() {
		return numeroSoporte;
	}
	public void setNumeroSoporte(String numeroSoporte) {
		this.numeroSoporte = numeroSoporte;
	}
	public Integer getCurs() {
		return curs;
	}
	public void setCurs(Integer curs) {
		this.curs = curs;
	}
	public String getRegistreCivil() {
		return registreCivil;
	}
	public void setRegistreCivil(String registreCivil) {
		this.registreCivil = registreCivil;
	}
	public String getTom() {
		return tom;
	}
	public void setTom(String tom) {
		this.tom = tom;
	}
	public String getPagina() {
		return pagina;
	}
	public void setPagina(String pagina) {
		this.pagina = pagina;
	}
	public Date getDataRegistre() {
		return dataRegistre;
	}
	public void setDataRegistre(Date dataRegistre) {
		this.dataRegistre = dataRegistre;
	}
	public boolean isAusenciaSegundoApellido() {
		return ausenciaSegundoApellido;
	}
	public void setAusenciaSegundoApellido(boolean ausenciaSegundoApellido) {
		this.ausenciaSegundoApellido = ausenciaSegundoApellido;
	}
	public String getMunicipiRegistreSVDRRCCDEFUNCIONWS01() {
		return municipiRegistreSVDRRCCDEFUNCIONWS01;
	}
	public void setMunicipiRegistreSVDRRCCDEFUNCIONWS01(String municipiRegistreSVDRRCCDEFUNCIONWS01) {
		this.municipiRegistreSVDRRCCDEFUNCIONWS01 = municipiRegistreSVDRRCCDEFUNCIONWS01;
	}
	public String getMunicipiNaixamentSVDRRCCDEFUNCIONWS01() {
		return municipiNaixamentSVDRRCCDEFUNCIONWS01;
	}
	public void setMunicipiNaixamentSVDRRCCDEFUNCIONWS01(String municipiNaixamentSVDRRCCDEFUNCIONWS01) {
		this.municipiNaixamentSVDRRCCDEFUNCIONWS01 = municipiNaixamentSVDRRCCDEFUNCIONWS01;
	}
	public String getMunicipiNaixamentSVDRRCCMATRIMONIOWS01() {
		return municipiNaixamentSVDRRCCMATRIMONIOWS01;
	}
	public void setMunicipiNaixamentSVDRRCCMATRIMONIOWS01(String municipiNaixamentSVDRRCCMATRIMONIOWS01) {
		this.municipiNaixamentSVDRRCCMATRIMONIOWS01 = municipiNaixamentSVDRRCCMATRIMONIOWS01;
	}
	public String getMunicipiRegistreSVDRRCCMATRIMONIOWS01() {
		return municipiRegistreSVDRRCCMATRIMONIOWS01;
	}
	public void setMunicipiRegistreSVDRRCCMATRIMONIOWS01(String municipiRegistreSVDRRCCMATRIMONIOWS01) {
		this.municipiRegistreSVDRRCCMATRIMONIOWS01 = municipiRegistreSVDRRCCMATRIMONIOWS01;
	}
	public String getMunicipiRegistreSVDRRCCNACIMIENTOWS01() {
		return municipiRegistreSVDRRCCNACIMIENTOWS01;
	}
	public void setMunicipiRegistreSVDRRCCNACIMIENTOWS01(String municipiRegistreSVDRRCCNACIMIENTOWS01) {
		this.municipiRegistreSVDRRCCNACIMIENTOWS01 = municipiRegistreSVDRRCCNACIMIENTOWS01;
	}
	public String getMunicipiNaixamentSVDRRCCNACIMIENTOWS01() {
		return municipiNaixamentSVDRRCCNACIMIENTOWS01;
	}
	public void setMunicipiNaixamentSVDRRCCNACIMIENTOWS01(String municipiNaixamentSVDRRCCNACIMIENTOWS01) {
		this.municipiNaixamentSVDRRCCNACIMIENTOWS01 = municipiNaixamentSVDRRCCNACIMIENTOWS01;
	}
	public String getMunicipiNaixamentSVDDELSEXWS01() {
		return municipiNaixamentSVDDELSEXWS01;
	}
	public void setMunicipiNaixamentSVDDELSEXWS01(String municipiNaixamentSVDDELSEXWS01) {
		this.municipiNaixamentSVDDELSEXWS01 = municipiNaixamentSVDDELSEXWS01;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public boolean isCommandValidat() {
		return commandValidat;
	}
	public void setCommandValidat(boolean commandValidat) {
		this.commandValidat = commandValidat;
	}
	
}