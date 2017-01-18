/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitSistraDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitSistraEnumDto;

/**
 * Command per modificar les dades d'integració dels tipus d'expedient amb Sistra.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusIntegracioTramitsSistraCommand {

	@NotNull(groups = {Modificacio.class})
	private Long id;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	@Size(max = 64, groups = {Creacio.class, Modificacio.class})
	private String sistraTramitCodi;
	private TramitSistraEnumDto tipus;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String codiVarIdentificadorExpedient;
	private Long accioId;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private Long expedientTipusId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSistraTramitCodi() {
		return sistraTramitCodi;
	}
	public void setSistraTramitCodi(String sistraTramitCodi) {
		this.sistraTramitCodi = sistraTramitCodi;
	}
	public TramitSistraEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(TramitSistraEnumDto tipus) {
		this.tipus = tipus;
	}
	public String getCodiVarIdentificadorExpedient() {
		return codiVarIdentificadorExpedient;
	}
	public void setCodiVarIdentificadorExpedient(String codiVarIdentificadorExpedient) {
		this.codiVarIdentificadorExpedient = codiVarIdentificadorExpedient;
	}
	public Long getAccioId() {
		return accioId;
	}
	public void setAccioId(Long accioId) {
		this.accioId = accioId;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	
	public static TramitSistraDto asTramitSistraDto(ExpedientTipusIntegracioTramitsSistraCommand command) {
		TramitSistraDto dto = new TramitSistraDto();
		dto.setId(command.getId());
		dto.setSistraTramitCodi(command.getSistraTramitCodi());
		dto.setTipus(command.getTipus());
		dto.setCodiVarIdentificadorExpedient(command.getCodiVarIdentificadorExpedient());
		
		if(command.getAccioId() != null) {
			AccioDto accioDto = new AccioDto();
			accioDto.setId(command.getAccioId());
			dto.setAccio(accioDto);
		}
		
		if(command.getExpedientTipusId() != null) {
			ExpedientTipusDto expedientTipusDto = new ExpedientTipusDto();
			expedientTipusDto.setId(command.getExpedientTipusId());
			dto.setExpedientTipus(expedientTipusDto);
		}
		
		return dto;
	}

	public interface Creacio {}
	public interface Modificacio {}
}
