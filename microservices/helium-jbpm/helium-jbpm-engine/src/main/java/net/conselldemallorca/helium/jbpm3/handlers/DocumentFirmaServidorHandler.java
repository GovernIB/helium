/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import es.caib.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;


/**
 * Handler per a firmar un document amb un certificat emmagatzemat
 * a un servidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentFirmaServidorHandler extends AbstractHeliumActionHandler implements DocumentFirmaServidorHandlerInterface {

	private String documentCodi;
	private String varDocumentCodi;
	private String motiu;

	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler firma servidor");
		String docCodi = (String)getValorOVariable(
				executionContext,
				documentCodi,
				varDocumentCodi);
		if (docCodi == null) {
			throw new JbpmException("No s'ha especificat cap codi de document");
		}
		Jbpm3HeliumBridge.getInstanceService().documentFirmaServidor(
				getProcessInstanceId(executionContext),
				docCodi,
				motiu);
		logger.debug("Handler firma servidor finalitzat amb èxit");
	}

	@Override
	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	@Override
	public void setVarDocumentCodi(String varDocumentCodi) {
		this.varDocumentCodi = varDocumentCodi;
	}
	@Override
	public void setMotiu(String motiu) {
		this.motiu = motiu;
	}

	private static final Log logger = LogFactory.getLog(DocumentFirmaServidorHandler.class);

}
