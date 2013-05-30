/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.List;

import net.conselldemallorca.helium.core.model.dao.DaoProxy;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;

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
		for (CampTasca camp: getCampsPerTaskInstance(taskInstance)) {
			if (camp.isReadFrom()) {
				String codi = camp.getCamp().getCodi();
				taskInstance.setVariableLocally(
						codi,
						contextInstance.getVariable(codi));
			}
		}
		for (DocumentTasca document: getDocumentsPerTaskInstance(taskInstance)) {
			String codi = DocumentHelper.PREFIX_VAR_DOCUMENT + document.getDocument().getCodi();
			if (!document.isReadOnly()) {
				Object valor = contextInstance.getVariable(DocumentHelper.PREFIX_VAR_DOCUMENT + document.getDocument().getCodi());
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
		for (CampTasca camp: getCampsPerTaskInstance(taskInstance)) {
			if (camp.isWriteTo() && !camp.getCamp().getTipus().equals(TipusCamp.ACCIO)) {
				String codi = camp.getCamp().getCodi();
				Object valor = taskInstance.getVariableLocally(codi);
				contextInstance.setVariable(
						codi,
						valor);
			}
		}
		for (DocumentTasca document: getDocumentsPerTaskInstance(taskInstance)) {
			String codi = DocumentHelper.PREFIX_VAR_DOCUMENT + document.getDocument().getCodi();
			Long docId = (Long)taskInstance.getVariableLocally(codi);
			if (docId != null && !document.isReadOnly()) {
				contextInstance.setVariable(
						codi,
						docId);
			}
		}
	}



	private List<CampTasca> getCampsPerTaskInstance(TaskInstance taskInstance) {
		long processDefinitionId = taskInstance.getProcessInstance().getProcessDefinition().getId();
		Tasca tasca = DaoProxy.getInstance().getTascaDao().findAmbActivityNameIProcessDefinitionId(
				taskInstance.getTask().getName(),
				new Long(processDefinitionId).toString());
		return tasca.getCamps();
	}
	private List<DocumentTasca> getDocumentsPerTaskInstance(TaskInstance taskInstance) {
		long processDefinitionId = taskInstance.getProcessInstance().getProcessDefinition().getId();
		Tasca tasca = DaoProxy.getInstance().getTascaDao().findAmbActivityNameIProcessDefinitionId(
				taskInstance.getTask().getName(),
				new Long(processDefinitionId).toString());
		return tasca.getDocuments();
	}

	private static final long serialVersionUID = -3360653717647288657L;

}
