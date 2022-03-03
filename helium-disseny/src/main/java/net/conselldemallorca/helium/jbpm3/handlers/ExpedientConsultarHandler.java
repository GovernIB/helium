/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per consultar informació d'un expedient i deixar la informació en variables. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@SuppressWarnings("serial")
public class ExpedientConsultarHandler implements ActionHandler, ExpedientConsultarHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setVarRegistreNumero(String varRegistreNumero) {}
	public void setVarTitol(String varTitol) {}
	public void setVarNumero(String varNumero) {}
	public void setVarDataInici(String varDataInici) {}

}
