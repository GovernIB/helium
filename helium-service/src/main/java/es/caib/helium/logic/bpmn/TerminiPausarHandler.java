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
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
@Setter
public class TerminiPausarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.TerminiPausarHandler {

	private String terminiCodi;
	private String varData;

	@Override
	public void execute(HeliumApi executionContext) throws HeliumHandlerException {

		TerminiInfo termini = null;
		try {
			HeliumBridge.getInstanceService().getTerminiIniciatAmbCodi(
				executionContext,
				terminiCodi);
		} catch (NoTrobatException ex) {
			termini = null;
		} catch (BpmnException e) {
			throw new HeliumHandlerException(e);
		}

		if (termini != null) {
			if (varData != null) {
				Date valorData = null;
				try {
					valorData = getVariableComData(executionContext, varData);
				} catch (NoTrobatException | BpmnException ex) {
					valorData = null;
				}

				if (valorData != null)
					HeliumBridge.getInstanceService().terminiPausar(
						termini.getId(),
						valorData);
			} else {
				HeliumBridge.getInstanceService().terminiPausar(
					termini.getId(),
					new Date());
			}
		}
	}
}
