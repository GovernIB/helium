/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlersmock;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per lligar un document del procés pare cap al
 * procés actual.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentExpedientCopiarHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setOrigenExpedientTipus(String origenExpedientTipus) {}
	public void setVarOrigenExpedientTipus(String varOrigenExpedientTipus) {}
	public void setOrigenExpedientNumero(String origenExpedientNumero) {}
	public void setVarOrigenExpedientNumero(String varOrigenExpedientNumero) {}
	public void setOrigenExpedientDocument(String origenExpedientDocument) {}
	public void setVarOrigenExpedientDocument(String varOrigenExpedientDocument) {}
	public void setDocumentCodi(String documentCodi) {}
	public void setVarDocumentCodi(String varDocumentCodi) {}

}
