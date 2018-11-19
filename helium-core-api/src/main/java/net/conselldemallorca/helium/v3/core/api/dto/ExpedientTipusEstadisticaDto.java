package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.Set;

public class ExpedientTipusEstadisticaDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String expedientAny;
	private Long totalYear;
	private String nom;
	private Set<ExpedientDto> expedients;
	
	public ExpedientTipusEstadisticaDto(
			String nom
//			Set<Expedient> expedients
			) {
		this.nom = nom;
//		this.expedients = expedients;
	}
	public String getExpedientAny() {
		return expedientAny;
	}
	public void setExpedientAny(String expedientAny) {
		this.expedientAny = expedientAny;
	}
	public Long getTotalYear() {
		return totalYear;
	}
	public Set<ExpedientDto> getExpedients() {
		return expedients;
	}
	public void setExpedients(Set<ExpedientDto> expedients) {
		this.expedients = expedients;
	}
	public void setTotalYear(Long totalYear) {
		this.totalYear = totalYear;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
}
