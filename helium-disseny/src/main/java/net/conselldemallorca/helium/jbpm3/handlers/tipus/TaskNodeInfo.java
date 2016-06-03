package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.taskmgmt.def.Task;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class TaskNodeInfo {

	private long id = 0;
	private String name;
	private String description;
	private ProcessDefinition processDefinition;
	// private Map<String, EventInfo> events;
	// private List exceptionHandlers;
	private List<Transition> leavingTransitions;
	private Set<Transition> arrivingTransitions;
	private Action action;
	// private SuperState superState;
	private boolean isAsync;
	private boolean isAsyncExclusive;
	private Set<Task> tasks;
	private int signal;
	private boolean createTasks;
	private boolean endTasks;
	
	public TaskNodeInfo(
			long id, 
			String name, 
			String description, 
			ProcessDefinition processDefinition,
			List<Transition> leavingTransitions, 
			Set<Transition> arrivingTransitions, 
			Action action,
			boolean isAsync, 
			boolean isAsyncExclusive,
			Set<Task> tasks, 
			int signal, 
			boolean createTasks, 
			boolean endTasks) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.processDefinition = processDefinition;
		this.leavingTransitions = leavingTransitions;
		this.arrivingTransitions = arrivingTransitions;
		this.action = action;
		this.isAsync = isAsync;
		this.isAsyncExclusive = isAsyncExclusive;
		this.tasks = tasks;
		this.signal = signal;
		this.createTasks = createTasks;
		this.endTasks = endTasks;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public ProcessDefinitionInfo getProcessDefinition() {
		return ConversioTipusInfoHelper.toProcessDefinitionInfo(processDefinition);
	}

	public List<TransitionInfo> getLeavingTransitions() {
		List<TransitionInfo> leavingTransitionsInfo = new ArrayList<TransitionInfo>();
		for (Transition t: leavingTransitions) {
			leavingTransitionsInfo.add(ConversioTipusInfoHelper.toTransitionInfo(t));
		}
		return leavingTransitionsInfo;
	}

	public Set<TransitionInfo> getArrivingTransitions() {
		Set<TransitionInfo> arrivingTransitionsInfo = new HashSet<TransitionInfo>();
		for (Transition t: arrivingTransitions) {
			arrivingTransitionsInfo.add(ConversioTipusInfoHelper.toTransitionInfo(t));
		}
		return arrivingTransitionsInfo;
	}

	public ActionInfo getAction() {
		return ConversioTipusInfoHelper.toActionInfo(action);
	}

	public boolean isAsync() {
		return isAsync;
	}

	public boolean isAsyncExclusive() {
		return isAsyncExclusive;
	}

	public Set<TaskInfo> getTasks() {
		Set<TaskInfo> tasksInfo = new HashSet<TaskInfo>();
		for (Task task: tasks) {
			tasksInfo.add(ConversioTipusInfoHelper.toTaskInfo(task));
		}
		return tasksInfo;
	}

	public int getSignal() {
		return signal;
	}

	public boolean isCreateTasks() {
		return createTasks;
	}

	public boolean isEndTasks() {
		return endTasks;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
