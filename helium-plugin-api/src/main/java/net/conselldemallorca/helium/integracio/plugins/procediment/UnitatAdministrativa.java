package net.conselldemallorca.helium.integracio.plugins.procediment;


/**
 * Classe per retornar informació d'una unitat administrativa del plugin de procediments.
 * 
 */
public class UnitatAdministrativa {
	 
    private String codi;
    private String nom;
    private String codiDir3;
	private String pareCodi;
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCodiDir3() {
		return codiDir3;
	}
	public void setCodiDir3(String codiDir3) {
		this.codiDir3 = codiDir3;
	}
	public String getPareCodi() {
		return pareCodi;
	}
	public void setPareCodi(String pareCodi) {
		this.pareCodi = pareCodi;
	}
	
}
