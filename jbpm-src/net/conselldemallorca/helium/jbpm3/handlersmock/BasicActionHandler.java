/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlersmock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.FilaResultat;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler que pot servir com a base per als handlers que s'hagin
 * d'implementar a dins les definicions de procés.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public abstract class BasicActionHandler implements ActionHandler {

	public abstract void execute(ExecutionContext executionContext) throws Exception;



	public void errorValidacio(String error) {
	}

	public List<FilaResultat> consultaDomini(
			ExecutionContext executionContext,
			String codiDomini,
			String id,
			Map<String, Object> parametres) {
		return new ArrayList<FilaResultat>();
	}

	public DocumentInfo getDocument(
			ExecutionContext executionContext,
			String varDocument) {
		return new DocumentInfo();
	}

	public ExpedientInfo getExpedient(ExecutionContext executionContext) {
		return new ExpedientInfo();
	}

	/**
	 * Envia un email amb possibilitat d'adjuntar documents de l'expedient.
	 * 
	 * @param recipients
	 * @param ccRecipients
	 * @param bccRecipients
	 * @param subject
	 * @param text
	 * @param attachments
	 */
	public void enviarEmail(
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text,
			List<Long> attachments) {
	}

	static final long serialVersionUID = 1L;

}
