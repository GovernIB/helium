package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * Objecte de domini que representa un document de la definició
 * de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccioDto extends HeretableDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private AccioTipusEnumDto tipus;
	private String defprocJbpmKey;
	private String jbpmAction;
	private String predefinitCodi;
	private String predefinitDades;
	private String script;
	private boolean publica;
	private boolean oculta;
	private String rols;

	public Long getId() {
		return id;
	}

	public String getRols() {
		return rols;
	}

	public void setRols(String rols) {
		this.rols = rols;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getJbpmAction() {
		return jbpmAction;
	}

	public void setJbpmAction(String jbpmAction) {
		this.jbpmAction = jbpmAction;
	}

	public String getDefprocJbpmKey() {
		return defprocJbpmKey;
	}

	public void setDefprocJbpmKey(String defprocJbpmKey) {
		this.defprocJbpmKey = defprocJbpmKey;
	}

	public String getDescripcio() {
		return descripcio;
	}

	public AccioTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(AccioTipusEnumDto tipus) {
		this.tipus = tipus;
	}

	public String getPredefinitCodi() {
		return predefinitCodi;
	}

	public void setPredefinitCodi(String predefinitCodi) {
		this.predefinitCodi = predefinitCodi;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	
	public String getPredefinitDades() {
		return predefinitDades;
	}

	public void setPredefinitDades(String predefinitDades) {
		this.predefinitDades = predefinitDades;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
}
