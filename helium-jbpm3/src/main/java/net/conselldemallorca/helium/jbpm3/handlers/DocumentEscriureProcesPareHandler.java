/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
		logger.debug("Inici execució handler enllaçar document del pare");
		Token tokenPare = executionContext.getProcessInstance().getSuperProcessToken();
		if (tokenPare != null) {
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
				logger.debug("Enllaçant document del pare (exp=" + expedient.getIdentificacioPerLogs() + ", document=" + dc + ")");
				String varDocument = Jbpm3HeliumBridge.getInstanceService().getCodiVariablePerDocumentCodi(dc);
				tokenPare.getProcessInstance().getContextInstance().setVariable(
						varDocument,
						executionContext.getVariable(varDocument));
			} else {
				throw new JbpmException("No s'ha trobat el document a dins l'expedient (codi=" + dc + ")");
			}
		} else {
			throw new JbpmException("El procés no té pare (id=" + getProcessInstanceId(executionContext) + ")");
		}
		logger.debug("Handler enllaçar document del pare finalitzat amb èxit");
	}

	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	public void setVarDocumentCodi(String varDocumentCodi) {
		this.varDocumentCodi = varDocumentCodi;
	}

	private static final Log logger = LogFactory.getLog(DocumentEscriureProcesPareHandler.class);

}
