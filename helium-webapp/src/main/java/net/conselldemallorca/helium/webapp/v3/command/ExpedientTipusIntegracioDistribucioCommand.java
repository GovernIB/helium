/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioDistribucioCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusIntegracioDistribucio;
/**
 * Command per modificar les dades d'integració amb distribució.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusIntegracioDistribucio(groups = {Modificacio.class})
public class ExpedientTipusIntegracioDistribucioCommand {

	@NotNull(groups = {Modificacio.class})
	private Long id;
	private boolean actiu;
	@Size(max = 200, groups = {Modificacio.class})
	private String codiProcediment;

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
	public String getCodiProcediment() {
		return codiProcediment;
	}
	public void setCodiProcediment(String codiProcediment) {
		this.codiProcediment = codiProcediment;
	}
	public interface Modificacio {}
}
