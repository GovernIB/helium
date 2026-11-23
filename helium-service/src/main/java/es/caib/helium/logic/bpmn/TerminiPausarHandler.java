package es.caib.helium.logic.bpmn;

import java.util.Date;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.integracio.HeliumBridge;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.BpmnException;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.model.TerminiInfo;
import lombok.Setter;
/**
 * Handler per pausar un termini.
 *
 * <p>{@code data} és la data de pausa en format {@value #FORMAT_DATA} (o una expressió, p. ex.
 * {@code ${dataPausa}}); si no s'informa s'agafa la data actual.</p>
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
		TerminiInfo termini;
		try {
			termini = HeliumBridge.getInstanceService().getTerminiIniciatAmbCodi(
				executionContext,
				terminiCodi);
		} catch (NoTrobatException ex) {
			termini = null;
		} catch (BpmnException e) {
			throw new HeliumHandlerException(e);
		}
		if (termini != null) {
			HeliumBridge.getInstanceService().terminiPausar(
				termini.getId(),
				isBuit(data) ? new Date() : asDate(data));
		}
	}
}
