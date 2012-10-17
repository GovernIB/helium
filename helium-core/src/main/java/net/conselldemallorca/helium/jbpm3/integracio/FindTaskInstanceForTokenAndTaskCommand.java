/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.Collection;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per a cercar instancies de tasca actives que
 * pertanyen a la mateixa tasca.
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
		Collection<TaskInstance> unfinishedTasks = jbpmContext.getProcessInstance(token.getProcessInstance().getId()).getTaskMgmtInstance().getUnfinishedTasks(token);
		for (TaskInstance task: unfinishedTasks) {
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
