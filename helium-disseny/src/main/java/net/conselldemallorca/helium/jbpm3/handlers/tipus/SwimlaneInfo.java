package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.taskmgmt.def.Task;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class SwimlaneInfo {

	private long id = 0;
	private String name = null;  
	private String actorIdExpression = null;
	private String pooledActorsExpression = null;
//	private Delegation assignmentDelegation = null;
//	private TaskMgmtDefinition taskMgmtDefinition = null;
	private Set<Task> tasks = null;
	
	public SwimlaneInfo(
			long id, 
			String name, 
			String actorIdExpression, 
			String pooledActorsExpression,
			Set<Task> tasks) {
		super();
		this.id = id;
		this.name = name;
		this.actorIdExpression = actorIdExpression;
		this.pooledActorsExpression = pooledActorsExpression;
		this.tasks = tasks;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getActorIdExpression() {
		return actorIdExpression;
	}

	public String getPooledActorsExpression() {
		return pooledActorsExpression;
	}

	public Set<TaskInfo> getTasks() {
		Set<TaskInfo> tasksInfo = new HashSet<TaskInfo>();
		for (Task task: tasks) {
			tasksInfo.add(ConversioTipusInfoHelper.toTaskInfo(task));
		}
		return tasksInfo;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
