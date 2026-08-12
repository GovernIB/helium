package es.caib.helium.logic.bpmn;


import es.caib.helium.commons.dto.TerminiDto;
import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.bpmn.integracio.Termini;
import es.caib.helium.disseny.exception.BpmnException;
import es.caib.helium.disseny.handler.TerminiIniciarHandler;
import org.flowable.engine.impl.context.ExecutionContext;


import java.util.Calendar;
import java.util.Date;

public class TerminiIniciarHandlerImpl extends AbstractHeliumActionHandler implements TerminiIniciarHandler {

	private String terminiCodi;
	private String varTerminiCodi;
	private String varData;
	private String sumarUnDia;
	private String varTermini;
	private String esDataFi;


	public void execute(HeliumApi executionContext) throws BpmnException {
		String tercod = (String)getValorOVariable(
			executionContext,
			terminiCodi,
			varTerminiCodi);
		TerminiDto termini = getTerminiAmbCodi(executionContext, tercod);
		if (termini != null) {
			if (varTermini != null) {
				Object valorTermini = executionContext.getVariable(varTermini);
				if (valorTermini == null)
					throw new BpmnException("No s'ha pogut llegir el termini de la variable '" + varTermini + "'");

				Termini vt = null;
				if (valorTermini instanceof Termini) {
					vt = (Termini)valorTermini;
				} else {
					vt = Termini.valueFromString((String) valorTermini);
				}

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

	public void setTerminiCodi(String terminiCodi) {
		this.terminiCodi = terminiCodi;
	}
	public void setVarTerminiCodi(String varTerminiCodi) {
		this.varTerminiCodi = varTerminiCodi;
	}
	public void setVarData(String varData) {
		this.varData = varData;
	}
	public void setSumarUnDia(String sumarUnDia) {
		this.sumarUnDia = sumarUnDia;
	}
	public void setVarTermini(String varTermini) {
		this.varTermini = varTermini;
	}
	public void setDesdeFi(String desdeFi) {
		this.esDataFi = desdeFi;
	}
	public void setEsDataFi(String esDataFi) {
		this.esDataFi = esDataFi;
	}

	private Date getDataVariable(HeliumApi executionContext) throws BpmnException {
		Date data;
		if (varData != null && varData.length() > 0) {
			data = getVariableComData(executionContext, varData);
		} else {
			data = new Date();
		}
		if (!esDataFi() && sumarUnDia != null && sumarUnDia.length() > 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			return cal.getTime();
		} else {
			return data;
		}
	}

	private boolean esDataFi() {
		return (esDataFi != null && "true".equalsIgnoreCase(esDataFi));
	}

}
