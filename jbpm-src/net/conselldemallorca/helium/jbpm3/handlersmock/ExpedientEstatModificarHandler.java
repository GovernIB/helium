/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlersmock;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar l'estat d'un expedient.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientEstatModificarHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setEstatCodi(String estatCodi) {}
	public void setVarEstatCodi(String varEstatCodi) {}

}
