/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per buidar els logs d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientBuidaLogHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

}
