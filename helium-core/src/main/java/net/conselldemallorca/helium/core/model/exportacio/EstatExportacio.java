/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'un estat d'un tipus d'expedient per
 * exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EstatExportacio implements Serializable {

	private String codi;
	private String nom;
	private int ordre;



	public EstatExportacio(
			String codi,
			String nom,
			int ordre) {
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
