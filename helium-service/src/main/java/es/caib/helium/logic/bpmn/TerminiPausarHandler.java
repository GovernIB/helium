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
import org.flowable.common.engine.api.delegate.Expression;
/**
 * Handler per pausar un termini.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
@Setter
public class TerminiPausarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.TerminiPausarHandler {

	private Expression terminiCodi;
	private Expression varData;

	@Override
	public void execute(HeliumApi executionContext) throws HeliumHandlerException {

		TerminiInfo termini = null;
		try {
			HeliumBridge.getInstanceService().getTerminiIniciatAmbCodi(
				executionContext,
				getValue(terminiCodi));
		} catch (NoTrobatException ex) {
			termini = null;
		} catch (BpmnException e) {
			throw new HeliumHandlerException(e);
		}

		if (termini != null) {
			if (varData != null) {
				Date valorData = null;
				try {
					valorData = getVariableComData(executionContext, getValue(varData));
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
