/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Calendar;
import java.util.Date;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per iniciar un termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class TerminiIniciarHandler extends AbstractHeliumActionHandler implements TerminiIniciarHandlerInterface {

	private String terminiCodi;
	private String varTerminiCodi;
	private String varData;
	private String sumarUnDia;
	private String varTermini;
	private String esDataFi;



	public void execute(ExecutionContext executionContext) throws Exception {
		String tercod = (String)getValorOVariable(
				executionContext,
				terminiCodi,
				varTerminiCodi);
		TerminiDto termini = getTerminiAmbCodi(executionContext, tercod);
		if (termini != null) {
			if (varTermini != null) {
				Object valorTermini = executionContext.getVariable(varTermini);
				if (valorTermini == null)
					throw new JbpmException("No s'ha pogut llegir el termini de la variable '" + varTermini + "'");
				Termini vt = (Termini)valorTermini;
				Jbpm3HeliumBridge.getInstanceService().terminiIniciar(
						tercod,
						getProcessInstanceId(executionContext),
						getDataVariable(executionContext),
						vt.getAnys(),
						vt.getMesos(),
						vt.getDies(),
						esDataFi());
			} else {
				Jbpm3HeliumBridge.getInstanceService().terminiIniciar(
						tercod,
						getProcessInstanceId(executionContext),
						getDataVariable(executionContext),
						esDataFi());
			}
		} else {
			throw new JbpmException("No existeix cap termini amb aquest codi '" + terminiCodi + "'");
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



	private Date getDataVariable(ExecutionContext executionContext) {
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
