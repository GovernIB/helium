package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.BpmnException;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.model.TerminiInfo;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Setter
public class TerminiCalcularDataIniciHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.TerminiCalcularDataIniciHandler {
	private String terminiCodi;
	private String varTerminiCodi;
	private String varData;
	private String restarUnDia;
	private String varTermini;
	private String varDataInici;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException, BpmnException {
		TerminiInfo termini = heliumApi.getTermini(
			(String)getValorOVariable(heliumApi, terminiCodi, varTerminiCodi));
		if (termini != null) {
			Date dataInici;
			if (varTermini != null) {
				Object valorTermini = heliumApi.getVariable(varTermini);
				if (valorTermini == null)
					throw new BpmnException("No s'ha pogut llegir el termini de la variable '" + varTermini + "'");

				TerminiInfo vt;
				if (valorTermini instanceof TerminiInfo) {
					vt = (TerminiInfo)valorTermini;
				} else {
					vt = TerminiInfo.valueFromString((String) valorTermini);
				}

				dataInici = heliumApi.terminiCalcularDataInici(
					getDataFi(heliumApi),
					vt.getAnys(),
					vt.getMesos(),
					vt.getDies(),
					termini.isLaborable());
			} else {
				dataInici = heliumApi.terminiCalcularDataInici(
					getDataFi(heliumApi),
					termini.getAnys(),
					termini.getMesos(),
					termini.getDies(),
					termini.isLaborable());
			}
            heliumApi.setVariable(varDataInici, dataInici);
		} else {
			throw new BpmnException("No existeix cap termini amb aquest codi '" + (String)getValorOVariable(heliumApi, terminiCodi, varTerminiCodi) + "'");
		}
	}

	private Date getDataFi(HeliumApi executionContext) throws BpmnException {
		Date data;
		if (varData != null && !varData.isEmpty()) {
			data = getVariableComData(executionContext, varData);
		} else {
			data = new Date();
		}
		if (restarUnDia != null && !restarUnDia.isEmpty()) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			return cal.getTime();
		} else {
			return data;
		}
	}
}
