/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlersmock;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a configurar una variable de tipus termini.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class TerminiVariableGuardarHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setVarTermini(String varTermini) {}
	public void setAnys(String anys) {}
	public void setVarAnys(String varAnys) {}
	public void setMesos(String mesos) {}
	public void setVarMesos(String varMesos) {}
	public void setDies(String dies) {}
	public void setVarDies(String varDies) {}

}
