/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

/**
 * Command que representa el formulari d'administració de configuració paràmetres de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ParametresCommand {

	boolean propagarEsborratExpedients;
	private String nom;
	private String codi;
	private String descripcio;
	private String valor;
	
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public boolean isPropagarEsborratExpedients() {
		return propagarEsborratExpedients;
	}
	public void setPropagarEsborratExpedients(boolean propagarEsborratExpedients) {
		this.propagarEsborratExpedients = propagarEsborratExpedients;
	}
	

}
