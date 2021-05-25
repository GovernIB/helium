/**
 * 
 */
package es.caib.helium.jbpm3.integracio;

import java.util.List;

import es.caib.helium.api.dto.CampTascaDto;
import es.caib.helium.api.dto.CampTipusDto;

import es.caib.helium.api.dto.DocumentTascaDto;
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
			String codi = Jbpm3HeliumBridge.getInstanceService().getCodiVariablePerDocumentCodi(document.getDocument().getCodi());
			if (!document.isReadOnly()) {
				Object valor = contextInstance.getVariable(codi);
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
			if (camp.isWriteTo() && !camp.getCamp().getTipus().equals(CampTipusDto.ACCIO)) {
				String codi = camp.getCamp().getCodi();
				Object valor = taskInstance.getVariableLocally(codi);
				contextInstance.setVariable(
						codi,
						valor);
			}
		}
		for (DocumentTascaDto document: getDocumentsPerTaskInstance(taskInstance)) {
			String codi = Jbpm3HeliumBridge.getInstanceService().getCodiVariablePerDocumentCodi(document.getDocument().getCodi());
			Long docId = (Long)taskInstance.getVariableLocally(codi);
			if (docId != null && !document.isReadOnly()) {
				contextInstance.setVariable(
						codi,
						docId);
			}
		}
	}



	private List<CampTascaDto> getCampsPerTaskInstance(TaskInstance taskInstance) {
		return Jbpm3HeliumBridge.getInstanceService().findCampsPerTaskInstance(
				new Long(taskInstance.getProcessInstance().getId()).toString(),
				new Long(taskInstance.getProcessInstance().getProcessDefinition().getId()).toString(),
				taskInstance.getName());
	}
	private List<DocumentTascaDto> getDocumentsPerTaskInstance(TaskInstance taskInstance) {
		return Jbpm3HeliumBridge.getInstanceService().findDocumentsPerTaskInstance(
				new Long(taskInstance.getProcessInstance().getId()).toString(),
				new Long(taskInstance.getProcessInstance().getProcessDefinition().getId()).toString(),
				taskInstance.getName());
	}

	private static final long serialVersionUID = -3360653717647288657L;

}
