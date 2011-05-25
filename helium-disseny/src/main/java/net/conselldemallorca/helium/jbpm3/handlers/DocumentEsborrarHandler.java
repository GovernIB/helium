/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.DocumentEsborrarHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per esborrar un document del procés.
 * 
 * TerminiCancelarHandler.java
 */
@SuppressWarnings("serial")
public class DocumentEsborrarHandler implements ActionHandler, DocumentEsborrarHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setDocumentCodi(String documentCodi) {}
	public void setVarDocumentCodi(String varDocumentCodi) {}

}
