/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.RegistreSortidaHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a interactuar amb el registre de sortida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class RegistreSortidaHandler extends RegistreHandler implements ActionHandler, RegistreSortidaHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

}
