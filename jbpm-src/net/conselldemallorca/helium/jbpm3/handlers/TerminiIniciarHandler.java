/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Calendar;
import java.util.Date;

import net.conselldemallorca.helium.model.hibernate.Termini;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per iniciar un termini.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class TerminiIniciarHandler extends AbstractHeliumActionHandler {

	private String terminiCodi;
	private String varTerminiCodi;
	private String varData;
	private String sumarUnDia;
	private String varTermini;



	public void execute(ExecutionContext executionContext) throws Exception {
		Termini termini = getTerminiAmbCodi(
				executionContext,
				(String)getValorOVariable(executionContext, terminiCodi, varTerminiCodi));
		if (termini != null) {
			if (varTermini != null) {
				Object valorTermini = executionContext.getVariable(varTermini);
				if (valorTermini == null)
					throw new JbpmException("No s'ha pogut llegir el termini de la variable '" + varTermini + "'");
				net.conselldemallorca.helium.jbpm3.integracio.Termini vt = (net.conselldemallorca.helium.jbpm3.integracio.Termini)valorTermini;
				if (varData != null)
					getTerminiService().iniciar(
							termini.getId(),
							getProcessInstanceId(executionContext),
							getDataVariable(executionContext),
							vt.getAnys(),
							vt.getMesos(),
							vt.getDies());
				else
					getTerminiService().iniciar(
							termini.getId(),
							getProcessInstanceId(executionContext),
							new Date(),
							vt.getAnys(),
							vt.getMesos(),
							vt.getDies());
			} else {
				if (varData != null)
					getTerminiService().iniciar(
							termini.getId(),
							getProcessInstanceId(executionContext),
							getDataVariable(executionContext));
				else
					getTerminiService().iniciar(
							termini.getId(),
							getProcessInstanceId(executionContext),
							new Date());
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

	private Date getDataVariable(ExecutionContext executionContext) {
		Date data = getVariableComData(executionContext, varData);
		if (sumarUnDia != null && sumarUnDia.length() > 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			return cal.getTime();
		} else {
			return data;
		}
	}

}
