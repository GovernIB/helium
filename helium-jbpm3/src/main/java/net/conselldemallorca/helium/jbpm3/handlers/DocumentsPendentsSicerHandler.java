package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;

/**
 * Handler per fer la integració amb portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentsPendentsSicerHandler extends AbstractHeliumActionHandler {

	private String varDocuments;
	private String documents;

	public void execute(ExecutionContext executionContext) throws Exception {
		try {
			List<Long> docs = null;
			String docsCodis = (String)getValorOVariable(executionContext, documents, varDocuments);
			if (docsCodis != null) {
				docs = new ArrayList<Long>();
				String[] codis = docsCodis.split(",");
				for (String codi: codis) {
					Long docId = (Long)executionContext.getVariable(
							Jbpm3HeliumBridge.getInstanceService().getCodiVariablePerDocumentCodi(codi.trim()));
					if (docId != null) {
						docs.add(docId);
					} else {
						throw new JbpmException("No s'ha pogut trobar el document amb el codi '" + codi.trim() + "'");
					}
				}
			} else {
				throw new JbpmException("No s'ha especificat cap codi de document a notificar al SICER");
			}
			
			Jbpm3HeliumBridge.getInstanceService().crearNotificacioSicerPendent(
					docs,
					getExpedientActual(executionContext),
					executionContext.getProcessInstance().getId());
			
		} catch (Exception e) {
			logger.error("Error DocumentsPendentsSicerHandler. ", e);
			throw new JbpmException("No s'han pogut establir els documents a notificar", e);
		}
	}

		public void setVarDocuments(String varDocuments) {
		this.varDocuments = varDocuments;
	}

	public void setDocuments(String documents) {
		this.documents = documents;
	}

		private static final Log logger = LogFactory.getLog(DocumentsPendentsSicerHandler.class);

}
