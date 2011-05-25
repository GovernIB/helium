/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.TerminiVariableGuardarHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a configurar una variable de tipus termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class TerminiVariableGuardarHandler implements ActionHandler, TerminiVariableGuardarHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setVarTermini(String varTermini) {}
	public void setAnys(String anys) {}
	public void setVarAnys(String varAnys) {}
	public void setMesos(String mesos) {}
	public void setVarMesos(String varMesos) {}
	public void setDies(String dies) {}
	public void setVarDies(String varDies) {}

}
