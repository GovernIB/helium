/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a firmar un document amb firma de servidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentFirmaServidorHandler implements ActionHandler, DocumentFirmaServidorHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setDocumentCodi(String documentCodi) {}
	public void setVarDocumentCodi(String varDocumentCodi) {}
	public void setMotiu(String motiu) {}

}
