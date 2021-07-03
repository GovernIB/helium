package es.caib.helium.logic.intf.exception;

@SuppressWarnings("serial")
public class SistemaExternConversioDocumentException extends SistemaExternException {

	public SistemaExternConversioDocumentException(
			Long entornId, 
			String entornCodi,
			String entornNom, 
			Long expedientId, 
			String expedientTitol,
			String expedientNumero, 
			Long expedientTipusId,
			String expedientTipusCodi, 
			String expedientTipusNom,
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
				"Servidor extern de conversió de documents", 
				cause);
		this.publicMessage = "S'ha produït un error amb el servidor extern de conversió de documents.";
	}
	
	public SistemaExternConversioDocumentException(
			Long entornId, 
			String entornCodi,
			String entornNom, 
			Long expedientId, 
			String expedientTitol,
			String expedientNumero, 
			Long expedientTipusId,
			String expedientTipusCodi, 
			String expedientTipusNom,
			String causa) {
		super(	entornId, 
				entornCodi, 
				entornNom, 
				expedientId, 
				expedientTitol,
				expedientNumero, 
				expedientTipusId, 
				expedientTipusCodi,
				expedientTipusNom, 
				"Servidor extern de conversió de documents", 
				causa);
		this.publicMessage = causa;
	}
	
	public SistemaExternConversioDocumentException(Throwable cause) {
		super(cause);
	}
	
}
