/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlersmock;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a crear una alerta a un usuari.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class AlertaCrearHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setUsuari(String usuari) {}
	public void setVarUsuari(String varUsuari) {}
	public void setText(String text) {}
	public void setVarText(String varText) {}

}
