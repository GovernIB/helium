/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.DocumentExpedientCopiarDestiHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a copiar un document de l'expedient actual a un altre
 * expedient destí.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentExpedientCopiarDestiHandler implements ActionHandler, DocumentExpedientCopiarDestiHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setDestiExpedientTipus(String destiExpedientTipus) {}
	public void setVarDestiExpedientTipus(String varDestiExpedientTipus) {}
	public void setDestiExpedientNumero(String destiExpedientNumero) {}
	public void setVarDestiExpedientNumero(String varDestiExpedientNumero) {}
	public void setDestiExpedientDocument(String destiExpedientDocument) {}
	public void setVarDestiExpedientDocument(String varDestiExpedientDocument) {}
	public void setDocumentCodi(String documentCodi) {}
	public void setVarDocumentCodi(String varDocumentCodi) {}

}
