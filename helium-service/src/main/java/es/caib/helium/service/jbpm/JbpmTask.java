package es.caib.helium.service.jbpm;

import java.util.Date;
import java.util.Set;

public class JbpmTask {

	public JbpmTask(TaskInstance taskInstance) {
	}

	public TaskInstance getTask() {
		return null;
	}
	public void setTask(TaskInstance taskInstance) {
	}

	public String getId() {
		return null;
	}

	public String getProcessInstanceId() {
		return null;
	}

	public String getProcessDefinitionId() {
		return null;
	}

	public String getTaskName() {
		return null;
	}
	public String getDescription() {
		return null;
	}
	public String getAssignee() {
		return null;
	}
	public Set<String> getPooledActors() {
		return null;
	}
	public Date getCreateTime() {
		return null;
	}
	public Date getStartTime() {
		return null;
	}
	public Date getEndTime() {
		return null;
	}
	public Date getDueDate() {
		return null;
	}
	public int getPriority() {
		return 0;
	}
	public boolean isOpen() {
		return false;
	}
	public boolean isCompleted() {
		return false;
	}
	public boolean isSuspended() {
		return false;
	}
	public boolean isCancelled() {
		return false;
	}
	public String getPooledActorsExpression() {
		return null;
	}
	
	public boolean isAgafada() {
		return false;
	}

	public void setCacheActiu() {
	}
	public void setCacheInactiu() {
	}
	public boolean isCacheActiu() {
		return false;
	}

	public String getDescriptionWithFields() {
		return null;
	}
	public String getFieldFromDescription(String name) {
		return null;
	}
	public void setFieldFromDescription(String name, String value) {
	}
}
