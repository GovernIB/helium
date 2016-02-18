/**
 *
 */
package org.jbpm.graph.exe;

import java.io.Serializable;

import org.jbpm.graph.def.Identifiable;

/**
 * Objecte de domini que representa un entorn de la instància de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcessInstanceEntorn implements Identifiable, Serializable {

	private long id;
	private String codi;
	private String nom;

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

	private static final long serialVersionUID = 1L;

}
