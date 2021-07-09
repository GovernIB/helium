/**
 * 
 */
package es.caib.helium.back.command;

import es.caib.helium.back.validator.ExpedientTipusMapeig;
import es.caib.helium.logic.intf.dto.MapeigSistraDto;
import es.caib.helium.logic.intf.dto.MapeigSistraDto.TipusMapeig;

import javax.validation.constraints.NotNull;

/**
 * Command per afegir un mapeig a la integració amb tràmits de Sistra.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusMapeig(groups = {ExpedientTipusIntegracioTramitsMapeigCommand.Creacio.class, ExpedientTipusIntegracioTramitsMapeigCommand.Modificacio.class})
public class ExpedientTipusIntegracioTramitsMapeigCommand {
	
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private Long expedientTipusId;
	private Long id;
	private String codiHelium;
	private String codiSistra;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private TipusMapeig tipus;
	
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
	public String getCodiHelium() {
		return codiHelium;
	}
	public void setCodiHelium(String codiHelium) {
		this.codiHelium = codiHelium;
	}
	public String getCodiSistra() {
		return codiSistra;
	}
	public void setCodiSistra(String codiSistra) {
		this.codiSistra = codiSistra;
	}
	public TipusMapeig getTipus() {
		return tipus;
	}
	public void setTipus(TipusMapeig tipus) {
		this.tipus = tipus;
	}
	
	public static MapeigSistraDto asMapeigSistraDto(ExpedientTipusIntegracioTramitsMapeigCommand command) {
		MapeigSistraDto dto = new MapeigSistraDto();
		dto.setId(command.getId());
		dto.setCodiHelium(command.getCodiHelium());
		dto.setCodiSistra(command.getCodiSistra());
		dto.setTipus(command.getTipus());
		return dto;
	}

	public interface Creacio {}
	public interface Modificacio {}
}
