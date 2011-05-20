/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.model.service.TascaService;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

/**
 * Handler per lligar un document del procés pare cap al
 * procés actual.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentLlegirProcesPareHandler extends AbstractHeliumActionHandler {

	private String documentCodi;
	private String varDocumentCodi;



	public void execute(ExecutionContext executionContext) throws Exception {
		Token tokenPare = executionContext.getProcessInstance().getSuperProcessToken();
		if (tokenPare != null) {
			String dc = (String)getValorOVariable(executionContext, documentCodi, varDocumentCodi);
			String varDocument = TascaService.PREFIX_DOCUMENT + dc;
			Object valor = tokenPare.getProcessInstance().getContextInstance().getVariable(varDocument);
			if (valor != null) {
				executionContext.setVariable(varDocument, valor);
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
