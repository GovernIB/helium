/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Command per editar la informació de les validacions de variables dels tipus d'expedient 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidacioCommand {
	
	private Long expedientTipusId;
	private Long definicioProcesId;
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
	public Long getDefinicioProcesId() {
		return definicioProcesId;
	}
	public void setDefinicioProcesId(Long definicioProcesId) {
		this.definicioProcesId = definicioProcesId;
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
