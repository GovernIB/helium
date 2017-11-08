/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

/**
 * Command per gestionar els documents d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentExpedientCommand {

	private String documentCodi;
	
	private Long docId;
	private Date data;
	private String nomArxiu;
	private MultipartFile arxiu;
	private String nom;
	private String codi;
	
	// Meta dades NTI
	private String ntiTipusDocumental;
	private String ntiTipoFirma;
	private String ntiSerieDocumental;
	private String ntiValorCsv;
	private String ntiDefGenCsv;
	private String ntiIdOrigen;

	public DocumentExpedientCommand() {}

	public Long getDocId() {
		return docId;
	}
	public void setDocId(Long docId) {
		this.docId = docId;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public MultipartFile getArxiu() {
		return arxiu;
	}
	public void setArxiu(MultipartFile arxiu) {
		this.arxiu = arxiu;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNomArxiu() {
		return nomArxiu;
	}
	public void setNomArxiu(String nomArxiu) {
		this.nomArxiu = nomArxiu;
	}
	public String getDocumentCodi() {
 		return documentCodi;
 	}
 	public void setDocumentCodi(String documentCodi) {
 		this.documentCodi = documentCodi;
 	}

	public String getNtiTipusDocumental() {
		return ntiTipusDocumental;
	}

	public void setNtiTipusDocumental(String ntiTipusDocumental) {
		this.ntiTipusDocumental = ntiTipusDocumental;
	}

	public String getNtiTipoFirma() {
		return ntiTipoFirma;
	}

	public void setNtiTipoFirma(String ntiTipoFirma) {
		this.ntiTipoFirma = ntiTipoFirma;
	}
	
	public String getNtiSerieDocumental() {
		return ntiSerieDocumental;
	}
	public void setNtiSerieDocumental(String ntiSerieDocoumental) {
		this.ntiSerieDocumental = ntiSerieDocoumental;
	}
	
	public String getNtiValorCsv() {
		return ntiValorCsv;
	}

	public void setNtiValorCsv(String ntiValorCsv) {
		this.ntiValorCsv = ntiValorCsv;
	}

	public String getNtiDefGenCsv() {
		return ntiDefGenCsv;
	}

	public void setNtiDefGenCsv(String ntiDefGenCsv) {
		this.ntiDefGenCsv = ntiDefGenCsv;
	}

	public String getNtiIdOrigen() {
		return ntiIdOrigen;
	}

	public void setNtiIdOrigen(String ntiidOrigen) {
		this.ntiIdOrigen = ntiidOrigen;
	}
}
