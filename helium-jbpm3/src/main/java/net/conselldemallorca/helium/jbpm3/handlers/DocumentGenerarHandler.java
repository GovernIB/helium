/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Date;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a generar un document de forma automàtica a 
 * partir d'una plantilla.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentGenerarHandler extends AbstractHeliumActionHandler implements DocumentGenerarHandlerInterface {

	private String documentCodi;
	private String varDocumentCodi;
	private String data;
	private String varData;



	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler generar document");
		String dc = (String)getValorOVariable(
				executionContext,
				documentCodi,
				varDocumentCodi);
		if (dc == null)
			throw new JbpmException("No s'ha especificat cap codi de document");
		Date docData = getValorOVariableData(executionContext, data, varData);
		if (docData == null)
			docData = new Date();
		Jbpm3HeliumBridge.getInstanceService().documentGenerarAmbPlantilla(
				null,
				getProcessInstanceId(executionContext),
				dc,
				docData);
		logger.debug("Handler generar document finalitzat amb èxit");
	}



	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	public void setVarDocumentCodi(String varDocumentCodi) {
		this.varDocumentCodi = varDocumentCodi;
	}
	public void setData(String data) {
		this.data = data;
	}
	public void setVarData(String varData) {
		this.varData = varData;
	}

	private static final Log logger = LogFactory.getLog(DocumentGenerarHandler.class);

}
