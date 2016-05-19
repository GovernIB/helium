package net.conselldemallorca.helium.v3.core.api.exception;

public abstract class HeliumException extends RuntimeException {
	
	//Dades de l'entorn
	private Long entornId;
	private String entornCodi;
	private String entornNom; 
	
	//Dades de l'expedient
	private Long expedientId;
	private String expedientTitol;
	private String expedientNumero;
	
	//Dades del tiups d'expedient
	private Long expedientTipusId;
	private String expedientTipusCodi;
	private String expedientTipusNom;
	
	public HeliumException() {
		super();
	}
	
	public HeliumException(Long entornId, String entornCodi, String entornNom,
			Long expedientId, String expedientTitol, String expedientNumero,
			Long expedientTipusId, String expedientTipusCodi,
			String expedientTipusNom) {
		super();
		this.entornId = entornId;
		this.entornCodi = entornCodi;
		this.entornNom = entornNom;
		this.expedientId = expedientId;
		this.expedientTitol = expedientTitol;
		this.expedientNumero = expedientNumero;
		this.expedientTipusId = expedientTipusId;
		this.expedientTipusCodi = expedientTipusCodi;
		this.expedientTipusNom = expedientTipusNom;
	}
	
	public Long getEntornId() {
		return entornId;
	}
	public void setEntornId(Long entornId) {
		this.entornId = entornId;
	}
	public String getEntornCodi() {
		return entornCodi;
	}
	public void setEntornCodi(String entornCodi) {
		this.entornCodi = entornCodi;
	}
	public String getEntornNom() {
		return entornNom;
	}
	public void setEntornNom(String entornNom) {
		this.entornNom = entornNom;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getExpedientTitol() {
		return expedientTitol;
	}
	public void setExpedientTitol(String expedientTitol) {
		this.expedientTitol = expedientTitol;
	}
	public String getExpedientNumero() {
		return expedientNumero;
	}
	public void setExpedientNumero(String expedientNumero) {
		this.expedientNumero = expedientNumero;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public String getExpedientTipusCodi() {
		return expedientTipusCodi;
	}
	public void setExpedientTipusCodi(String expedientTipusCodi) {
		this.expedientTipusCodi = expedientTipusCodi;
	}
	public String getExpedientTipusNom() {
		return expedientTipusNom;
	}
	public void setExpedientTipusNom(String expedientTipusNom) {
		this.expedientTipusNom = expedientTipusNom;
	}
	
	private static final long serialVersionUID = -2743173354549535447L;
}
