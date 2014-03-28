package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * Objecte de domini que representa un document de la definició
 * de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccioDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String codi;
	private String nom;
//	private String descripcio;
	private String jbpmAction;
	private boolean publica;
//	private boolean oculta;
	private String rols;
//	private String cron;

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
//		return nom;
		return nom.substring(0, 1).toUpperCase() + nom.substring(1).toLowerCase();
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

	public String getJbpmAction() {
		return jbpmAction;
	}

	public void setJbpmAction(String jbpmAction) {
		this.jbpmAction = jbpmAction;
	}
}
