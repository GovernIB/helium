/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.TaskInstance;


/**
 * Una instància de tasca multiversió
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class JbpmTask {

	private TaskInstance task;



	public JbpmTask(TaskInstance task) {
		this.task = task;
	}

	public TaskInstance getTask() {
		return task;
	}
	public void setTask(TaskInstance task) {
		this.task = task;
	}

	public String getId() {
		return new Long(task.getId()).toString();
	}

	public String getProcessInstanceId() {
		return new Long(task.getProcessInstance().getId()).toString();
	}

	public String getProcessDefinitionId() {
		return new Long(task.getProcessInstance().getProcessDefinition().getId()).toString();
	}

	public String getName() {
		return task.getName();
	}
	public String getDescription() {
		return task.getDescription();
	}
	public String getAssignee() {
		return task.getActorId();
	}
	public Set<String> getPooledActors() {
		Set<String> resultat = new HashSet<String>();
		for (PooledActor pa: task.getPooledActors()) {
			resultat.add(pa.getActorId());
		}
		return resultat;
	}
	public Date getCreateTime() {
		return task.getCreate();
	}
	public Date getStartTime() {
		return task.getStart();
	}
	public Date getEndTime() {
		return task.getEnd();
	}
	public Date getDueDate() {
		return task.getDueDate();
	}
	public int getPriority() {
		return 3 - task.getPriority();
	}
	public boolean isOpen() {
		return task.isOpen();
	}
	public boolean isCompleted() {
		return task.getEnd() != null;
	}
	public boolean isSuspended() {
		return task.isSuspended();
	}
	public boolean isCancelled() {
		return task.isCancelled();
	}

}
