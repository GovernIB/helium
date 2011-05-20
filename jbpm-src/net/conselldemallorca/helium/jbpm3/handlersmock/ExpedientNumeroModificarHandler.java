/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlersmock;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar el número d'un expedient.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientNumeroModificarHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setNumero(String numero) {}
	public void setVarNumero(String varNumero) {}

}
