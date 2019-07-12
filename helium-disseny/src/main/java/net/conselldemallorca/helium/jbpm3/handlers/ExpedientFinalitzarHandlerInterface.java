/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per finalitzar l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientFinalitzarHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

}
