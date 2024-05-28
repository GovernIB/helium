/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * DTO amb informació d'un document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentStoreDto implements Serializable {
	
	private Long id;
	private boolean adjunt = false;
	private String codiDocument;
	private String nom;
	private String arxiuNom;
	private String ntiVersion;
	private String ntiIdentificador;
	private String ntiOrgan;
	private String ntiOrigen;
	private String ntiEstatElaboracio;
	private String ntiNomFormat;
	private String ntiTipusDocumental;
	
	private String ntiTipoFirma;
	private String ntiValorCsv;
	private String ntiDefGenCsv;
	
	private Date dataCreacio;
	
	private String ntiIdDocOrigen;
	
	/** Indica si en la consulta Distribucio el marca com a válid o invàlid */
	private boolean documentValid;
	
	/** Camp on distribucio informa dels possibles errors que pugui tenir el document. */
	private String documentError;
	
	/** Llista de documents continguts en el zip guardats a laa taula hel_document_contingut. S'usa en les notificacions de zips. */
	private List<DocumentStoreDto> continguts = new ArrayList<DocumentStoreDto>();
	
	/** Llista de documents zip que contenen aquest document guardats a la taula hel_document_contingut. S'usa en les notificacions de zips. */
	private List<DocumentStoreDto> zips = new ArrayList<DocumentStoreDto>();

	
	public String getNtiVersion() {
		return ntiVersion;
	}
	public void setNtiVersion(String ntiVersion) {
		this.ntiVersion = ntiVersion;
	}
	
	public String getNtiIdentificador() {
		return ntiIdentificador;
	}
	public void setNtiIdentificador(String ntiIdentificador) {
		this.ntiIdentificador = ntiIdentificador;
	}
	
	public String getNtiOrgan() {
		return ntiOrgan;
	}
	public void setNtiOrgan(String ntiOrgan) {
		this.ntiOrgan = ntiOrgan;
	}
	
	public String getNtiOrigen() {
		return ntiOrigen;
	}
	public void setNtiOrigen(String ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}
	
	public String getNtiEstatElaboracio() {
		return ntiEstatElaboracio;
	}
	public void setNtiEstatElaboracio(String ntiEstatElaboracio) {
		this.ntiEstatElaboracio = ntiEstatElaboracio;
	}
	
	public String getNtiNomFormat() {
		return ntiNomFormat;
	}
	public void setNtiNomFormat(String ntiNomFormat) {
		this.ntiNomFormat = ntiNomFormat;
	}
	
	public String getNtiTipusDocumental() {
		return ntiTipusDocumental;
	}
	public void setNtiTipusDocumental(String ntiTipusDocumental) {
		this.ntiTipusDocumental = ntiTipusDocumental;
	}
	
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}
	
	public String getNtiTipoFirma() {
		return ntiTipoFirma;
	}
	public void setNtiTipoFirma(String ntiTipoFirma) {
		this.ntiTipoFirma = ntiTipoFirma;
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
	
	public String getNtiIdDocOrigen() {
		return ntiIdDocOrigen;
	}
	public void setNtiIdDocOrigen(String ntiIdDocOrigen) {
		this.ntiIdDocOrigen = ntiIdDocOrigen;
	}
	
	
	public boolean isDocumentValid() {
		return documentValid;
	}
	public void setDocumentValid(boolean documentValid) {
		this.documentValid = documentValid;
	}
	public String getDocumentError() {
		return documentError;
	}
	public void setDocumentError(String documentError) {
		this.documentError = documentError;
	}

	public List<DocumentStoreDto> getContinguts() {
		return continguts;
	}
	public void setContinguts(List<DocumentStoreDto> continguts) {
		this.continguts = continguts;
	}
	public List<DocumentStoreDto> getZips() {
		return zips;
	}
	public void setZips(List<DocumentStoreDto> zips) {
		this.zips = zips;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean isAdjunt() {
		return adjunt;
	}
	public void setAdjunt(boolean adjunt) {
		this.adjunt = adjunt;
	}
	public String getCodiDocument() {
		return codiDocument;
	}
	public void setCodiDocument(String codiDocument) {
		this.codiDocument = codiDocument;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}


	private static final long serialVersionUID = 774909297938469787L;
	
}
