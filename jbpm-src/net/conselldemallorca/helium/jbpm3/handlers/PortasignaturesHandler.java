package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Date;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.model.service.TascaService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per fer la integració amb portasignatures.
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */
@SuppressWarnings("serial")
public class PortasignaturesHandler extends AbstractHeliumActionHandler {

	private String varResponsableCodi;
	private String responsableCodi;
	private String varDocument;
	private String document;
	private String varImportancia;
	private String importancia;
	private String varDataLimit;
	private String dataLimit;
	private String varTransicioOK;
	private String transicioOK;
	private String varTransicioKO;
	private String transicioKO;

	public void execute(ExecutionContext executionContext) throws Exception {
		try {
			Boolean personaCodi = getValorOVariable(executionContext, responsableCodi, varResponsableCodi) != null;
			Persona persona = null;
			if (personaCodi) {
				persona = getPersonaAmbCodi(
						(String)getValorOVariable(executionContext, responsableCodi, varResponsableCodi));
			} else {
				throw new JbpmException("No s'ha pogut trobar la persona '" + getValorOVariable(executionContext, responsableCodi, varResponsableCodi) + "'.");
			}
			
			Boolean documentCodi = getValorOVariable(executionContext, document, varDocument) != null;
			Long documentStoreId = null;
			if (documentCodi) {
				String varCodi = TascaService.PREFIX_DOCUMENT +
						(String)getValorOVariable(executionContext, document, varDocument);
				documentStoreId = (Long)executionContext.getVariable(varCodi);
			} else {
				throw new JbpmException("No s'ha pogut trobar el document '" + getValorOVariable(executionContext, document, varDocument) + "'.");
			}

			getPluginService().enviarPortasignatures(
					persona,
					documentStoreId,
					this.getExpedient(executionContext),
					(String)getValorOVariable(executionContext, importancia, varImportancia),
					(Date)getValorOVariable(executionContext, dataLimit, varDataLimit),
					executionContext.getToken().getId(),
					(String)getValorOVariable(executionContext, transicioOK, varTransicioOK),
					(String)getValorOVariable(executionContext, transicioKO, varTransicioKO));
		} catch (Exception e) {
			logger.error("Error PortasignaturesHandler. ", e);
			throw new JbpmException("No s'ha pogut enviar el document al portasignatures", e);
		}
	}

	public void setVarResponsableCodi(String varResponsableCodi) {
		this.varResponsableCodi = varResponsableCodi;
	}
	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}
	public void setVarDocument(String varDocument) {
		this.varDocument = varDocument;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public void setVarImportancia(String varImportancia) {
		this.varImportancia = varImportancia;
	}
	public void setImportancia(String importancia) {
		this.importancia = importancia;
	}
	public void setVarDataLimit(String varDataLimit) {
		this.varDataLimit = varDataLimit;
	}
	public void setDataLimit(String dataLimit) {
		this.dataLimit = dataLimit;
	}
	public void setVarTransicioOK(String varTransicioOK) {
		this.varTransicioOK = varTransicioOK;
	}
	public void setTransicioOK(String transicioOK) {
		this.transicioOK = transicioOK;
	}
	public void setVarTransicioKO(String varTransicioKO) {
		this.varTransicioKO = varTransicioKO;
	}
	public void setTransicioKO(String transicioKO) {
		this.transicioKO = transicioKO;
	}

	private static final Log logger = LogFactory.getLog(PortasignaturesHandler.class);

}
