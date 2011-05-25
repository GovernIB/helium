/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'una enumeracio per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnumeracioExportacio implements Serializable {

	private String codi;
	private String nom;
	private String valors;



	public EnumeracioExportacio(
			String codi,
			String nom,
			String valors) {
		this.codi = codi;
		this.nom = nom;
		this.valors = valors;
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
	public String getValors() {
		return valors;
	}
	public void setValors(String valors) {
		this.valors = valors;
	}



	private static final long serialVersionUID = 1L;

}
