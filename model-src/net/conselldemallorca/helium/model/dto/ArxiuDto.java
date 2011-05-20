/**
 * 
 */
package net.conselldemallorca.helium.model.dto;



/**
 * DTO amb informació d'un arxiu per descarregar
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ArxiuDto {

	private String nom;
	private byte[] contingut;

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

	private static final long serialVersionUID = 1L;

}
