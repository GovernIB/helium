/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

import java.io.Serializable;

import net.conselldemallorca.helium.v3.core.api.dto.AccioTipusEnumDto;



/**
 * DTO amb informació d'una acció per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccioExportacio implements Serializable {

	private String codi;
	private String nom;
	private String descripcio;
	private AccioTipusEnumDto tipus;
	private String defprocJbpmKey;
	private String jbpmAction;
	private boolean publica;
	private boolean oculta;
	private String rols;
	private String handlerClasse;
	private String handlerDades;
	private String script;



	public AccioExportacio(
			String codi,
			String nom,
			String descripcio,
			AccioTipusEnumDto tipus,
			String defprocJbpmKey,
			String jbpmAction,
			String handlerClasse,
			String handlerDades,
			String script,
			boolean publica,
			boolean oculta,
			String rols) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.tipus = tipus;
		this.defprocJbpmKey = defprocJbpmKey;
		this.jbpmAction = jbpmAction;
		this.handlerClasse = handlerClasse;
		this.handlerDades = handlerDades;
		this.setScript(script);
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

	public AccioTipusEnumDto getTipus() {
		return tipus;
	}

	public void setTipus(AccioTipusEnumDto tipus) {
		this.tipus = tipus;
	}

	public String getHandlerClasse() {
		return handlerClasse;
	}

	public void setHandlerClasse(String handlerClasse) {
		this.handlerClasse = handlerClasse;
	}

	public String getHandlerDades() {
		return handlerDades;
	}

	public void setHandlerDades(String handlerDades) {
		this.handlerDades = handlerDades;
	}



	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}



	private static final long serialVersionUID = 1L;

}
