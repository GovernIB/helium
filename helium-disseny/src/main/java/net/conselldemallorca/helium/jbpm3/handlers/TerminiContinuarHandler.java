/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.TerminiContinuarHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per continuar un termini pausat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class TerminiContinuarHandler implements ActionHandler, TerminiContinuarHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setTerminiCodi(String terminiCodi) {}
	public void setVarTerminiCodi(String varTerminiCodi) {}
	public void setVarData(String varData) {}

}
