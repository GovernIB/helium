/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;

/**
 * Dades per a crear el document a Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesDocumentDto {

	private String codi;
	private String titol;
	private Date data;
	private String arxiuNom;
	private byte[] arxiuContingut;
	private Long idDocument;
	
	private boolean documentValid = true;
	private String documentError;



	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
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
	public Long getIdDocument() {
		return idDocument;
	}
	public void setIdDocument(Long idDocument) {
		this.idDocument = idDocument;
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

}
