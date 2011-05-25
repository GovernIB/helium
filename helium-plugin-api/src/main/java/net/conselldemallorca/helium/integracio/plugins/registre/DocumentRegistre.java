/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

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

}
