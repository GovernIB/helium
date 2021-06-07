/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Date;

import es.caib.helium.api.dto.TerminiIniciatDto;
import es.caib.helium.jbpm3.integracio.Jbpm3HeliumBridge;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per cancel·lar un termini iniciat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class TerminiCancelarHandler extends AbstractHeliumActionHandler implements TerminiCancelarHandlerInterface {

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
			if (varData != null) {
				Jbpm3HeliumBridge.getInstanceService().terminiCancelar(
						termini.getId(),
						getVariableComData(executionContext, varData));
			} else {
				Jbpm3HeliumBridge.getInstanceService().terminiCancelar(
						termini.getId(),
						new Date());
			}
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
