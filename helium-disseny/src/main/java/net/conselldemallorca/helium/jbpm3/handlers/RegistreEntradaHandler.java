/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.RegistreEntradaHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a interactuar amb el registre d'entrada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class RegistreEntradaHandler extends RegistreHandler implements ActionHandler, RegistreEntradaHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

}
