/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.core.model.service.DocumentHelper;

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
		String varCodi = DocumentHelper.PREFIX_VAR_DOCUMENT + dc;
		Object valor = executionContext.getVariable(varCodi);
		if (valor != null && valor instanceof Long) {
			/*Long id = (Long)valor;
			DocumentStore docStore = getDocumentStoreDao().getById(id, false);
			if (docStore != null) {*/
				getDocumentService().esborrarDocument(
						null,
						new Long(executionContext.getProcessInstance().getId()).toString(),
						dc);
			/*} else {
				throw new JbpmException("No s'ha trobat el contingut del document especificat(" + dc + ")");
			}*/
		}
	}



	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	public void setVarDocumentCodi(String varDocumentCodi) {
		this.varDocumentCodi = varDocumentCodi;
	}

}
