/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

/**
 * Command per iniciar un expedient
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ExpedientIniciarPasTitolCommand {

	private Long entornId;
	private Long expedientTipusId;
	private String numero;
	private String titol;
	private String responsableCodi;



	public ExpedientIniciarPasTitolCommand() {}

	public Long getEntornId() {
		return entornId;
	}
	public void setEntornId(Long entornId) {
		this.entornId = entornId;
	}

	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}

	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}

	public String getResponsableCodi() {
		return responsableCodi;
	}

	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}

}
