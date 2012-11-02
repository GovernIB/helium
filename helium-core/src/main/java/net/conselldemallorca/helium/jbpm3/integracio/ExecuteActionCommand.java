/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.List;

import net.conselldemallorca.helium.jbpm3.handlers.AccioExternaRetrocedirHandler;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per executar una acció global a dins una instància de procés jBPM
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExecuteActionCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String actionName;
	private boolean isTaskInstance = false;
	private boolean goBack = false;
	private List<String> params;

	public ExecuteActionCommand(
			long id,
			String actionName) {
		super();
		this.id = id;
		this.actionName = actionName;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		Action action = null;
		TaskInstance ti = null;
		ProcessInstance pi = null;
		if (isTaskInstance) {
			ti = jbpmContext.getTaskInstance(id);
			action = ti.getTaskMgmtInstance().getProcessInstance().getProcessDefinition().getAction(actionName);
		} else {
			pi = jbpmContext.getProcessInstance(id);
			action = pi.getProcessDefinition().getAction(actionName);
		}
		if (!goBack) {
			if (isTaskInstance) {
				ExecutionContext ec = new ExecutionContext(ti.getToken());
				ec.setTaskInstance(ti);
				ti.getTask().executeAction(action, ec);
			} else {
				pi.getProcessDefinition().executeAction(
						action,
						new ExecutionContext(pi.getRootToken()));
			}
		} else {
			//System.out.println(">>> Executant acció (codi=" + actionName + ", p-t-id=" + id + "): " + action);
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
				}
			} finally {
				Thread.currentThread().setContextClassLoader(surroundingClassLoader);
			}
		}
	}

}
