/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per calcular la data d'inici d'un termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiCalcularDataIniciHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setTerminiCodi(String terminiCodi);
	public void setVarTerminiCodi(String varTerminiCodi);
	public void setVarData(String varData);
	public void setRestarUnDia(String restarUnDia);
	public void setVarTermini(String varTermini);
	public void setVarDataInici(String varDataInici);

}
