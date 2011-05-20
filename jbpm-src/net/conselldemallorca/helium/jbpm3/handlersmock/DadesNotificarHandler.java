/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlersmock;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a la notificació de dades d'un expedient a un sistema extern.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class DadesNotificarHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setUrl(String url) {}
	public void setCodi(String codi) {}
	public void setVars(String vars) {}

}
