/**
 * 
 */
package net.conselldemallorca.helium.ws.tramitacio.v1;

/**
 * Informació d'un arxiu associat a un document d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuProces {

	private String nom;
	private byte[] contingut;



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
