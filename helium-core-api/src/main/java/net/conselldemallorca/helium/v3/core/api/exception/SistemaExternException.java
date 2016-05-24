/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

/**
 * Excepció que es llança si hi ha algun error crindant al plugin.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class SistemaExternException extends HeliumException {

	private String sistemaExtern;
	
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
				"Error en la comunicació amb el sistema extern " + sistemaExtern + ": " + cause.getMessage(),
				cause);
		this.sistemaExtern = sistemaExtern;
	}

	public String getSistemaExtern() {
		return sistemaExtern;
	}

}
