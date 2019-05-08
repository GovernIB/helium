package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per desfinalitzar expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@SuppressWarnings("serial")
public class ExpedientDesfinalitzarHandler implements ActionHandler, ExpedientDesfinalitzarHandlerInterficie {
	public void execute(ExecutionContext executionContext) throws Exception{}

	public void setReprendre(String reprendre) {}
	public void setVarReprendre(String varReprendre) {}
}
