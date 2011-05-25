/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per configurar una tasca o un timer donat un termini iniciat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigurarAmbTerminiHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setTerminiCodi(String terminiCodi);

	public void setVarTerminiCodi(String varTerminiCodi);

}
