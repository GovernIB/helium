/**
 * 
 */
package es.caib.helium.integracio.plugins.tramitacio;


/**
 * 
 * @author Limit Tecnologies
 */
public class DocumentEvent {

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
