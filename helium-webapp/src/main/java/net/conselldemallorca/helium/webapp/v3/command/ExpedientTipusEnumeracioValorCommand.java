package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioValorDto;

public class ExpedientTipusEnumeracioValorCommand {

	private Long expedientTipusId;
	private Long enumeracioId;
	private Long id;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 64, groups = {Creacio.class, Modificacio.class})
	private String codi;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String nom;
	private int ordre;
	
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public Long getEnumeracioId() {
		return enumeracioId;
	}
	public void setEnumeracioId(Long enumeracioId) {
		this.enumeracioId = enumeracioId;
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
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}
	
	public static ExpedientTipusEnumeracioValorDto asExpedientTipusEnumeracioValorDto(ExpedientTipusEnumeracioValorCommand command) {
		ExpedientTipusEnumeracioValorDto dto = new ExpedientTipusEnumeracioValorDto();
		dto.setId(command.getId());
		dto.setCodi(command.getCodi());
		dto.setNom(command.getNom());
		dto.setOrdre(command.getOrdre());
		return dto;
	}
	
	public interface Creacio {}
	public interface Modificacio {}
}
