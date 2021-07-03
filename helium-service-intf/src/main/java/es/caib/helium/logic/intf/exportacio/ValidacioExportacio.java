/**
 * 
 */
package es.caib.helium.logic.intf.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'una validació per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidacioExportacio implements Serializable {

	private String nom;
	private String expressio;
	private String missatge;
	int ordre;



	public ValidacioExportacio(String nom, String expressio, String missatge, int ordre) {
		this.nom = nom;
		this.expressio = expressio;
		this.missatge = missatge;
		this.ordre = ordre;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getExpressio() {
		return expressio;
	}
	public void setExpressio(String expressio) {
		this.expressio = expressio;
	}
	public String getMissatge() {
		return missatge;
	}
	public void setMissatge(String missatge) {
		this.missatge = missatge;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}



	private static final long serialVersionUID = 1L;

}
