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
	public HeliumException(
			String message,
			Throwable cause) {
		super(message, cause);
	}

	public HeliumException(
			Long entornId,
			String entornCodi,
			String entornNom,
			Long expedientId,
			String expedientTitol,
			String expedientNumero,
			Long expedientTipusId,
			String expedientTipusCodi,
			String expedientTipusNom) {
		super(getCommonMessage(
				entornId,
				entornCodi,
				entornNom,
				expedientId,
				expedientTitol,
				expedientNumero,
				expedientTipusId,
				expedientTipusCodi,
				expedientTipusNom,
				null));
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

	public HeliumException(
			Long entornId,
			String entornCodi,
			String entornNom,
			Long expedientId,
			String expedientTitol,
			String expedientNumero,
			Long expedientTipusId,
			String expedientTipusCodi,
			String expedientTipusNom,
			String message,
			Throwable cause) {
		super(getCommonMessage(
				entornId,
				entornCodi,
				entornNom,
				expedientId,
				expedientTitol,
				expedientNumero,
				expedientTipusId,
				expedientTipusCodi,
				expedientTipusNom,
				message), cause);
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
	
	private static String getCommonMessage (
			Long entornId,
			String entornCodi,
			String entornNom,
			Long expedientId,
			String expedientTitol,
			String expedientNumero,
			Long expedientTipusId,
			String expedientTipusCodi,
			String expedientTipusNom,
			String message) {
		String finalMessage = " ===> ";
		finalMessage += (message != null ? message : "");
		if (entornId != null)
			finalMessage += " entornID: " + entornId + ".";
		if (entornCodi != null)
			finalMessage += " entornCodi: " + entornCodi + ".";
		if (entornNom != null)
			finalMessage += " entornNom: " + entornNom + ".";
		if (expedientId != null)
			finalMessage += " expedientID: " + expedientId + ".";
		if (expedientTitol != null)
			finalMessage += " expedientTitol: " + expedientTitol + ".";
		if (expedientNumero != null)
			finalMessage += " expedientNumero: " + expedientNumero + ".";
		if (expedientTipusId != null)
			finalMessage += " expedientTipusID: " + expedientTipusId + ".";
		if (expedientTipusCodi != null)
			finalMessage += " expedientTipusCodi: " + expedientTipusCodi + ".";
		if (expedientTipusNom != null)
			finalMessage += " expedientTipusNom: " + expedientTipusNom + ".";
		
		return finalMessage;
	}

	public Long getEntornId() {
		return entornId;
	}
	public String getEntornCodi() {
		return entornCodi;
	}
	public String getEntornNom() {
		return entornNom;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public String getExpedientTitol() {
		return expedientTitol;
	}
	public String getExpedientNumero() {
		return expedientNumero;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public String getExpedientTipusCodi() {
		return expedientTipusCodi;
	}
	public String getExpedientTipusNom() {
		return expedientTipusNom;
	}

	private static final long serialVersionUID = -2743173354549535447L;

}
