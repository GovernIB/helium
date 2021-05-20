/**
 * 
 */
package es.caib.helium.jbpm3.handlers;

import es.caib.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.jbpm3.handlers.DocumentEsborrarHandlerInterface;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per esborrar un document del procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentEsborrarHandler extends AbstractHeliumActionHandler implements DocumentEsborrarHandlerInterface {

	private String documentCodi;
	private String varDocumentCodi;



	public void execute(ExecutionContext executionContext) throws Exception {
		String dc = (String)getValorOVariable(executionContext, documentCodi, varDocumentCodi);
		if (dc == null)
			throw new JbpmException("No s'ha especificat cap codi de document");
		String varCodi = Jbpm3HeliumBridge.getInstanceService().getCodiVariablePerDocumentCodi(dc);
		Object valor = executionContext.getVariable(varCodi);
		if (valor != null && valor instanceof Long) {
			Jbpm3HeliumBridge.getInstanceService().documentExpedientEsborrar(
					null,
					getProcessInstanceId(executionContext),
					dc);
		}
	}



	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	public void setVarDocumentCodi(String varDocumentCodi) {
		this.varDocumentCodi = varDocumentCodi;
	}

}
