/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

import es.caib.helium.logic.intf.dto.ExpedientDto;

/**
 * Handler per a copiar un document d'un expedient origen a l'expedient
 * actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentExpedientCopiarOrigenHandler extends AbstractHeliumActionHandler implements DocumentExpedientCopiarOrigenHandlerInterface{

	private String origenExpedientTipus;
	private String varOrigenExpedientTipus;
	private String origenExpedientNumero;
	private String varOrigenExpedientNumero;
	private String origenExpedientDocument;
	private String varOrigenExpedientDocument;

	private String documentCodi;
	private String varDocumentCodi;



	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler copiar document d'origen");
		ExpedientDto expedient = getExpedientActual(executionContext);
		String expedientTipusCodi = (String)getValorOVariable(
				executionContext,
				origenExpedientTipus,
				varOrigenExpedientTipus);
		String expedientNumero = (String)getValorOVariable(
				executionContext,
				origenExpedientNumero,
				varOrigenExpedientNumero);
		String documentOrigenCodi = (String)getValorOVariable(
				executionContext,
				origenExpedientDocument,
				varOrigenExpedientDocument);
		String documentDestiCodi = (String)getValorOVariable(executionContext, documentCodi, varDocumentCodi);
		if (documentDestiCodi != null) {
			logger.debug("Copiant document d'origen (exp=" + expedient.getIdentificacioPerLogs() + ", document=" + documentOrigenCodi + ", expedientTipusCodi=" + expedientTipusCodi + ", expedientNumero=" + expedientNumero + ")");
			ExpedientDto expedientOrigen = Jbpm3HeliumBridge.getInstanceService().getExpedientAmbEntornITipusINumero(
					expedient.getEntorn().getId(),
					expedientTipusCodi,
					expedientNumero);
			ProcessInstance pi = executionContext.getJbpmContext().getProcessInstance(
					new Long(expedientOrigen.getProcessInstanceId()));
			DocumentInfo docInfo = getDocumentInfo(
					new ExecutionContext(pi.getRootToken()),
					documentOrigenCodi,
					true);
			if (docInfo != null) {
				Long documentStoreId = Jbpm3HeliumBridge.getInstanceService().documentExpedientGuardar(
						getProcessInstanceId(executionContext),
						documentDestiCodi,
						docInfo.getDataDocument(),
						docInfo.getArxiuNom(),
						docInfo.getArxiuContingut());
				if (docInfo.isRegistrat()) {
					Jbpm3HeliumBridge.getInstanceService().documentExpedientGuardarDadesRegistre(
							documentStoreId,
							docInfo.getRegistreNumero(),
							docInfo.getRegistreData(),
							docInfo.getRegistreOficinaCodi(),
							docInfo.getRegistreOficinaNom(),
							docInfo.isRegistreEntrada());
				}
			} else {
				// throw new JbpmException("No s'ha trobat el document a dins l'expedient (codi=" + documentOrigenCodi + ")");
				logger.debug("No s'ha trobat el document a dins l'expedient (codi=" + documentOrigenCodi + ")");
			}
		} else {
			throw new JbpmException("No s'ha especificat el codi del document destí: " + documentCodi + " - " + varDocumentCodi);
		}
	}

	public void setOrigenExpedientTipus(String origenExpedientTipus) {
		this.origenExpedientTipus = origenExpedientTipus;
	}
	public void setVarOrigenExpedientTipus(String varOrigenExpedientTipus) {
		this.varOrigenExpedientTipus = varOrigenExpedientTipus;
	}
	public void setOrigenExpedientNumero(String origenExpedientNumero) {
		this.origenExpedientNumero = origenExpedientNumero;
	}
	public void setVarOrigenExpedientNumero(String varOrigenExpedientNumero) {
		this.varOrigenExpedientNumero = varOrigenExpedientNumero;
	}
	public void setOrigenExpedientDocument(String origenExpedientDocument) {
		this.origenExpedientDocument = origenExpedientDocument;
	}
	public void setVarOrigenExpedientDocument(String varOrigenExpedientDocument) {
		this.varOrigenExpedientDocument = varOrigenExpedientDocument;
	}
	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	public void setVarDocumentCodi(String varDocumentCodi) {
		this.varDocumentCodi = varDocumentCodi;
	}

	private static final Log logger = LogFactory.getLog(DocumentExpedientCopiarDestiHandler.class);

}
