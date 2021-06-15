/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URLConnection;

/**
 * DTO amb informació d'un arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArxiuDto {

	private String nom;
	private byte[] contingut;
	private String tipusMime;

	public ArxiuDto(
			String nom,
			byte[] contingut) {
		this.nom = nom;
		this.contingut = contingut;
		this.tipusMime = URLConnection.guessContentTypeFromName(nom);
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
