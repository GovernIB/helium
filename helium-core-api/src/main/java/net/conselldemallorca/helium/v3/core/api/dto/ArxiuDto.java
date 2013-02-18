/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;



/**
 * DTO amb informació d'un arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuDto {

	private String nom;
	private byte[] contingut;

	public ArxiuDto() {
	}
	public ArxiuDto(String nom, byte[] contingut) {
		this.nom = nom;
		this.contingut = contingut;
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

}
