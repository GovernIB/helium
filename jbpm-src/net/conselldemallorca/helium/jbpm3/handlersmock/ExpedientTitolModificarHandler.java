/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlersmock;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar el títol d'un expedient.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientTitolModificarHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setTitol(String titol) {}
	public void setVarTitol(String varTitol) {}

}
