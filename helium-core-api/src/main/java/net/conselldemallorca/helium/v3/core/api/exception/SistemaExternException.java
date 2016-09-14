/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * Excepció que es llança si hi ha algun error crindant al plugin.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class SistemaExternException extends HeliumException {

	private String sistemaExtern;
	protected String publicMessage;
	
	public SistemaExternException(
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
				"Error en la comunicació amb el sistema extern '" + sistemaExtern + "': " + ExceptionUtils.getRootCauseMessage(cause),
				cause);
		this.sistemaExtern = sistemaExtern;
		this.publicMessage = "Error en la comunicació amb el sistema extern '" + sistemaExtern + "': " + ExceptionUtils.getRootCauseMessage(cause);
	}
	
	public SistemaExternException(
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
				"Error en la comunicació amb el sistema extern '" + sistemaExtern + "': " + causa,
				null);
		this.sistemaExtern = sistemaExtern;
		this.publicMessage = "Error en la comunicació amb el sistema extern '" + sistemaExtern + "': " + causa;
	}
	
	public SistemaExternException(Throwable cause) {
		super(cause);
	}
	
	public static SistemaExternException tractarSistemaExternException(
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
		
		if(ExceptionUtils.getRootCause(cause) != null && 
				(ExceptionUtils.getRootCause(cause).getClass().getName().contains("Timeout") ||
				 ExceptionUtils.getRootCause(cause).getClass().getName().contains("timeout"))) {
			
			return new SistemaExternTimeoutException(
					entornId,
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
			
		} else {
			return new SistemaExternException(
					entornId,
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
	
	public String getPublicMessage() {
		return publicMessage;
	}

	public String getSistemaExtern() {
		return sistemaExtern;
	}

}
