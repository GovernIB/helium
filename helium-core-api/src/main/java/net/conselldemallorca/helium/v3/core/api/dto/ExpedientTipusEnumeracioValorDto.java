package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

public class ExpedientTipusEnumeracioValorDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private int ordre;
	
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
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	private static final long serialVersionUID = -8382718235731957547L;
}