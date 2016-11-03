/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

import java.io.Serializable;

/**
 * DTO amb informació d'un valor d'una enumeració per a l'exportació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnumeracioValorExportacio implements Serializable{

	private String codi;
	private String nom;
	int ordre;

	public EnumeracioValorExportacio(String codi, String nom, int ordre) {
		this.codi = codi;
		this.nom = nom;
		this.ordre = ordre;
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
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	private static final long serialVersionUID = 1L;
}
