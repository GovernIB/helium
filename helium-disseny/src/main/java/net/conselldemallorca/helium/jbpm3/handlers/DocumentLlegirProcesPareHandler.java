/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.DocumentLlegirProcesPareHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per lligar un document del procés pare cap al
 * procés actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentLlegirProcesPareHandler implements ActionHandler, DocumentLlegirProcesPareHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setDocumentCodi(String documentCodi) {}
	public void setVarDocumentCodi(String varDocumentCodi) {}

}
