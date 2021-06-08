/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Date;

import es.caib.helium.api.dto.ExpedientDto;
import es.caib.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a adjuntar un document al procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentAdjuntarHandler extends AbstractHeliumActionHandler implements DocumentAdjuntarHandlerInterface {

	private String documentOrigen;
	private String varDocumentOrigen;
	private String titol;
	private String varTitol;
	private String data;
	private String varData;
	private String concatenarTitol;
	private String esborrarDocument;



	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler adjuntar document");
		String dor = (String)getValorOVariable(
				executionContext,
				documentOrigen,
				varDocumentOrigen);
		if (dor == null || dor.length() == 0) {
			throw new JbpmException("No s'ha especificat cap document per adjuntar: " + documentOrigen);
		}
		
		String varCodi = Jbpm3HeliumBridge.getInstanceService().getCodiVariablePerDocumentCodi(dor);
		Object valor = executionContext.getVariable(varCodi);
		if (valor != null && valor instanceof Long) {
			DocumentInfo docInfo = getDocumentInfo(
					executionContext,
					dor,
					true);
			if (docInfo != null) {
				ExpedientDto expedient = getExpedientActual(executionContext);
				logger.debug("Adjuntant document (exp=" + expedient.getIdentificacioPerLogs() + ", document=" + dor + ")");
				String tit = (String)getValorOVariable(
						executionContext,
						titol,
						varTitol);
				String adjuntTitol;
				if (isConcatenarTitol())
					adjuntTitol = docInfo.getTitol() + " " + tit;
				else
					adjuntTitol = tit;
				Date adjuntData = getValorOVariableData(executionContext, data, varData);
				Jbpm3HeliumBridge.getInstanceService().documentExpedientAdjuntar(
						getProcessInstanceId(executionContext),
						null,
						adjuntTitol,
						(adjuntData != null) ? adjuntData : docInfo.getDataDocument(),
						docInfo.getArxiuNom(),
						docInfo.getArxiuContingut());
				if (isEsborrarDocument()) {
					Jbpm3HeliumBridge.getInstanceService().documentExpedientEsborrar(
							null,
							getProcessInstanceId(executionContext),
							docInfo.getCodiDocument());
				}
			} else {
				throw new JbpmException("No s'ha trobat el contingut del document especificat(" + dor + ")");
			}
		}
		logger.debug("Handler adjuntar document finalitzat amb èxit");
	}

	public void setDocumentOrigen(String documentOrigen) {
		this.documentOrigen = documentOrigen;
	}
	public void setVarDocumentOrigen(String varDocumentOrigen) {
		this.varDocumentOrigen = varDocumentOrigen;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public void setVarTitol(String varTitol) {
		this.varTitol = varTitol;
	}
	public void setData(String data) {
		this.data = data;
	}
	public void setVarData(String varData) {
		this.varData = varData;
	}
	public void setConcatenarTitol(String concatenarTitol) {
		this.concatenarTitol = concatenarTitol;
	}
	public void setEsborrarDocument(String esborrarDocument) {
		this.esborrarDocument = esborrarDocument;
	}

	private boolean isConcatenarTitol() {
		return "true".equalsIgnoreCase(concatenarTitol) || "si".equalsIgnoreCase(concatenarTitol);
	}
	private boolean isEsborrarDocument() {
		return "true".equalsIgnoreCase(esborrarDocument) || "si".equalsIgnoreCase(esborrarDocument);
	}

	private static final Log logger = LogFactory.getLog(DocumentAdjuntarHandler.class);

}
