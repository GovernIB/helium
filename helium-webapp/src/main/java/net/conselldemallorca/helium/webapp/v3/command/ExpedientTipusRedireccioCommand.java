/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import es.caib.helium.logic.intf.dto.ReassignacioDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusRedireccioCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusRedireccioCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusRedireccio;

/**
 * Command per editar la informació de les accions dels tipus d'expedient 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusRedireccio(groups = {Creacio.class, Modificacio.class})
public class ExpedientTipusRedireccioCommand {
	
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private Long expedientTipusId;
	private Long id;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String usuariOrigen;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String usuariDesti;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private Date dataInici;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private Date dataFi;
	private Date dataCancelacio;
	
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
	public String getUsuariOrigen() {
		return usuariOrigen;
	}
	public void setUsuariOrigen(String usuariOrigen) {
		this.usuariOrigen = usuariOrigen;
	}
	public String getUsuariDesti() {
		return usuariDesti;
	}
	public void setUsuariDesti(String usuariDesti) {
		this.usuariDesti = usuariDesti;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public Date getDataCancelacio() {
		return dataCancelacio;
	}
	public void setDataCancelacio(Date dataCancelacio) {
		this.dataCancelacio = dataCancelacio;
	}
	
	public static ReassignacioDto asReassignacioDto(ExpedientTipusRedireccioCommand command) {
		ReassignacioDto dto = new ReassignacioDto();
		
		dto.setTipusExpedientId(command.getExpedientTipusId());
		dto.setId(command.getId());
		dto.setUsuariOrigen(command.getUsuariOrigen());
		dto.setUsuariDesti(command.getUsuariDesti());
		dto.setDataInici(command.getDataInici());
		dto.setDataFi(command.getDataFi());
		dto.setDataCancelacio(command.getDataCancelacio());
		
		return dto;
	}
	public interface Creacio {}
	public interface Modificacio {}
}
