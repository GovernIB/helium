/**
 * 
 */
package es.caib.helium.logic.intf.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Excepció que es llança si hi ha algun error crindant al plugin.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class TramitacioException extends HeliumException {
	
	public TramitacioException(
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
		super(entornId, 
			  entornCodi, 
			  entornNom, 
			  expedientId, 
			  expedientTitol,
			  expedientNumero, 
			  expedientTipusId, 
			  expedientTipusCodi,
			  expedientTipusNom,
			  "S'ha produït un error de tramitació. " + message + ": " + cause.getMessage(),
			  cause);
	}
	
	public String getPublicMessage() {
		return "Error de tramitació. " + ExceptionUtils.getRootCauseMessage(this.getCause());
	}
}
