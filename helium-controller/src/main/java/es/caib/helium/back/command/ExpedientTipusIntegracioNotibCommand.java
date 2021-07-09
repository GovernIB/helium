/**
 * 
 */
package es.caib.helium.back.command;


import es.caib.helium.back.validator.ExpedientTipusIntegracioNotib;

/**
 * Command per modificar les dades d'integració dels tipus d'expedient amb Notib.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusIntegracioNotib(groups = {ExpedientTipusIntegracioNotibCommand.Creacio.class, ExpedientTipusIntegracioNotibCommand.Modificacio.class})
public class ExpedientTipusIntegracioNotibCommand {
	
	private String notibEmisor;
	private String notibCodiProcediment;
	
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

	public interface Creacio {}
	public interface Modificacio {}
}
