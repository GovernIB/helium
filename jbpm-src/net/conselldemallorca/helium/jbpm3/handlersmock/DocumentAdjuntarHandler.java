/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlersmock;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a adjuntar un document al procés.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentAdjuntarHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setDocumentOrigen(String documentOrigen) {}
	public void setVarDocumentOrigen(String varDocumentOrigen) {}
	public void setTitol(String titol) {}
	public void setVarTitol(String varTitol) {}
	public void setData(String data) {}
	public void setVarData(String varData) {}
	public void setConcatenarTitol(String concatenarTitol) {}
	public void setEsborrarDocument(String esborrarDocument) {}

}
