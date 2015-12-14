/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;



/**
 * DTO amb informació d'una formulari per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FormExportacio implements Serializable {

	private String codi;
	private String nom;
	private Set<CampTascaExportacio> camps = new HashSet<CampTascaExportacio>();



	public FormExportacio(String codi, String nom) {
		this.codi = codi;
		this.nom = nom;
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
	public Set<CampTascaExportacio> getCamps() {
		return camps;
	}
	public void setCamps(Set<CampTascaExportacio> camps) {
		this.camps = camps;
	}
	public void addCamp(CampTascaExportacio camp) {
		camps.add(camp);
	}
	public void removeCamp(CampTascaExportacio camp) {
		camps.remove(camp);
	}



	private static final long serialVersionUID = 1L;

}
