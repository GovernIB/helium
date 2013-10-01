/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;


/**
 * DTO amb informació d'un tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusDto implements Serializable, GenericEntityDto<Long> {
	private static final long serialVersionUID = 4990928454645567913L;
	
	private Long id;
	private String codi;
	private String nom;
	private boolean teNumero;
	private boolean teTitol;

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
	public boolean isTeNumero() {
		return teNumero;
	}
	public void setTeNumero(boolean teNumero) {
		this.teNumero = teNumero;
	}
	public boolean isTeTitol() {
		return teTitol;
	}
	public void setTeTitol(boolean teTitol) {
		this.teTitol = teTitol;
	}

}
