package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class PooledActorInfo {

	private long id;
//	private int version;
	private String actorId;
	private Set<TaskInstance> taskInstances;
	private SwimlaneInstance swimlaneInstance;
	
	public PooledActorInfo(
			long id, 
//			int version, 
			String actorId, 
			Set<TaskInstance> taskInstances,
			SwimlaneInstance swimlaneInstance) {
		super();
		this.id = id;
//		this.version = version;
		this.actorId = actorId;
		this.taskInstances = taskInstances;
		this.swimlaneInstance = swimlaneInstance;
	}

	public long getId() {
		return id;
	}

//	public int getVersion() {
//		return version;
//	}

	public String getActorId() {
		return actorId;
	}

	public Set<TaskInstanceInfo> getTaskInstances() {
		Set<TaskInstanceInfo> taskInstancesInfo = new HashSet<TaskInstanceInfo>();
		for (TaskInstance taskInstance: taskInstances) {
			taskInstancesInfo.add(ConversioTipusInfoHelper.toTaskInstanceInfo(taskInstance));
		}
		return taskInstancesInfo;
	}

	public SwimlaneInstanceInfo getSwimlaneInstance() {
		return ConversioTipusInfoHelper.toSwimlaneInstanceInfo(swimlaneInstance);
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
