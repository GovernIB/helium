/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per configurar el context jBPM donat un termini iniciat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
@Deprecated
public class TimerConfigurarAmbTerminiHandler extends ConfigurarAmbTerminiHandler implements TimerConfigurarAmbTerminiHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {
		super.execute(executionContext);
	}

}
