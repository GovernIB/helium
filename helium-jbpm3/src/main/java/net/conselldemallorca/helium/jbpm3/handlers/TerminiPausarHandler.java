/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Date;

import org.jbpm.graph.exe.ExecutionContext;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;

/**
 * Handler per pausar un termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class TerminiPausarHandler extends AbstractHeliumActionHandler implements TerminiPausarHandlerInterface {

	private String terminiCodi;
	private String varTerminiCodi;
	private String varData;



	public void execute(ExecutionContext executionContext) throws Exception {
		
		TerminiIniciatDto termini = null;
		try {
			termini = getTerminiIniciatAmbCodi(
					executionContext,
					(String)getValorOVariable(executionContext, terminiCodi, varTerminiCodi));
		} catch (NoTrobatException ex) {
			termini = null;
		}
		
		if (termini != null) {
			if (varData != null) {
				
				Date valorData = null;
				try {
					valorData = getVariableComData(executionContext, varData);
				} catch (NoTrobatException ex) {
					valorData = null;
				}
				
				if (valorData != null)
					Jbpm3HeliumBridge.getInstanceService().terminiPausar(
							termini.getId(),
							valorData);
			} else {
				Jbpm3HeliumBridge.getInstanceService().terminiPausar(
						termini.getId(),
						new Date());
			}
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
