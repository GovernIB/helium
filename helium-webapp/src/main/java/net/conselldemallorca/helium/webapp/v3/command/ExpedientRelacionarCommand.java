/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;


/**
 * Command per a relacionar expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientRelacionarCommand {

	private String instanciaProcesId;
	private Long expedientIdOrigen;
	private Long expedientIdDesti;
	public String getInstanciaProcesId() {
		return instanciaProcesId;
	}
	public void setInstanciaProcesId(String instanciaProcesId) {
		this.instanciaProcesId = instanciaProcesId;
	}
	public Long getExpedientIdOrigen() {
		return expedientIdOrigen;
	}
	public void setExpedientIdOrigen(Long expedientIdOrigen) {
		this.expedientIdOrigen = expedientIdOrigen;
	}
	public Long getExpedientIdDesti() {
		return expedientIdDesti;
	}
	public void setExpedientIdDesti(Long expedientIdDesti) {
		this.expedientIdDesti = expedientIdDesti;
	}

}
