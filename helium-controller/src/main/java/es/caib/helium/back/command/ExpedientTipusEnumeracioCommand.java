package es.caib.helium.back.command;

import es.caib.helium.back.validator.Codi;
import es.caib.helium.back.validator.ExpedientTipusEnumeracio;
import es.caib.helium.logic.intf.dto.EnumeracioDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@ExpedientTipusEnumeracio(groups = {ExpedientTipusEnumeracioCommand.Creacio.class, ExpedientTipusEnumeracioCommand.Modificacio.class})
public class ExpedientTipusEnumeracioCommand {

	private Long expedientTipusId;
	private Long id;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 64, groups = {Creacio.class, Modificacio.class})
	@Codi(groups = {Creacio.class, Modificacio.class})
	private String codi;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String nom;

	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
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
	
	public static EnumeracioDto asEnumeracioDto(ExpedientTipusEnumeracioCommand command) {
		EnumeracioDto dto = new EnumeracioDto();
		dto.setId(command.getId());
		dto.setCodi(command.getCodi());
		dto.setNom(command.getNom());
		return dto;
	}	
	
	public interface Creacio {}
	public interface Modificacio {}
}
