/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Command per modificar les dades d'integració dels tipus d'expedient amb Sistra.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusIntegracioTramitsCommand {

	@NotNull(groups = {Modificacio.class})
	private Long id;
	private boolean actiu;
	@Size(max = 64, groups = {Modificacio.class})
	private String tramitCodi;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}
	public String getTramitCodi() {
		return tramitCodi;
	}
	public void setTramitCodi(String tramitCodi) {
		this.tramitCodi = tramitCodi;
	}
	
	public interface Modificacio {}
}
