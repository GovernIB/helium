/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'una acció per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccioExportacio implements Serializable {

	private String codi;
	private String nom;
	private String descripcio;
	private String jbpmAction;
	private boolean publica;
	private boolean oculta;



	public AccioExportacio(
			String codi,
			String nom,
			String descripcio,
			String jbpmAction,
			boolean publica,
			boolean oculta) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.jbpmAction = jbpmAction;
		this.publica = publica;
		this.oculta = oculta;
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
	public boolean isPublica() {
		return publica;
	}
	public void setPublica(boolean publica) {
		this.publica = publica;
	}
	public boolean isOculta() {
		return oculta;
	}
	public void setOculta(boolean oculta) {
		this.oculta = oculta;
	}



	private static final long serialVersionUID = 1L;

}
