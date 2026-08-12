package es.caib.helium.logic.bpmn;

import es.caib.helium.commons.dto.TerminiIniciatDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.BpmnException;
import es.caib.helium.disseny.exception.HeliumHandlerException;

import java.util.Date;

/**
 * Handler per pausar un termini.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class TerminiPausarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.TerminiPausarHandler {

	private String terminiCodi;
	private String varTerminiCodi;
	private String varData;

	@Override
	public void setTerminiCodi(String terminiCodi) {

	}

	@Override
	public void setVarTerminiCodi(String varTerminiCodi) {

	}

	@Override
	public void setVarData(String varData) {

	}

	@Override
	public void execute(HeliumApi executionContext) throws HeliumHandlerException {

		TerminiIniciatDto termini = null;
		try {
			termini = getTerminiIniciatAmbCodi(
				executionContext,
				(String)getValorOVariable(executionContext, terminiCodi, varTerminiCodi));
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
