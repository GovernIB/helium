/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar el responsable d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientResponsableModificarHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setResponsableCodi(String responsableCodi);
	public void setVarResponsableCodi(String varResponsableCodi);

}
