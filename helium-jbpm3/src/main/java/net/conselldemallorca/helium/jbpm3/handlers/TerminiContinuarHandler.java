/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Date;

import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per continuar un termini pausat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class TerminiContinuarHandler extends AbstractHeliumActionHandler implements TerminiContinuarHandlerInterface {

	private String terminiCodi;
	private String varTerminiCodi;
	private String varData;



	public void execute(ExecutionContext executionContext) throws Exception {
		TerminiIniciatDto termini = getTerminiIniciatAmbCodi(
				executionContext,
				(String)getValorOVariable(
						executionContext,
						terminiCodi,
						varTerminiCodi));
		if (termini != null) {
			if (varData != null)
				getTerminiService().continuar(
						termini.getId(),
						getVariableComData(
								executionContext,
								varData));
			else
				getTerminiService().continuar(termini.getId(), new Date());
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
