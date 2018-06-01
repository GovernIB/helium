/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.net.URLConnection;

/**
 * DTO amb informació d'un arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuDto {

	private String nom;
	private byte[] contingut;
	private String tipusMime;

	public ArxiuDto() {}
	public ArxiuDto(
			String nom,
			byte[] contingut) {
		this.nom = nom;
		this.contingut = contingut;
		this.tipusMime = URLConnection.guessContentTypeFromName(nom);
	}
	public ArxiuDto(
			String nom,
			byte[] contingut,
			String tipusMime) {
		this.nom = nom;
		this.contingut = contingut;
		this.tipusMime = tipusMime;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public byte[] getContingut() {
		return contingut;
	}
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
	}
	public String getTipusMime() {
		return tipusMime;
	}
	public void setTipusMime(String tipusMime) {
		this.tipusMime = tipusMime;
	}

	public long getTamany() {
		return (contingut != null) ? contingut.length : 0;
	}

	public String getExtensio() {
		int indexPunt = nom.lastIndexOf(".");
		if (indexPunt != -1 && indexPunt < nom.length() - 1) {
			return nom.substring(indexPunt + 1);
		} else {
			return null;
		}
	}

}
