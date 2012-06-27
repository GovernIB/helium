/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.core.model.service.DocumentHelper;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

/**
 * Handler per lligar un document del procés fill cap al
 * procés pare.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentEscriureProcesPareHandler extends AbstractHeliumActionHandler {

	private String documentCodi;
	private String varDocumentCodi;



	public void execute(ExecutionContext executionContext) throws Exception {
		Token tokenPare = executionContext.getProcessInstance().getSuperProcessToken();
		if (tokenPare != null) {
			String dc = (String)getValorOVariable(executionContext, documentCodi, varDocumentCodi);
			String varDocument = DocumentHelper.PREFIX_VAR_DOCUMENT + dc;
			Object valor = executionContext.getVariable(varDocument);
			if (valor != null) {
				tokenPare.getProcessInstance().getContextInstance().setVariable(varDocument, valor);
			}
		} else {
			throw new JbpmException("Aquest procés(" + executionContext.getProcessInstance().getId() + ") no té cap procés pare");
		}
	}

	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	public void setVarDocumentCodi(String varDocumentCodi) {
		this.varDocumentCodi = varDocumentCodi;
	}

}
