/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

/**
 * Informació sobre la persona representada en una anotació
 * de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesRepresentat {

	private String nif;
	private String nomAmbCognoms;
	private String nom;
	private String cognom1;
	private String cognom2;

	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getNomAmbCognoms() {
		return nomAmbCognoms;
	}
	public void setNomAmbCognoms(String nomAmbCognoms) {
		this.nomAmbCognoms = nomAmbCognoms;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCognom1() {
		return cognom1;
	}
	public void setCognom1(String cognom1) {
		this.cognom1 = cognom1;
	}
	public String getCognom2() {
		return cognom2;
	}
	public void setCognom2(String cognom2) {
		this.cognom2 = cognom2;
	}
}
