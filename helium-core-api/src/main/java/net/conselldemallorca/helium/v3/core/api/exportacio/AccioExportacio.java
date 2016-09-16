/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

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
	private String defprocJbpmKey;
	private String jbpmAction;
	private boolean publica;
	private boolean oculta;
	private String rols;



	public AccioExportacio(
			String codi,
			String nom,
			String descripcio,
			String defprocJbpmKey,
			String jbpmAction,
			boolean publica,
			boolean oculta,
			String rols) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.defprocJbpmKey = defprocJbpmKey;
		this.jbpmAction = jbpmAction;
		this.publica = publica;
		this.oculta = oculta;
		this.rols = rols;
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
	public String getDefprocJbpmKey() {
		return defprocJbpmKey;
	}

	public void setDefprocJbpmKey(String defprocJbpmKey) {
		this.defprocJbpmKey = defprocJbpmKey;
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
	public String getRols() {
		return rols;
	}
	public void setRols(String rols) {
		this.rols = rols;
	}



	private static final long serialVersionUID = 1L;

}
