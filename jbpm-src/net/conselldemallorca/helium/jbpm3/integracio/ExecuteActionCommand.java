/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per executar una acció global a dins una instància de procés jBPM
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ExecuteActionCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String actionName;
	private boolean isTaskInstance = false;

	public ExecuteActionCommand(
			long id,
			String actionName) {
		super();
		this.id = id;
		this.actionName = actionName;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		if (!isTaskInstance) {
			ProcessInstance pi = jbpmContext.getProcessInstance(id);
			Action action = pi.getProcessDefinition().getAction(actionName);
			action.execute(new ExecutionContext(pi.getRootToken()));
		} else {
			TaskInstance ti = jbpmContext.getTaskInstance(id);
			ExecutionContext ec = new ExecutionContext(ti.getToken());
			ec.setTaskInstance(ti);
			Action action = ti.getProcessInstance().getProcessDefinition().getAction(actionName);
			action.execute(ec);
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

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

	//methods for fluent programming
	public ExecuteActionCommand id(long id) {
		setId(id);
	    return this;
	}

}
