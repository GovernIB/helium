package es.caib.helium.logic.bpmn;


import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.integracio.HeliumBridge;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.integracio.Termini;
import es.caib.helium.bpmn.exception.BpmnException;
import es.caib.helium.disseny.handler.TerminiIniciarHandler;
import es.caib.helium.bpmn.model.TerminiInfo;
import lombok.Setter;
import org.flowable.common.engine.api.delegate.Expression;

import java.util.Calendar;
import java.util.Date;

@Setter
public class TerminiIniciarHandlerImpl extends AbstractHeliumActionHandler implements TerminiIniciarHandler {

	private Expression terminiCodi;
	private Expression varData;
	private Expression varTermini;
	private Expression sumarUnDia;
	private Expression esDataFi;

	public void execute(HeliumApi executionContext) throws BpmnException {
		String tercod = getValue(terminiCodi);
		TerminiInfo termini = HeliumBridge
			    				.getInstanceService()
								.getTerminiIniciatAmbCodi(executionContext, tercod);
		if (termini != null) {
			if (varTermini != null) {
				String valorTermini = getValue(varTermini);
				if (valorTermini == null)
					throw new BpmnException("No s'ha pogut llegir el termini de la variable '" + varTermini + "'");

				Termini vt = Termini.valueFromString(valorTermini);
				executionContext.getTerminiIniciatAmbProcessInstanceITerminiCodi(
					executionContext.getProcessInstance().getId(),
					tercod
				);
				HeliumBridge.getInstanceService().terminiIniciar(
					tercod,
					getProcessInstanceId(executionContext),
					getDataVariable(executionContext),
					vt.getAnys(),
					vt.getMesos(),
					vt.getDies(),
					esDataFi());
			} else {
				HeliumBridge.getInstanceService().terminiIniciar(
					tercod,
					getProcessInstanceId(executionContext),
					getDataVariable(executionContext),
					esDataFi());
			}
		} else {
			throw new BpmnException("No existeix cap termini amb aquest codi '" + terminiCodi + "'");
		}
	}

	private Date getDataVariable(HeliumApi executionContext) throws BpmnException {
		Date data;
		String valorData = getValue(varData);

		if (valorData != null && !valorData.isEmpty()) {
			data = getVariableComData(executionContext, valorData);
		} else {
			data = new Date();
		}

		String valorSumarUnDia = getValue(sumarUnDia);
		if (!esDataFi() && valorSumarUnDia != null && !valorSumarUnDia.isEmpty()) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			return cal.getTime();
		} else {
			return data;
		}
	}

	private boolean esDataFi() {
		return getValueAsBoolean(esDataFi);
	}
}
