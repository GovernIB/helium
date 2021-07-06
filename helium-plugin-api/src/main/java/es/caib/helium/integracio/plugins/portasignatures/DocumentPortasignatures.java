/**
 * 
 */
package es.caib.helium.integracio.plugins.portasignatures;

/**
 * Document per enviar al portasignatures
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentPortasignatures {

	private String titol;
	private String arxiuNom;
	private byte[] arxiuContingut;
	private Integer tipus;
	private boolean isSignat;
	private String reference;
	private String descripcio;



	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
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
	public Integer getTipus() {
		return tipus;
	}
	public void setTipus(Integer tipus) {
		this.tipus = tipus;
	}
	public boolean isSignat() {
		return isSignat;
	}
	public void setSignat(boolean isSignat) {
		this.isSignat = isSignat;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

}
