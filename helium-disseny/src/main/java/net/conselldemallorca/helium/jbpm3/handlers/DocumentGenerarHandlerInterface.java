/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a generar un document de forma automàtica a 
 * partir d'una plantilla.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentGenerarHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;



	public void setDocumentCodi(String documentCodi);
	public void setVarDocumentCodi(String varDocumentCodi);
	public void setData(String data);
	public void setVarData(String varData);

}
