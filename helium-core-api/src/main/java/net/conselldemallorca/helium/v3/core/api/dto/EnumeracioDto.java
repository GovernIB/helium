/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * DTO amb informació d'una enumeració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnumeracioDto extends HeretableDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	
	/** Comptador per mostrar el número de valors. */
	private Integer numValors;

	public Long getId() {
		return id;
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
	public Integer getNumValors() {
		return numValors;
	}
	public void setNumValors(Integer numValors) {
		this.numValors = numValors;
	}

	private static final long serialVersionUID = -5244469246285004899L;
}
