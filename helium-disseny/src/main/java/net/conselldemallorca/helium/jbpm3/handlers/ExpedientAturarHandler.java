/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.ExpedientAturarHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per aturar un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientAturarHandler implements ActionHandler,ExpedientAturarHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setMotiu(String motiu) {}
	public void setVarMotiu(String varMotiu) {}

}
