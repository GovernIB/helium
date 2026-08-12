package es.caib.helium.logic.bpmn;

import es.caib.helium.commons.dto.TerminiDto;
import es.caib.helium.commons.dto.TerminiIniciatDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.BpmnException;
import es.caib.helium.persistence.common.jbpm.DominiCodiDescripcio;

import java.util.Date;

/**
 * Handler base amb accés a la funcionalitat de Helium
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
abstract class AbstractHeliumActionHandler {
	protected Object getValorOVariable(HeliumApi executionContext, Object value, String var) {
		if (value != null)
			return value;
		if (var != null && !var.isEmpty()) {
			Object returnVal;
			if (executionContext.getVariable(var) instanceof DominiCodiDescripcio)
				returnVal = ((DominiCodiDescripcio)executionContext.getVariable(var)).getCodi();
			else
				returnVal = executionContext.getVariable(var);
			return returnVal;
		}
		return null;
	}

	TerminiIniciatDto getTerminiIniciatAmbCodi(HeliumApi executionContext, String codi) throws BpmnException {
		TerminiDto termini = getTerminiAmbCodi(
			executionContext,
			codi);
		if (termini != null) {
			return executionContext.getTerminiIniciatAmbProcessInstanceITerminiCodi(
				getProcessInstanceId(executionContext),
				termini.getCodi());
		} else {
			return null;
		}
	}

	protected String getProcessInstanceId(HeliumApi executionContext) {
		return executionContext.getProcessInstance().getId();
	}

	protected String getTaskInstanceId(HeliumApi executionContext) {
		return executionContext.getTaskInstance().getId();
	}

	protected Date getVariableComData(HeliumApi executionContext,
	                                  String var) throws BpmnException {
		Object obj = executionContext.getVariable(var);
		if (obj == null)
			throw new NoTrobatException(Date.class, var);
		if (obj instanceof Date)
			return (Date) obj;
		throw new BpmnException("La variable amb el codi '" + var + "' no és de tipus Date");
	}

	protected TerminiDto getTerminiAmbCodi(HeliumApi executionContext, String codi) throws BpmnException {
		try {
			return HeliumBridge.getInstanceService().getTerminiAmbProcessInstanceICodi(
				getProcessInstanceId(executionContext),
				codi);
		} catch (Exception ex) {
			throw new BpmnException("No s'ha trobat el termini amb codi: " + codi);
		}
	}
}
