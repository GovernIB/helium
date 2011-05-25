/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

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
		// Obté el document de l'expedient actual
		String documentOrigenCodi = (String)getValorOVariable(executionContext, documentCodi, varDocumentCodi);
		DocumentInfo documentInfo = getDocumentInfo(
				executionContext,
				documentOrigenCodi);
		if (documentInfo != null) {
			String expedientTipusCodi = (String)getValorOVariable(executionContext, destiExpedientTipus, varDestiExpedientTipus);
			String expedientNumero = (String)getValorOVariable(executionContext, destiExpedientNumero, varDestiExpedientNumero);
			String documentDestiCodi = (String)getValorOVariable(executionContext, destiExpedientDocument, varDestiExpedientDocument);
			ExpedientDto expedientActual = getExpedient(executionContext);
			ExpedientTipus expedientTipus = getDissenyService().findExpedientTipusAmbEntornICodi(
					expedientActual.getEntorn().getId(),
					expedientTipusCodi);
			if (expedientTipus == null)
				throw new JbpmException("No s'ha trobat cap tipus d'expedient amb el codi " + expedientTipusCodi);
			ExpedientDto expedientDesti = getExpedientService().findExpedientAmbEntornTipusINumero(
					expedientActual.getEntorn().getId(),
					expedientTipus.getId(),
					expedientNumero);
			if (expedientDesti == null)
				throw new JbpmException("No s'ha trobat l'expedient origen [" + expedientTipusCodi + ", " + expedientNumero + "]");
			ProcessInstance pi = executionContext.getJbpmContext().getProcessInstance(
					new Long(expedientDesti.getProcessInstanceId()));
			Document documentDesti = getDissenyService().findDocumentAmbDefinicioProcesICodi(
					getDefinicioProces(new ExecutionContext(pi.getRootToken())).getId(),
					documentDestiCodi);
			getExpedientService().guardarDocument(
					new Long(pi.getId()).toString(),
					documentDesti.getId(),
					documentInfo.getDataDocument(),
					documentInfo.getArxiuNom(),
					documentInfo.getArxiuContingut());
		}
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

}
