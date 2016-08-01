/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;

import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAgrupacioCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAgrupacioCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusAgrupacio;

/**
 * Command per editar la informació de les validacions de variables dels tipus d'expedient 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusAgrupacio(groups = {Creacio.class, Modificacio.class})
public class ExpedientTipusCampRegistreCommand {
	
	private Long expedientTipusId;
	private Long registreId;
	private Long id;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private Long membreId;
	private boolean obligatori;
	private boolean llistar;
	
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public Long getRegistreId() {
		return registreId;
	}
	public void setRegistreId(Long registreId) {
		this.registreId = registreId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMembreId() {
		return membreId;
	}
	public void setMembreId(Long membreId) {
		this.membreId = membreId;
	}
	public boolean isObligatori() {
		return obligatori;
	}
	public void setObligatori(boolean obligatori) {
		this.obligatori = obligatori;
	}
	public boolean isLlistar() {
		return llistar;
	}
	public void setLlistar(boolean llistar) {
		this.llistar = llistar;
	}
	
	public interface Creacio {}
	public interface Modificacio {}
}
