/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per calcular la data de fi d'un termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiCalcularDataFiHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setTerminiCodi(String terminiCodi);
	public void setVarTerminiCodi(String varTerminiCodi);
	public void setVarData(String varData);
	public void setSumarUnDia(String sumarUnDia);
	public void setVarTermini(String varTermini);
	public void setVarDataFi(String varDataFi);

}
