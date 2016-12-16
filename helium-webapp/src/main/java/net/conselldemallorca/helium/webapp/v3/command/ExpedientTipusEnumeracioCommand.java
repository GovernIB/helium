package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusEnumeracioCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusEnumeracioCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.Codi;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusEnumeracio;

@ExpedientTipusEnumeracio(groups = {Creacio.class, Modificacio.class})
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
