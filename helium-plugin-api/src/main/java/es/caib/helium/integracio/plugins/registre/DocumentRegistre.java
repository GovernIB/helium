/**
 * 
 */
package es.caib.helium.integracio.plugins.registre;

import java.util.Date;

/**
 * Informació sobre un document que es registra.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentRegistre {

	private String nom;
	private Date data;
	private String idiomaCodi;
	private String arxiuNom;
	private byte[] arxiuContingut;
	
	private String tipusDocument;
	private String tipusDocumental;
	private Integer origen;
	private Integer modeFirma;
	private String observacions;
	private String validesa;

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getIdiomaCodi() {
		return idiomaCodi;
	}
	public void setIdiomaCodi(String idiomaCodi) {
		this.idiomaCodi = idiomaCodi;
	}
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}
	public byte[] getArxiuContingut() {
		return arxiuContingut;
	}
	public void setArxiuContingut(byte[] arxiuContingut) {
		this.arxiuContingut = arxiuContingut;
	}
	public String getTipusDocument() {
		return tipusDocument;
	}
	public void setTipusDocument(
			String tipusDocument) {
		this.tipusDocument = tipusDocument;
	}
	public String getTipusDocumental() {
		return tipusDocumental;
	}
	public void setTipusDocumental(
			String tipusDocumental) {
		this.tipusDocumental = tipusDocumental;
	}
	public Integer getOrigen() {
		return origen;
	}
	public void setOrigen(
			Integer origen) {
		this.origen = origen;
	}
	public Integer getModeFirma() {
		return modeFirma;
	}
	public void setModeFirma(
			Integer modeFirma) {
		this.modeFirma = modeFirma;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(
			String observacions) {
		this.observacions = observacions;
	}
	public String getValidesa() {
		return validesa;
	}
	public void setValidesa(String validesa) {
		this.validesa = validesa;
	}

}
