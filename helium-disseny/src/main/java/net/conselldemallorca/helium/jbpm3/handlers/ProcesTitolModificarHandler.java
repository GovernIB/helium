/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.ProcesTitolModificarHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar el títol d'una instància de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ProcesTitolModificarHandler implements ActionHandler, ProcesTitolModificarHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setTitol(String titol) {}
	public void setVarTitol(String varTitol) {}

}
