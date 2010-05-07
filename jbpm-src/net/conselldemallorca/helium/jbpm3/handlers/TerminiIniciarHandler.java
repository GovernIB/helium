/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

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



	public void execute(ExecutionContext executionContext) throws Exception {
		Termini termini = getTerminiAmbCodi(
				executionContext,
				(String)getValorOVariable(executionContext, terminiCodi, varTerminiCodi));
		if (termini != null) {
			if (varData != null)
				getTerminiService().iniciar(
						termini.getId(),
						getProcessInstanceId(executionContext),
						getVariableComData(executionContext, varData));
			else
				getTerminiService().iniciar(
						termini.getId(),
						getProcessInstanceId(executionContext));
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

}
