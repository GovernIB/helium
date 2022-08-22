package net.conselldemallorca.helium.webapp.v3.command;

public class ExpedientTipusAdminCommand {
	
	private String codiTipologia;
	private String codiSIA;
	private String numRegistreAnotacio;
	private Long expedientTipusId;

	
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public String getCodiTipologia() {
		return codiTipologia;
	}
	public void setCodiTipologia(String codiTipologia) {
		this.codiTipologia = codiTipologia;
	}
	public String getCodiSIA() {
		return codiSIA;
	}
	public void setCodiSIA(String codiSIA) {
		this.codiSIA = codiSIA;
	}
	public String getNumRegistreAnotacio() {
		return numRegistreAnotacio;
	}
	public void setNumRegistreAnotacio(String numRegistreAnotacio) {
		this.numRegistreAnotacio = numRegistreAnotacio;
	}
	
}
