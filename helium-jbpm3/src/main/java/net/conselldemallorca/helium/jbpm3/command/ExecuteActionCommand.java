/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.file.def.FileDefinition;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

/*import net.conselldemallorca.helium.jbpm3.api.HeliumActionHandler;
import net.conselldemallorca.helium.jbpm3.api.HeliumApi;
import net.conselldemallorca.helium.jbpm3.api.HeliumApiImpl;*/
import net.conselldemallorca.helium.jbpm3.handlers.AccioExternaRetrocedirHandler;

/**
 * Command per executar una acció global a dins una instància de procés jBPM
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExecuteActionCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String actionName;
	private boolean isTaskInstance = false;
	private boolean goBack = false;
	private List<String> params;
	/** Id de la definició de procés pare en cas d'herència per recuperar handlers en cas d'herència.*/
	private String processDefinitionPareId;

	public ExecuteActionCommand(
			long id,
			String actionName,
			String processDefinitionPareId) {
		super();
		this.id = id;
		this.actionName = actionName;
		this.processDefinitionPareId = processDefinitionPareId;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		Action action = null;
		TaskInstance ti = null;
		ProcessInstance pi = null;
		ProcessDefinition pd  = null;
		if (isTaskInstance) {
			ti = jbpmContext.getTaskInstance(id);
			pd = ti.getTaskMgmtInstance().getProcessInstance().getProcessDefinition();
			action = pd.getAction(actionName);
		} else {
			pi = jbpmContext.getProcessInstance(id);
			pd = pi.getProcessDefinition();
			action = pd.getAction(actionName);
		}
		// Herència d'accions i handlers
		if (action == null && processDefinitionPareId != null) {
			Session session = jbpmContext.getSession();
			// Cerca l'action en el processDefinition del pare
			Query query = session.createQuery(
					"from org.jbpm.graph.def.Action action " +
					"where action.processDefinition.id = :processDefinitionId ");
			query.setParameter("processDefinitionId", Long.valueOf(processDefinitionPareId));
			if (!query.list().isEmpty()) {
				action = (Action) query.list().get(0);
				// Cerca el process definition pare
				ProcessDefinition pdp = null;
				query = session.createQuery(
						"from org.jbpm.graph.def.ProcessDefinition processDefinition " +
						"where processDefinition.id = :processDefinitionId ");
				query.setParameter("processDefinitionId", Long.valueOf(processDefinitionPareId));
				if (!query.list().isEmpty()) {
					pdp = (ProcessDefinition) query.list().get(0);
					// Carrega tots els handlers
					pdp = retrieveProcessDefinition(pdp);
					if (pdp != null) {
						FileDefinition fd = pdp.getFileDefinition();
						if (fd != null) {
							// Carrega els handlers en el process definition
							Set<String> resources = fd.getBytesMap().keySet();
							for (String resource : resources)
								if (resource.endsWith(".class"))
									// Els afegeix al processDefinition
									pd.getFileDefinition().addFile(resource, pdp.getFileDefinition().getBytes(resource));
						}
					}
				}
			}
		}
		if (action == null)
			throw new JbpmException("No es troba l'acció a executar: { id=" + id + ", actionName=" + actionName + ", isTaskInstance=" + isTaskInstance + ", processDefinitionPareId=" + processDefinitionPareId);
		if (!goBack) {
			if (isTaskInstance) {
				ExecutionContext ec = new ExecutionContext(ti.getToken());
				ec.setTaskInstance(ti);
				ti.getTask().executeAction(action, ec);
			} else {
				pd.executeAction(
						action,
						new ExecutionContext(pi.getRootToken()));
			}
		} else {
			executeGoBack(action, new ExecutionContext(pi.getRootToken()));
		}
		return null;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public boolean isTaskInstance() {
		return isTaskInstance;
	}
	public void setTaskInstance(boolean isTaskInstance) {
		this.isTaskInstance = isTaskInstance;
	}
	public boolean isGoBack() {
		return goBack;
	}
	public void setGoBack(boolean goBack) {
		this.goBack = goBack;
	}
	public List<String> getParams() {
		return params;
	}
	public void setParams(List<String> params) {
		this.params = params;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id + ", actionName=" + actionName;
	}

	public void executeGoBack(Action action, ExecutionContext context) throws Exception {
		if (action != null && action.getActionDelegation() != null) {
			ClassLoader surroundingClassLoader = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(JbpmConfiguration.getProcessClassLoader(context.getProcessDefinition()));
				Object actionHandler = action.getActionDelegation().getInstance();
				if (actionHandler instanceof AccioExternaRetrocedirHandler) {
					((AccioExternaRetrocedirHandler)actionHandler).retrocedir(context, params);
				}/* else if (actionHandler instanceof HeliumActionHandler) {
					HeliumApi heliumApi = new HeliumApiImpl(context);
					((HeliumActionHandler)actionHandler).retrocedir(heliumApi, params);
				}*/
			} finally {
				Thread.currentThread().setContextClassLoader(surroundingClassLoader);
			}
		}
	}

}
