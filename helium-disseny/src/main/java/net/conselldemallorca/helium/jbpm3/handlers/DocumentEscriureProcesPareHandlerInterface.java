/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per lligar un document del procés fill cap al
 * procés pare.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentEscriureProcesPareHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setDocumentCodi(String documentCodi);
	public void setVarDocumentCodi(String varDocumentCodi);

}
