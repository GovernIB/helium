/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * DTO amb informació d'una document de la zonaper.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ZonaperDocumentDto {

	public enum DocumentEventTipus {
		ARXIU,
		REFGD
	}

	protected String nom;
	protected DocumentEventTipus tipus;
	protected String referencia;
	protected String arxiuNom;
	protected byte[] arxiuContingut;

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public DocumentEventTipus getTipus() {
		return tipus;
	}
	public void setTipus(DocumentEventTipus tipus) {
		this.tipus = tipus;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
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

}
