/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.notificacio;


/**
 * Informació d'una persona per a un enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Persona {

	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String nif;
	private String telefon;
	private String email;
	private InteressatTipusEnum tipus;
	private String codiDir3;
	private String raoSocial;

	
	public InteressatTipusEnum getTipus() {
		return tipus;
	}
	public void setTipus(InteressatTipusEnum tipus) {
		this.tipus = tipus;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCodiDir3() {
		return codiDir3;
	}
	public void setCodiDir3(String codiDir3) {
		this.codiDir3 = codiDir3;
	}
	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(String raoSocial) {
		this.raoSocial = raoSocial;
	}
	

}
