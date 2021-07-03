package es.caib.helium.logic.intf.exception;

@SuppressWarnings("serial")
public class SistemaExternTimeoutException extends SistemaExternException {

	public SistemaExternTimeoutException(
			Long entornId, 
			String entornCodi,
			String entornNom, 
			Long expedientId, 
			String expedientTitol,
			String expedientNumero, 
			Long expedientTipusId,
			String expedientTipusCodi, 
			String expedientTipusNom,
			String sistemaExtern, 
			Throwable cause) {
		super(	entornId, 
				entornCodi, 
				entornNom, 
				expedientId, 
				expedientTitol,
				expedientNumero, 
				expedientTipusId, 
				expedientTipusCodi,
				expedientTipusNom, 
				sistemaExtern, 
				cause);
	}
	
}
