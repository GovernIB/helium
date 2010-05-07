/**
 * 
 */
package net.conselldemallorca.helium.integracio.bantel.plugin;

import java.util.Date;

/**
 * Dades per a crear el document a Helium.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DadesDocument {

	private String codi;
	private Date data;
	private String arxiuNom;
	private byte[] arxiuContingut;
	private Long idDocument;



	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
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

}
