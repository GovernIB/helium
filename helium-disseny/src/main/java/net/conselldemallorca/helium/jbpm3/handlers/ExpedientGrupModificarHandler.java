/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a configurar el grup al qual pertany un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientGrupModificarHandler implements ActionHandler,ExpedientGrupModificarHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setGrup(String grup) {}
	public void setVarGrup(String varGrup) {}

}
