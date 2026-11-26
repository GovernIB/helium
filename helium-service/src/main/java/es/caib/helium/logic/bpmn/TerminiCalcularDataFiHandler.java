package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.BpmnException;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.model.TerminiInfo;
import es.caib.helium.persistence.entity.Termini;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Setter
public class TerminiCalcularDataFiHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.TerminiCalcularDataFiHandler {
	private String terminiCodi;
	private String varTerminiCodi;
	private String varData;
	private String sumarUnDia;
	private String varTermini;
	private String varDataFi;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException, BpmnException {
		TerminiInfo termini = heliumApi.getTermini(
                (String)getValorOVariable(
                    heliumApi,
                    terminiCodi,
                    varTerminiCodi));
		if (termini != null) {
			Date dataFi;
			if (varTermini != null) {
				Object valorTermini = heliumApi.getVariable(varTermini);
				if (valorTermini == null)
					throw new BpmnException("No s'ha pogut llegir el termini de la variable '" + varTermini + "'");
				Termini vt;
				if (valorTermini instanceof Termini) {
					vt = (Termini)valorTermini;
				} else {
					vt = Termini.valueFromString((String) valorTermini);
				}

				dataFi = heliumApi.terminiCalcularDataFi(
					getDataInici(heliumApi),
					vt.getAnys(),
					vt.getMesos(),
					vt.getDies(),
					termini.isLaborable());
			} else {
				dataFi = heliumApi.terminiCalcularDataFi(
					getDataInici(heliumApi),
					termini.getAnys(),
					termini.getMesos(),
					termini.getDies(),
					termini.isLaborable());
			}
			heliumApi.setVariable(varDataFi, dataFi);
		} else {
			throw new BpmnException("No existeix cap termini amb aquest codi '" + getValorOVariable(heliumApi, terminiCodi, varTerminiCodi) + "'");
		}
	}

	private Date getDataInici(HeliumApi executionContext) throws BpmnException {
		Date data;
		if (varData != null && !varData.isEmpty()) {
			data = getVariableComData(executionContext, varData);
		} else {
			data = new Date();
		}
		if (sumarUnDia != null && !sumarUnDia.isEmpty()) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			return cal.getTime();
		} else {
			return data;
		}
	}
}
