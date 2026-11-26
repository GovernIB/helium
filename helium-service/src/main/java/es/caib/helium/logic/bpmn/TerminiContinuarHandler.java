package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.BpmnException;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.model.TerminiIniciatInfo;
import lombok.Setter;

import java.util.Date;

@Setter
public class TerminiContinuarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.TerminiContinuarHandler {

	private String terminiCodi;
	private String varTerminiCodi;
	private String varData;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException, BpmnException {
		TerminiIniciatInfo termini = heliumApi.getTerminiIniciatAmbProcessInstanceITerminiCodi(
			heliumApi.getProcessInstance().getId(),
			(String) getValorOVariable(
				heliumApi,
				terminiCodi,
				varTerminiCodi)
		);
		if (termini != null) {
			if (varData != null)
				heliumApi.terminiContinuar(
					termini.getTermini().getCodi(),
					getVariableComData(heliumApi, varData));
			else
				heliumApi.terminiContinuar(
					termini.getTermini().getCodi(),
					new Date());
		} else {
			throw new BpmnException("No existeix cap termini iniciat amb aquest codi '" + terminiCodi + "'");
		}
	}

}
