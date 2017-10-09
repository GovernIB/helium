package net.conselldemallorca.helium.integracio.plugins.registre;

public class Llibre {

	private String codi;
	private String organisme;
	private String nomCurt;
	private String nomLlarg;
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(
			String codi) {
		this.codi = codi;
	}
	public String getOrganisme() {
		return organisme;
	}
	public void setOrganisme(
			String organisme) {
		this.organisme = organisme;
	}
	public String getNomCurt() {
		return nomCurt;
	}
	public void setNomCurt(
			String nomCurt) {
		this.nomCurt = nomCurt;
	}
	public String getNomLlarg() {
		return nomLlarg;
	}
	public void setNomLlarg(
			String nomLlarg) {
		this.nomLlarg = nomLlarg;
	}
}
