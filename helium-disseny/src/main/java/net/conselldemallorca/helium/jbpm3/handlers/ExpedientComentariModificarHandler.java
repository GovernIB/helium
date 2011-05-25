/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.ExpedientComentariModificarHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar el comentari d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientComentariModificarHandler implements ActionHandler, ExpedientComentariModificarHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setComentari(String comentari) {}
	public void setVarComentari(String varComentari) {}

}
