/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;

import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusMapeig;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioTramitsMapeigCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioTramitsMapeigCommand.Modificacio;

/**
 * Command per afegir un mapeig a la integració amb tràmits de Sistra.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusMapeig(groups = {Creacio.class, Modificacio.class})
public class ExpedientTipusIntegracioTramitsMapeigCommand {
	
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private Long expedientTipusId;
	private Long id;
	private String codiHelium;
	private String codiSistra;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private TipusMapeig tipus;
	private boolean evitarSobreescriptura;
	
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
	public boolean isEvitarSobreescriptura() {
		return evitarSobreescriptura;
	}
	public void setEvitarSobreescriptura(boolean evitarSobreescriptura) {
		this.evitarSobreescriptura = evitarSobreescriptura;
	}
	
	public static MapeigSistraDto asMapeigSistraDto(ExpedientTipusIntegracioTramitsMapeigCommand command) {
		MapeigSistraDto dto = new MapeigSistraDto();
		dto.setId(command.getId());
		dto.setCodiHelium(command.getCodiHelium());
		dto.setCodiSistra(command.getCodiSistra());
		dto.setTipus(command.getTipus());
		dto.setEvitarSobreescriptura(command.isEvitarSobreescriptura());
		return dto;
	}

	public interface Creacio {}
	public interface Modificacio {}
}
