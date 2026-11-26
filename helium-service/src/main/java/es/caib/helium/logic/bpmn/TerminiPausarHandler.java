package es.caib.helium.logic.bpmn;

import java.util.Date;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;
/**
 * Handler per pausar un termini.
 *
 * <p>{@code data} és la data de pausa en format {@value #FORMAT_DATA} (o una expressió, p. ex.
 * {@code ${dataPausa}}); si no s'informa s'agafa la data actual. Si el termini no s'ha iniciat, no fa res.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
@Setter
public class TerminiPausarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.TerminiPausarHandler {

	private String terminiCodi;
	private String data;

	@Override
	public void execute(HeliumApi executionContext) throws HeliumHandlerException {
		// Si no hi ha cap termini amb aquest codi, la HeliumApi llança l'error
		if (executionContext.getTerminiIniciat(terminiCodi) != null) {
			executionContext.terminiPausar(
				terminiCodi,
				isBuit(data) ? new Date() : asDate(data));
		}
	}
}
