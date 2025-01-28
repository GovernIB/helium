/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioNotibCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioNotibCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusIntegracioNotib;

/**
 * Command per modificar les dades d'integració dels tipus d'expedient amb Notib.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusIntegracioNotib(groups = {Creacio.class, Modificacio.class})
public class ExpedientTipusIntegracioNotibCommand {
	
	private String notibEmisor;
	private String notibCodiProcediment;
	private Boolean codiSiaError;
	private Boolean notibActiu;
	
	
	public String getNotibEmisor() {
		return notibEmisor;
	}

	public void setNotibEmisor(String notibEmisor) {
		this.notibEmisor = notibEmisor;
	}

	public String getNotibCodiProcediment() {
		return notibCodiProcediment;
	}

	public void setNotibCodiProcediment(String notibCodiProcediment) {
		this.notibCodiProcediment = notibCodiProcediment;
	}

	public Boolean getNotibActiu() {
		return notibActiu;
	}

	public void setNotibActiu(Boolean notibActiu) {
		this.notibActiu = notibActiu;
	}

	public Boolean getCodiSiaError() {
		return codiSiaError;
	}

	public void setCodiSiaError(Boolean codiSiaError) {
		this.codiSiaError = codiSiaError;
	}



	public interface Creacio {}
	public interface Modificacio {}
}
