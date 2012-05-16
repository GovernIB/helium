/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Date;

import net.conselldemallorca.helium.core.model.hibernate.Document;

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
		String docCodi = (String)getValorOVariable(executionContext, documentCodi, varDocumentCodi);
		if (docCodi == null)
			throw new JbpmException("No s'ha especificat el codi del document");
		Document docDisseny = getDissenyService().findDocumentAmbDefinicioProcesICodi(
				getDefinicioProces(executionContext).getId(),
				docCodi);
		if (docDisseny == null)
			throw new JbpmException("No s'ha trobat cap document amb el codi " + docCodi);
		Date docData = getValorOVariableData(executionContext, data, varData);
		if (docData == null)
			docData = new Date();
		getDocumentService().generarDocumentPlantilla(
				docDisseny.getDefinicioProces().getEntorn().getId(),
				docDisseny.getId(),
				null,
				Long.toString(executionContext.getProcessInstance().getId()),
				docData,
				true);
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

}
