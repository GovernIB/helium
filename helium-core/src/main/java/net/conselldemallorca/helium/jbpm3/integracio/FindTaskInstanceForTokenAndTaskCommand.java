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
	private long taskId;

	public FindTaskInstanceForTokenAndTaskCommand(long tokenId, long taskId){
		super();
		this.tokenId = tokenId;
		this.taskId = taskId;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		Token token = jbpmContext.getToken(tokenId);
		Collection<TaskInstance> unfinishedTasks = jbpmContext.getProcessInstance(token.getProcessInstance().getId()).getTaskMgmtInstance().getUnfinishedTasks(token);
		for (TaskInstance task: unfinishedTasks) {
			if (task.getTask().getId() == taskId)
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
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "tokenId=" + tokenId + ", taskId=" + taskId;
	}

}
