/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per esborrar un document del procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentEsborrarHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setDocumentCodi(String documentCodi);
	public void setVarDocumentCodi(String varDocumentCodi);

}
