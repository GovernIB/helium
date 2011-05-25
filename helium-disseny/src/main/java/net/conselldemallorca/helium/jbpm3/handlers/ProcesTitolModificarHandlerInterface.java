/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar el títol d'una instància de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcesTitolModificarHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setTitol(String titol);
	public void setVarTitol(String varTitol);

}
