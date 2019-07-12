package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public interface ExpedientDesfinalitzarHandlerInterficie extends ActionHandler {
	public void execute(ExecutionContext executionContext) throws Exception;

	public void setReprendre(String reprendre);
	public void setVarReprendre(String varReprendre);
}
