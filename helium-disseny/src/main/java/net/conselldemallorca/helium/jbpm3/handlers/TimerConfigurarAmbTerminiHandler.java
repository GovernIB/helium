/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.TimerConfigurarAmbTerminiHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per configurar un timer donat un termini iniciat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
@Deprecated
public class TimerConfigurarAmbTerminiHandler implements ActionHandler, TimerConfigurarAmbTerminiHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setTerminiCodi(String terminiCodi) {}
	public void setVarTerminiCodi(String varTerminiCodi) {}

}
