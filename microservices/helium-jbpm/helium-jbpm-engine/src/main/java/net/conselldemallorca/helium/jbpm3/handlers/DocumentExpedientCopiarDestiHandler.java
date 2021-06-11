/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import es.caib.helium.api.dto.ExpedientDto;
import es.caib.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Handler per a copiar un document de l'expedient actual a un altre
 * expedient destí.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentExpedientCopiarDestiHandler extends AbstractHeliumActionHandler implements DocumentExpedientCopiarDestiHandlerInterface {

	private String destiExpedientTipus;
	private String varDestiExpedientTipus;
	private String destiExpedientNumero;
	private String varDestiExpedientNumero;
	private String destiExpedientDocument;
	private String varDestiExpedientDocument;

	private String documentCodi;
	private String varDocumentCodi;

	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler copiar document a desti");
		String documentOrigenCodi = (String)getValorOVariable(
				executionContext,
				documentCodi,
				varDocumentCodi);
		if (documentOrigenCodi == null)
			throw new JbpmException("No s'ha especificat cap codi de document");
		DocumentInfo docInfo = getDocumentInfo(
				executionContext,
				documentOrigenCodi,
				true);
		if (docInfo != null) {
			ExpedientDto expedient = getExpedientActual(executionContext);
			logger.debug("Copiant document a desti (exp=" + expedient.getIdentificacioPerLogs() + ", document=" + documentOrigenCodi + ")");
			String expedientTipusCodi = (String)getValorOVariable(
					executionContext,
					destiExpedientTipus,
					varDestiExpedientTipus);
			String expedientNumero = (String)getValorOVariable(
					executionContext,
					destiExpedientNumero,
					varDestiExpedientNumero);
			String documentDestiCodi = (String)getValorOVariable(
					executionContext,
					destiExpedientDocument,
					varDestiExpedientDocument);
			String processInstanceId = Jbpm3HeliumBridge.getInstanceService().getProcessInstanceIdAmbEntornITipusINumero(
					expedient.getEntorn().getId(),
					expedientTipusCodi,
					expedientNumero);
			if (processInstanceId != null) {
				ProcessInstance pi = executionContext.getJbpmContext().getProcessInstance(new Long(processInstanceId));
				Long documentStoreId = Jbpm3HeliumBridge.getInstanceService().documentExpedientGuardar(
						new Long(pi.getId()).toString(),
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
				throw new JbpmException("No s'ha trobat l'expedient destí (expedientTipusCodi=" + expedientTipusCodi + ", expedientNumero=" + destiExpedientNumero + ")");
			}
		} else {
			throw new JbpmException("No s'ha trobat el document a dins l'expedient (codi=" + documentOrigenCodi + ")");
		}
		logger.debug("Handler copiar document a desti finalitzat amb èxit");
	}



	public void setDestiExpedientTipus(String destiExpedientTipus) {
		this.destiExpedientTipus = destiExpedientTipus;
	}
	public void setVarDestiExpedientTipus(String varDestiExpedientTipus) {
		this.varDestiExpedientTipus = varDestiExpedientTipus;
	}
	public void setDestiExpedientNumero(String destiExpedientNumero) {
		this.destiExpedientNumero = destiExpedientNumero;
	}
	public void setVarDestiExpedientNumero(String varDestiExpedientNumero) {
		this.varDestiExpedientNumero = varDestiExpedientNumero;
	}
	public void setDestiExpedientDocument(String destiExpedientDocument) {
		this.destiExpedientDocument = destiExpedientDocument;
	}
	public void setVarDestiExpedientDocument(String varDestiExpedientDocument) {
		this.varDestiExpedientDocument = varDestiExpedientDocument;
	}
	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	public void setVarDocumentCodi(String varDocumentCodi) {
		this.varDocumentCodi = varDocumentCodi;
	}

	private static final Log logger = LogFactory.getLog(DocumentExpedientCopiarDestiHandler.class);

}
