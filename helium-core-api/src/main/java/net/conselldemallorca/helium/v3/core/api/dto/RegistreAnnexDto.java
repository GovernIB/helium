/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;


/**
 * DTO amb informació d'un annex a una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreAnnexDto {

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
