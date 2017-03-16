package net.conselldemallorca.helium.test.util;

public class DocumentoExpedient {
	private String codi;
	private String nom;
	private boolean esPlantilla;
	private String arxiu;
	private String descripcio;
	private boolean adjuntarAutomaticamente;
	
	public boolean isAdjuntarAutomaticamente() {
		return adjuntarAutomaticamente;
	}
	public void setAdjuntarAutomaticamente(boolean adjuntarAutomaticamente) {
		this.adjuntarAutomaticamente = adjuntarAutomaticamente;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getArxiu() {
		return arxiu;
	}
	public void setArxiu(String arxiu) {
		this.arxiu = arxiu;
	}
	public boolean isEsPlantilla() {
		return esPlantilla;
	}
	public void setEsPlantilla(boolean esPlantilla) {
		this.esPlantilla = esPlantilla;
	}
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
}
