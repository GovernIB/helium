package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class TaskMgmtInstanceInfo {

	private long id = 0;
//	private int version = 0;
	private ProcessInstance processInstance = null;
//	private TaskMgmtDefinition taskMgmtDefinition = null;
	private Map<String, SwimlaneInstance> swimlaneInstances = null;
	private Set<TaskInstance> taskInstances = null;
	
	public TaskMgmtInstanceInfo(
			long id, 
//			int version, 
			ProcessInstance processInstance,
			Map<String, SwimlaneInstance> swimlaneInstances, 
			Set<TaskInstance> taskInstances) {
		super();
		this.id = id;
//		this.version = version;
		this.processInstance = processInstance;
		this.swimlaneInstances = swimlaneInstances;
		this.taskInstances = taskInstances;
	}

	public long getId() {
		return id;
	}

//	public int getVersion() {
//		return version;
//	}

	public ProcessInstanceInfo getProcessInstance() {
		return ConversioTipusInfoHelper.toProcessInstanceInfo(processInstance);
	}

	public Map<String, SwimlaneInstanceInfo> getSwimlaneInstances() {
		Map<String, SwimlaneInstanceInfo> swimlaneInstancesinfo = new HashMap<String, SwimlaneInstanceInfo>();
		for (Entry<String, SwimlaneInstance> entry: swimlaneInstances.entrySet()) {
			swimlaneInstancesinfo.put(entry.getKey(), ConversioTipusInfoHelper.toSwimlaneInstanceInfo(entry.getValue()));
		}
		return swimlaneInstancesinfo;
	}

	public Set<TaskInstanceInfo> getTaskInstances() {
		Set<TaskInstanceInfo> taskInstancesInfo = new HashSet<TaskInstanceInfo>();
		for (TaskInstance ti: taskInstances) {
			taskInstancesInfo.add(ConversioTipusInfoHelper.toTaskInstanceInfo(ti));
		}
		return taskInstancesInfo;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
