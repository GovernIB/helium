/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.ExpedientTitolModificarHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar el títol d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientTitolModificarHandler implements ActionHandler, ExpedientTitolModificarHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setTitol(String titol) {}
	public void setVarTitol(String varTitol) {}

}
