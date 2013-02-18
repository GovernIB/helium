/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;

import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.def.TaskControllerHandler;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * ControllerHandler per defecte
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DefaultControllerHandler implements TaskControllerHandler {

	public void initializeTaskVariables(
			TaskInstance taskInstance,
			ContextInstance contextInstance,
			Token token) {
		for (CampTascaDto camp: getCampsPerTaskInstance(taskInstance)) {
			if (camp.isReadFrom()) {
				String codi = camp.getCamp().getCodi();
				taskInstance.setVariableLocally(
						codi,
						contextInstance.getVariable(codi));
			}
		}
		for (DocumentTascaDto document: getDocumentsPerTaskInstance(taskInstance)) {
			String codi = DocumentService.PREFIX_VAR_DOCUMENT + document.getDocument().getCodi();
			if (!document.isReadOnly()) {
				Object valor = contextInstance.getVariable(DocumentService.PREFIX_VAR_DOCUMENT + document.getDocument().getCodi());
				if (valor != null)
					taskInstance.setVariableLocally(
							codi,
							contextInstance.getVariable(codi));
			}
		}
	}
	public void submitTaskVariables(
			TaskInstance taskInstance,
			ContextInstance contextInstance,
			Token token) {
		for (CampTascaDto camp: getCampsPerTaskInstance(taskInstance)) {
			if (camp.isWriteTo()) {
				String codi = camp.getCamp().getCodi();
				Object valor = taskInstance.getVariableLocally(codi);
				if (valor != null)
					contextInstance.setVariable(
							codi,
							valor);
			}
		}
		for (DocumentTascaDto document: getDocumentsPerTaskInstance(taskInstance)) {
			String codi = DocumentService.PREFIX_VAR_DOCUMENT + document.getDocument().getCodi();
			Long docId = (Long)taskInstance.getVariableLocally(codi);
			if (docId != null && !document.isReadOnly()) {
				contextInstance.setVariable(
						codi,
						docId);
			}
		}
	}



	private List<CampTascaDto> getCampsPerTaskInstance(TaskInstance taskInstance) {
		return Jbpm3HeliumBridge.getInstance().getTascaService().findCampsPerTaskInstance(
				taskInstance.getId());
	}
	private List<DocumentTascaDto> getDocumentsPerTaskInstance(TaskInstance taskInstance) {
		return Jbpm3HeliumBridge.getInstance().getTascaService().findDocumentsPerTaskInstance(
				taskInstance.getId());
	}

	private static final long serialVersionUID = -3360653717647288657L;

}
