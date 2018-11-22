/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

public class ExpedientTipusEstadisticaDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String codi;
	private String nom;
	private String anyInici;
	private Long n;
	
	public ExpedientTipusEstadisticaDto(
			Long id,
			String codi,
			String nom,
			Long n,
			String anyInici
			) {
		this.id = id;
		this.codi = codi;
		this.nom = nom;
		this.n = n;
		this.anyInici = anyInici;
	}

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

	public String getAnyInici() {
		return anyInici;
	}

	public void setAnyInici(String anyInici) {
		this.anyInici = anyInici;
	}

	public Long getN() {
		return n;
	}

	public void setN(Long n) {
		this.n = n;
	}	
}