/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Date;

import net.conselldemallorca.helium.model.hibernate.TerminiIniciat;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per cancel·lar un termini iniciat.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class TerminiCancelarHandler extends AbstractHeliumActionHandler {

	private String terminiCodi;
	private String varTerminiCodi;
	private String varData;



	public void execute(ExecutionContext executionContext) throws Exception {
		TerminiIniciat termini = getTerminiIniciatAmbCodi(
				executionContext,
				(String)getValorOVariable(executionContext, terminiCodi, varTerminiCodi));
		if (termini != null) {
			if (varData != null)
				getTerminiService().cancelar(
						termini.getId(),
						getVariableComData(executionContext, varData));
			else
				getTerminiService().cancelar(termini.getId(), new Date());
		} else {
			throw new JbpmException("No existeix cap termini iniciat amb aquest codi '" + terminiCodi + "'");
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
