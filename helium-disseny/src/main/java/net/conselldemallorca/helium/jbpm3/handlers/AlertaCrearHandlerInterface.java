/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a crear una alerta a un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AlertaCrearHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setUsuari(String usuari);
	public void setVarUsuari(String varUsuari);
	public void setText(String text);
	public void setVarText(String varText);

}
