/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.registre;

import java.util.Date;


/**
 * Classe que representa un annex d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreAnnex {

	private String titol;
	private String fitxerNom;
	private int fitxerTamany;
	private String fitxerTipusMime;
	private String fitxerArxiuUuid;
	private byte[] fitxerContingut;
	private Date dataCaptura;
	private String localitzacio;
	private String origenCiutadaAdmin;
	private String ntiTipusDocument;
	private String sicresTipusDocument;
	private String ntiElaboracioEstat;
	private String observacions;
	private Integer firmaMode;
	private String firmaFitxerNom;
	private int firmaFitxerTamany;
	private String firmaFitxerTipusMime;
	private String firmaFitxerArxiuUuid;
	private String firmaFitxerContingutBase64;
	private String firmaCsv;
	private String timestamp;
	private String validacioOCSP;
	
	private String tipusDocument;
	private String tipusDocumental;
	private Integer origen;
	private String validesa;


	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getFitxerNom() {
		return fitxerNom;
	}
	public void setFitxerNom(String fitxerNom) {
		this.fitxerNom = fitxerNom;
	}
	public int getFitxerTamany() {
		return fitxerTamany;
	}
	public void setFitxerTamany(int fitxerTamany) {
		this.fitxerTamany = fitxerTamany;
	}
	public String getFitxerTipusMime() {
		return fitxerTipusMime;
	}
	public void setFitxerTipusMime(String fitxerTipusMime) {
		this.fitxerTipusMime = fitxerTipusMime;
	}
	public String getFitxerArxiuUuid() {
		return fitxerArxiuUuid;
	}
	public void setFitxerArxiuUuid(String fitxerArxiuUuid) {
		this.fitxerArxiuUuid = fitxerArxiuUuid;
	}
	public byte[] getFitxerContingut() {
		return fitxerContingut;
	}
	public void setFitxerContingut(byte[] fitxerContingut) {
		this.fitxerContingut = fitxerContingut;
	}
	public Date getDataCaptura() {
		return dataCaptura;
	}
	public void setDataCaptura(Date dataCaptura) {
		this.dataCaptura = dataCaptura;
	}
	public String getLocalitzacio() {
		return localitzacio;
	}
	public void setLocalitzacio(String localitzacio) {
		this.localitzacio = localitzacio;
	}
	public String getOrigenCiutadaAdmin() {
		return origenCiutadaAdmin;
	}
	public void setOrigenCiutadaAdmin(String origenCiutadaAdmin) {
		this.origenCiutadaAdmin = origenCiutadaAdmin;
	}
	public String getNtiTipusDocument() {
		return ntiTipusDocument;
	}
	public void setNtiTipusDocument(String ntiTipusDocument) {
		this.ntiTipusDocument = ntiTipusDocument;
	}
	public String getSicresTipusDocument() {
		return sicresTipusDocument;
	}
	public void setSicresTipusDocument(String sicresTipusDocument) {
		this.sicresTipusDocument = sicresTipusDocument;
	}
	public String getNtiElaboracioEstat() {
		return ntiElaboracioEstat;
	}
	public void setNtiElaboracioEstat(String ntiElaboracioEstat) {
		this.ntiElaboracioEstat = ntiElaboracioEstat;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public Integer getFirmaMode() {
		return firmaMode;
	}
	public void setFirmaMode(Integer firmaMode) {
		this.firmaMode = firmaMode;
	}
	public String getFirmaFitxerNom() {
		return firmaFitxerNom;
	}
	public void setFirmaFitxerNom(String firmaFitxerNom) {
		this.firmaFitxerNom = firmaFitxerNom;
	}
	public int getFirmaFitxerTamany() {
		return firmaFitxerTamany;
	}
	public void setFirmaFitxerTamany(int firmaFitxerTamany) {
		this.firmaFitxerTamany = firmaFitxerTamany;
	}
	public String getFirmaFitxerTipusMime() {
		return firmaFitxerTipusMime;
	}
	public void setFirmaFitxerTipusMime(String firmaFitxerTipusMime) {
		this.firmaFitxerTipusMime = firmaFitxerTipusMime;
	}
	public String getFirmaFitxerArxiuUuid() {
		return firmaFitxerArxiuUuid;
	}
	public void setFirmaFitxerArxiuUuid(String firmaFitxerArxiuUuid) {
		this.firmaFitxerArxiuUuid = firmaFitxerArxiuUuid;
	}
	public String getFirmaFitxerContingutBase64() {
		return firmaFitxerContingutBase64;
	}
	public void setFirmaFitxerContingutBase64(String firmaFitxerContingutBase64) {
		this.firmaFitxerContingutBase64 = firmaFitxerContingutBase64;
	}
	public String getFirmaCsv() {
		return firmaCsv;
	}
	public void setFirmaCsv(String firmaCsv) {
		this.firmaCsv = firmaCsv;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getValidacioOCSP() {
		return validacioOCSP;
	}
	public void setValidacioOCSP(String validacioOCSP) {
		this.validacioOCSP = validacioOCSP;
	}
	public String getTipusDocument() {
		return tipusDocument;
	}
	public void setTipusDocument(String tipusDocument) {
		this.tipusDocument = tipusDocument;
	}
	public String getTipusDocumental() {
		return tipusDocumental;
	}
	public void setTipusDocumental(String tipusDocumental) {
		this.tipusDocumental = tipusDocumental;
	}
	public Integer getOrigen() {
		return origen;
	}
	public void setOrigen(Integer origen) {
		this.origen = origen;
	}
	public String getValidesa() {
		return validesa;
	}
	public void setValidesa(String validesa) {
		this.validesa = validesa;
	}

}
