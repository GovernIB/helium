package net.conselldemallorca.helium.v3.core.api.exception;

public class SistemaExternTimeoutException extends HeliumException {
	
	private String codiSistemaExtern;

	public SistemaExternTimeoutException() {
		super();
	}

	public SistemaExternTimeoutException(Long entornId, String entornCodi,
			String entornNom, Long expedientId, String expedientTitol,
			String expedientNumero, Long expedientTipusId,
			String expedientTipusCodi, String expedientTipusNom) {
		super(entornId, entornCodi, entornNom, expedientId, expedientTitol,
				expedientNumero, expedientTipusId, expedientTipusCodi,
				expedientTipusNom);
	}

	public SistemaExternTimeoutException(Long entornId, String entornCodi,
			String entornNom, Long expedientId, String expedientTitol,
			String expedientNumero, Long expedientTipusId,
			String expedientTipusCodi, String expedientTipusNom,
			String codiSistemaExtern) {
		super(entornId, entornCodi, entornNom, expedientId, expedientTitol,
				expedientNumero, expedientTipusId, expedientTipusCodi,
				expedientTipusNom);
		this.codiSistemaExtern = codiSistemaExtern;
	}
	
	public SistemaExternTimeoutException(String codiSistemaExtern) {
		super();
		this.codiSistemaExtern = codiSistemaExtern;
	}

	public String getCodiSistemaExtern() {
		return codiSistemaExtern;
	}

	public void setCodiSistemaExtern(String codiSistemaExtern) {
		this.codiSistemaExtern = codiSistemaExtern;
	}

	private static final long serialVersionUID = -7048884361828346790L;
}
