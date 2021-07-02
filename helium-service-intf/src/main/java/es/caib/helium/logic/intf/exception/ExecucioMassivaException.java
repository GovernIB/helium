/**
 * 
 */
package es.caib.helium.logic.intf.exception;

/**
 * Excepció que es llança si hi ha algun error crindant al plugin.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExecucioMassivaException extends HeliumException {

	private Long execucioMassivaId;
	private Long execucioMassivaExpedientId;
	
	public ExecucioMassivaException(
			Long entornId, 
			String entornCodi,
			String entornNom, 
			Long expedientId, 
			String expedientTitol,
			String expedientNumero, 
			Long expedientTipusId,
			String expedientTipusCodi, 
			String expedientTipusNom,
			Long execucioMassivaId,
			Long execucioMassivaExpedientId,
			String message,
			Throwable cause) {
		super(entornId, 
			  entornCodi, 
			  entornNom, 
			  expedientId, 
			  expedientTitol,
			  expedientNumero, 
			  expedientTipusId, 
			  expedientTipusCodi,
			  expedientTipusNom,
			  "Error en l'operació massiva " + execucioMassivaExpedientId + " de l'execució " + execucioMassivaId + ". " + message + ": " + cause.getMessage(),
			  cause);
		this.execucioMassivaId = execucioMassivaId;
		this.execucioMassivaExpedientId = execucioMassivaExpedientId;
	}

	public Long getExecucioMassivaId() {
		return execucioMassivaId;
	}
	
	public Long getExecucioMassivaExpedientId() {
		return execucioMassivaExpedientId;
	}
	
	
}
