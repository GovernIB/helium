/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;

import net.conselldemallorca.helium.webapp.v3.command.AgrupacioCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.AgrupacioCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.Agrupacio;

/**
 * Command per editar la informació de les validacions de variables dels tipus d'expedient 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Agrupacio(groups = {Creacio.class, Modificacio.class})
public class ExpedientTipusCampRegistreCommand {
	
	private Long expedientTipusId;
	private Long definicioProcesId;
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
	public Long getDefinicioProcesId() {
		return definicioProcesId;
	}
	public void setDefinicioProcesId(Long definicioProcesId) {
		this.definicioProcesId = definicioProcesId;
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
