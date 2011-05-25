/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.DocumentEscriureProcesPareHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per lligar un document del procés fill cap al
 * procés pare.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentEscriureProcesPareHandler implements ActionHandler, DocumentEscriureProcesPareHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setDocumentCodi(String documentCodi) {}
	public void setVarDocumentCodi(String varDocumentCodi) {}

}
