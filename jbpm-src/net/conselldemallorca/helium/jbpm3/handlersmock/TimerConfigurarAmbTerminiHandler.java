/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlersmock;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per configurar un timer donat un termini iniciat.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
@Deprecated
public class TimerConfigurarAmbTerminiHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {
	}

	public void setTerminiCodi(String terminiCodi) {
	}

}
