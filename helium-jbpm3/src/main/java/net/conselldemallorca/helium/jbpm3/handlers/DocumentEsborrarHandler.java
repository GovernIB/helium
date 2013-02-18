/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
		logger.debug("Inici execució handler esborrar document");
		String dc = (String)getValorOVariable(
				executionContext,
				documentCodi,
				varDocumentCodi);
		if (dc == null)
			throw new JbpmException("No s'ha especificat cap codi de document");
		DocumentInfo docInfo = getDocumentInfo(
				executionContext,
				dc,
				false);
		if (docInfo != null) {
			ExpedientDto expedient = getExpedientActual(executionContext);
			logger.debug("Esborrant document (exp=" + expedient.getIdentificacioPerLogs() + ", document=" + dc + ")");
			getDocumentService().esborrarDocument(
					null,
					getProcessInstanceId(executionContext),
					dc);
		} else {
			throw new JbpmException("No s'ha trobat el document a dins l'expedient (codi=" + dc + ")");
		}
		logger.debug("Handler esborrar document finalitzat amb èxit");
	}

	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	public void setVarDocumentCodi(String varDocumentCodi) {
		this.varDocumentCodi = varDocumentCodi;
	}

	private static final Log logger = LogFactory.getLog(DocumentEsborrarHandler.class);

}
