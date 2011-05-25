/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.DocumentExpedientCopiarOrigenHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a copiar un document d'un expedient origen a
 * l'expedient actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentExpedientCopiarOrigenHandler implements ActionHandler, DocumentExpedientCopiarOrigenHandlerInterface {

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
