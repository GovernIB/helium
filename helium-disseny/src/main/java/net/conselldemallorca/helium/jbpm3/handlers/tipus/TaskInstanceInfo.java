package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.context.exe.VariableInstance;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class TaskInstanceInfo {

	private long id;
	//private int version;
	private String name;
	private String description;
	private String actorId;
	private Date create;
	private Date start;
	private Date end;
	private Date dueDate;
	private int priority;
	private boolean isCancelled;
	private boolean isSuspended;
	private boolean isOpen;
	private boolean isSignalling;
	private boolean isBlocking;
	private Task task;
	private Token token;
	private SwimlaneInstance swimlaneInstance;
	private TaskMgmtInstance taskMgmtInstance;
	private ProcessInstance processInstance;
	private Set<PooledActor> pooledActors;
//	private List comments;
	private Map<String, VariableInstance> variableInstances;
	
	public TaskInstanceInfo(
			long id, 
			//int version, 
			String name, 
			String description, 
			String actorId, 
			Date create, 
			Date start,
			Date end, 
			Date dueDate, 
			int priority, 
			boolean isCancelled, 
			boolean isSuspended, 
			boolean isOpen,
			boolean isSignalling, 
			boolean isBlocking, 
			Task task, 
			Token token, 
			SwimlaneInstance swimlaneInstance,
			TaskMgmtInstance taskMgmtInstance, 
			ProcessInstance processInstance, 
			Set<PooledActor> pooledActors,
			Map<String, VariableInstance> variableInstances) {
		super();
		this.id = id;
		//this.version = version;
		this.name = name;
		this.description = description;
		this.actorId = actorId;
		this.create = create;
		this.start = start;
		this.end = end;
		this.dueDate = dueDate;
		this.priority = priority;
		this.isCancelled = isCancelled;
		this.isSuspended = isSuspended;
		this.isOpen = isOpen;
		this.isSignalling = isSignalling;
		this.isBlocking = isBlocking;
		this.task = task;
		this.token = token;
		this.swimlaneInstance = swimlaneInstance;
		this.taskMgmtInstance = taskMgmtInstance;
		this.processInstance = processInstance;
		this.pooledActors = pooledActors;
		this.variableInstances = variableInstances;
	}

	public long getId() {
		return id;
	}

//	public int getVersion() {
//		return version;
//	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getActorId() {
		return actorId;
	}

	public Date getCreate() {
		return create;
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public int getPriority() {
		return priority;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	public boolean isSuspended() {
		return isSuspended;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public boolean isSignalling() {
		return isSignalling;
	}

	public boolean isBlocking() {
		return isBlocking;
	}

	public TaskInfo getTask() {
		return ConversioTipusInfoHelper.toTaskInfo(task);
	}

	public TokenInfo getToken() {
		return ConversioTipusInfoHelper.toTokenInfo(token);
	}

	public SwimlaneInstanceInfo getSwimlaneInstance() {
		return ConversioTipusInfoHelper.toSwimlaneInstanceInfo(swimlaneInstance);
	}

	public TaskMgmtInstanceInfo getTaskMgmtInstance() {
		return ConversioTipusInfoHelper.toTaskMgmtInstanceInfo(taskMgmtInstance);
	}

	public ProcessInstanceInfo getProcessInstance() {
		return ConversioTipusInfoHelper.toProcessInstanceInfo(processInstance);
	}

	public Set<PooledActorInfo> getPooledActors() {
		Set<PooledActorInfo> pooledActorsInfo = new HashSet<PooledActorInfo>();
		for (PooledActor pa: pooledActors) {
			pooledActorsInfo.add(ConversioTipusInfoHelper.toPooledActorInfo(pa));
		}
		return pooledActorsInfo;
	}


	public Map<String, VariableInstanceInfo> getVariableInstances() {
		Map<String, VariableInstanceInfo> variableInstancesInfo = new HashMap<String, VariableInstanceInfo>();
		for (Entry<String, VariableInstance> entry: variableInstances.entrySet()) {
			variableInstancesInfo.put(entry.getKey(), ConversioTipusInfoHelper.toVariableInstanceInfo(entry.getValue()));
		}
		return variableInstancesInfo;
	}


	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
