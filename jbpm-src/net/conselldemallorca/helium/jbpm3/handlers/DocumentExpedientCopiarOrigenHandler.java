/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.hibernate.Document;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Handler per a copiar un document d'un expedient origen a l'expedient
 * actual.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentExpedientCopiarOrigenHandler extends AbstractHeliumActionHandler {

	private String origenExpedientTipus;
	private String varOrigenExpedientTipus;
	private String origenExpedientNumero;
	private String varOrigenExpedientNumero;
	private String origenExpedientDocument;
	private String varOrigenExpedientDocument;

	private String documentCodi;
	private String varDocumentCodi;



	public void execute(ExecutionContext executionContext) throws Exception {
		String expedientTipusCodi = (String)getValorOVariable(executionContext, origenExpedientTipus, varOrigenExpedientTipus);
		String expedientNumero = (String)getValorOVariable(executionContext, origenExpedientNumero, varOrigenExpedientNumero);
		String documentOrigenCodi = (String)getValorOVariable(executionContext, origenExpedientDocument, varOrigenExpedientDocument);
		// Obté el document de l'expedient d'origen
		ExpedientDto expedientActual = getExpedient(executionContext);
		ExpedientTipus expedientTipus = getDissenyService().findExpedientTipusAmbEntornICodi(
				expedientActual.getEntorn().getId(),
				expedientTipusCodi);
		if (expedientTipus == null)
			throw new JbpmException("No s'ha trobat cap tipus d'expedient amb el codi " + expedientTipusCodi);
		ExpedientDto expedientOrigen = getExpedientService().findExpedientAmbEntornTipusINumero(
				expedientActual.getEntorn().getId(),
				expedientTipus.getId(),
				expedientNumero);
		if (expedientOrigen == null)
			throw new JbpmException("No s'ha trobat l'expedient origen [" + expedientTipusCodi + ", " + expedientNumero + "]");
		ProcessInstance pi = executionContext.getJbpmContext().getProcessInstance(
				new Long(expedientOrigen.getProcessInstanceId()));
		DocumentInfo documentInfo = getDocumentInfo(
				new ExecutionContext(pi.getRootToken()),
				documentOrigenCodi);
		if (documentInfo != null) {
			// El guarda a l'expedient actual
			String documentDestiCodi = (String)getValorOVariable(executionContext, documentCodi, varDocumentCodi);
			Document documentDesti = getDissenyService().findDocumentAmbDefinicioProcesICodi(
					getDefinicioProces(executionContext).getId(),
					documentDestiCodi);
			if (expedientOrigen == null)
				throw new JbpmException("No s'ha trobat el document amb codi " + documentDestiCodi);
			getExpedientService().guardarDocument(
					new Long(executionContext.getProcessInstance().getId()).toString(),
					documentDesti.getId(),
					documentInfo.getDataDocument(),
					documentInfo.getArxiuNom(),
					documentInfo.getArxiuContingut());
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

}
