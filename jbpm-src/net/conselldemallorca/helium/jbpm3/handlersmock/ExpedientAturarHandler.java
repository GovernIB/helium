/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlersmock;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per aturar un expedient.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientAturarHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setMotiu(String motiu) {}
	public void setVarMotiu(String varMotiu) {}

}
