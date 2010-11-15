/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.model.service.TascaService;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per esborrar un document del procés.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentEsborrarHandler extends AbstractHeliumActionHandler {

	private String documentCodi;
	private String varDocumentCodi;



	public void execute(ExecutionContext executionContext) throws Exception {
		String dc = (String)getValorOVariable(executionContext, documentCodi, varDocumentCodi);
		if (dc == null)
			throw new JbpmException("No s'ha especificat cap codi de document");
		String varCodi = TascaService.PREFIX_DOCUMENT + dc;
		Object valor = executionContext.getVariable(varCodi);
		if (valor != null && valor instanceof Long) {
			Long id = (Long)valor;
			DocumentStore docStore = getDocumentStoreDao().getById(id, false);
			if (docStore != null) {
				getExpedientService().deleteDocument(
						new Long(executionContext.getProcessInstance().getId()).toString(),
						docStore.getId());
			} else {
				throw new JbpmException("No s'ha trobat el contingut del document especificat(" + dc + ")");
			}
		} else {
			throw new JbpmException("No s'ha trobat el document especificat(" + dc + ")");
		}
	}



	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	public void setVarDocumentCodi(String varDocumentCodi) {
		this.varDocumentCodi = varDocumentCodi;
	}

}
