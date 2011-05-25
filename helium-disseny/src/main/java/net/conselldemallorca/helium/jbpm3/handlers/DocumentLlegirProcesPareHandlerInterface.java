/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per lligar un document del procés pare cap al
 * procés actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentLlegirProcesPareHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setDocumentCodi(String documentCodi);
	public void setVarDocumentCodi(String varDocumentCodi);

}
