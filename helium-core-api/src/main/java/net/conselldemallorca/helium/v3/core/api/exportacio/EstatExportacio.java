/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

import java.io.Serializable;


/**
 * DTO amb informació d'un estat de l'expedient per a l'exportació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EstatExportacio implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String codi;
	private String nom;
	private int ordre;
	
	public EstatExportacio(String codi, String nom, int ordre) {
		super();
		this.codi = codi;
		this.nom = nom;
		this.ordre = ordre;
	}

	public EstatExportacio() {
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

}
