/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar el títol d'una instància de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ProcesTitolModificarHandler extends AbstractHeliumActionHandler implements ProcesTitolModificarHandlerInterface {

	private String titol;
	private String varTitol;



	public void execute(ExecutionContext executionContext) throws Exception {
		executionContext.getProcessInstance().setKey(
				(String)getValorOVariable(
						executionContext,
						titol,
						varTitol));
	}

	public void setTitol(String titol) {
		this.titol = titol;
	}
	public void setVarTitol(String varTitol) {
		this.varTitol = varTitol;
	}

}
