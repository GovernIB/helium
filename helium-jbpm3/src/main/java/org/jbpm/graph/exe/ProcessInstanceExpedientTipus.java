/**
 *
 */
package org.jbpm.graph.exe;

import java.io.Serializable;

import org.jbpm.graph.def.Identifiable;

/**
 * Objecte de domini que representa un tipus d'expedient de la instància de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcessInstanceExpedientTipus implements Identifiable, Serializable {

	private long id;
	private String codi;
	private String nom;
	private boolean teTitol;
	private boolean teNumero;
	private boolean restringirPerGrup;
	private boolean procedimentComu;

	public long getId() {
		return id;
	}
	public void setId(long id) {
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
	public boolean isTeTitol() {
		return teTitol;
	}
	public void setTeTitol(boolean teTitol) {
		this.teTitol = teTitol;
	}
	public boolean isTeNumero() {
		return teNumero;
	}
	public void setTeNumero(boolean teNumero) {
		this.teNumero = teNumero;
	}

	public boolean isRestringirPerGrup() {
		return restringirPerGrup;
	}
	public void setRestringirPerGrup(boolean restringirPerGrup) {
		this.restringirPerGrup = restringirPerGrup;
	}

	public boolean isProcedimentComu() {
		return procedimentComu;
	}
	public void setProcedimentComu(boolean procedimentComu) {
		this.procedimentComu = procedimentComu;
	}


	private static final long serialVersionUID = 1L;

}
