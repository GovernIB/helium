/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;


/**
 * DTO amb informació d'un estat de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EstatDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String codi;
	private String nom;

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

}
