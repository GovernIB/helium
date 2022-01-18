/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;

/**
 * Handler per consultar el CSV i/o la URL de verificació de firmes d'un document firmat. A partir
 * del codi del document consulta la informació i posa el CSV i la URL de verificació de firmes
 * en les variables especificades per codi.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentConsultarHandler extends BasicActionHandler implements DocumentConsultarHandlerInterface {

	private String document;
	private String varDocument;
	private String varCsv;
	private String varUrl;

	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler adjuntar document");
		Object valor = getValorOVariable(executionContext, document, varDocument);
		if (valor == null || "".equals(valor)) {
			throw new JbpmException("No s'ha especificat cap document per consultar: " + valor);
		}
		String documentCodi = valor.toString();
		DocumentInfo docInfo = getDocumentInfo(
				executionContext,
				documentCodi,
				false);
		if (docInfo == null) {
			throw new JbpmException("No s'ha trobat el contingut del document especificat(" + documentCodi + ")");
		}
		if (docInfo.isSignat()) {
			// CSV
			if (varCsv != null && !varCsv.isEmpty()) {
				this.setVariableGlobal(executionContext, varCsv, docInfo.getCsv());
			}
			// URL
			if (varUrl != null && !varUrl.isEmpty()) {
				this.setVariableGlobal(executionContext, varUrl, docInfo.getUrlVerificacioSignatures());
			}
		}
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public void setVarDocument(String varDocument) {
		this.varDocument = varDocument;
	}

	public void setVarCsv(String varCsv) {
		this.varCsv = varCsv;
	}

	public void setVarUrl(String varUrl) {
		this.varUrl = varUrl;
	}

	private static final Log logger = LogFactory.getLog(DocumentConsultarHandler.class);

}
