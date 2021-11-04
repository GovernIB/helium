package net.conselldemallorca.helium.integracio.plugins.firma;

/**
 * Informació de la resposta de la firma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaResposta {

	private byte[] contingut;
	private String nom;
	private String mime;
	private String tipusFirma;
	private String tipusFirmaEni;
	private String perfilFirmaEni;
	
	public byte[] getContingut() {
		return contingut;
	}
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getMime() {
		return mime;
	}
	public void setMime(String mime) {
		this.mime = mime;
	}
	public String getTipusFirma() {
		return tipusFirma;
	}
	public void setTipusFirma(String tipusFirma) {
		this.tipusFirma = tipusFirma;
	}
	public String getTipusFirmaEni() {
		return tipusFirmaEni;
	}
	public void setTipusFirmaEni(String tipusFirmaEni) {
		this.tipusFirmaEni = tipusFirmaEni;
	}
	public String getPerfilFirmaEni() {
		return perfilFirmaEni;
	}
	public void setPerfilFirmaEni(String perfilFirmaEni) {
		this.perfilFirmaEni = perfilFirmaEni;
	}
	
}
