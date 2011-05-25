/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.ExpedientEstatModificarHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar l'estat d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientEstatModificarHandler implements ActionHandler, ExpedientEstatModificarHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setEstatCodi(String estatCodi) {}
	public void setVarEstatCodi(String varEstatCodi) {}

}
