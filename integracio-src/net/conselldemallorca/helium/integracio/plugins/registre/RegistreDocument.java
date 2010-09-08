/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

import java.io.Serializable;

/**
 * Classe que representa el document associat amb el seient registral
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class RegistreDocument implements Serializable {

	public enum IdiomaRegistre {
		CA,
		ES
	}

	private String data;
	private String tipus;
	private IdiomaRegistre idiomaDocument;
	private IdiomaRegistre idiomaExtracte;
	private String extracte;



	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public IdiomaRegistre getIdiomaDocument() {
		return idiomaDocument;
	}
	public void setIdiomaDocument(IdiomaRegistre idiomaDocument) {
		this.idiomaDocument = idiomaDocument;
	}
	public IdiomaRegistre getIdiomaExtracte() {
		return idiomaExtracte;
	}
	public void setIdiomaExtracte(IdiomaRegistre idiomaExtracte) {
		this.idiomaExtracte = idiomaExtracte;
	}
	public String getExtracte() {
		return extracte;
	}
	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}



	private static final long serialVersionUID = 1L;

}
