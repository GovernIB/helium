/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAgrupacioCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAgrupacioCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusAgrupacio;

/**
 * Command per editar la informació de les validacions de variables dels tipus d'expedient 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusAgrupacio(groups = {Creacio.class, Modificacio.class})
public class ExpedientTipusValidacioCommand {
	
	private Long expedientTipusId;
	private Long campId;
	private Long id;
	
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 1024, groups = {Creacio.class, Modificacio.class})
	private String expressio;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String missatge;
	
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public Long getCampId() {
		return campId;
	}
	public void setCampId(Long campId) {
		this.campId = campId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getExpressio() {
		return expressio;
	}
	public void setExpressio(String expressio) {
		this.expressio = expressio;
	}
	public String getMissatge() {
		return missatge;
	}
	public void setMissatge(String missatge) {
		this.missatge = missatge;
	}
	
	public interface Creacio {}
	public interface Modificacio {}
}
