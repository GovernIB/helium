/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.Map;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per clonar una tasca jBPM
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class CloneTaskInstanceCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String actorId;
	private boolean signalling;
	private Map<String, Object> variables;
	

	public CloneTaskInstanceCommand(
			long id,
			String actorId,
			boolean signalling) {
		super();
		this.id = id;
		this.actorId = actorId;
		this.signalling = signalling;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		TaskInstance taskInstance = jbpmContext.getTaskInstance(id);
		ExecutionContext executionContext = new ExecutionContext(taskInstance.getToken());
		TaskInstance clone = taskInstance.getTaskMgmtInstance().createTaskInstance(
				taskInstance.getTask(),
				executionContext);
		clone.setSignalling(signalling);
		clone.setActorId(actorId, false);
		if (variables != null) {
			for (String codi: variables.keySet()) {
				clone.setVariableLocally(codi, variables.get(codi));
			}
		}
		return clone;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}
	public boolean isSignalling() {
		return signalling;
	}
	public void setSignalling(boolean signalling) {
		this.signalling = signalling;
	}
	public Map<String, Object> getVariables() {
		return variables;
	}
	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

	//methods for fluent programming
	public CloneTaskInstanceCommand id(long id) {
		setId(id);
	    return this;
	}

}
