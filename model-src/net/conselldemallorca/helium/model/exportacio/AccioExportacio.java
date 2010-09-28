/**
 * 
 */
package net.conselldemallorca.helium.model.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'una acció per exportar
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class AccioExportacio implements Serializable {

	private String codi;
	private String nom;
	private String descripcio;
	private String jbpmAction;



	public AccioExportacio(
			String codi,
			String nom,
			String descripcio,
			String jbpmAction) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.jbpmAction = jbpmAction;
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
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getJbpmAction() {
		return jbpmAction;
	}
	public void setJbpmAction(String jbpmAction) {
		this.jbpmAction = jbpmAction;
	}



	private static final long serialVersionUID = 1L;

}
