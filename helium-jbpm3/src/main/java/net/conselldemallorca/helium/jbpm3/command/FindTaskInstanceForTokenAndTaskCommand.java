/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.Collection;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per a cercar una tasca per token i nom.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FindTaskInstanceForTokenAndTaskCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long tokenId;
	private String taskName;

	public FindTaskInstanceForTokenAndTaskCommand(long tokenId, String taskName){
		super();
		this.tokenId = tokenId;
		this.taskName = taskName;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		Token token = jbpmContext.getToken(tokenId);
		Collection<TaskInstance> tasks = jbpmContext.getProcessInstance(token.getProcessInstance().getId()).getTaskMgmtInstance().getTaskInstances();
		for (TaskInstance task: tasks) {
			if (task.getTask().getName().equals(taskName))
				return task;
		}
		return null;
	}

	public long getTokenId() {
		return tokenId;
	}
	public void setTokenId(long tokenId) {
		this.tokenId = tokenId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "tokenId=" + tokenId + ", taskName=" + taskName;
	}

}
