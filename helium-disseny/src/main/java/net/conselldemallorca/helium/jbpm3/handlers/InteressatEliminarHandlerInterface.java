/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface InteressatEliminarHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setCodi(String codi);
	public void setVarCodi(String varCodi);

}
