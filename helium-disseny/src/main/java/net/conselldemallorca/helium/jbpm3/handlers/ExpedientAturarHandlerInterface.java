/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per aturar un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientAturarHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setMotiu(String motiu);
	public void setVarMotiu(String varMotiu);

}
