package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class TaskInfo {

	private long id;
	private String name;
	private String description;
	private ProcessDefinition processDefinition;
	//private Map events = null;
	//private List exceptionHandlers = null;
	private boolean isBlocking;
	private boolean isSignalling;
	private String condition;
	private String dueDate;
	private int priority;
	private TaskNode taskNode;
	private StartState startState;
	//private TaskMgmtDefinition taskMgmtDefinition;
	private Swimlane swimlane;
	private String actorIdExpression;
	private String pooledActorsExpression;
	//private Delegation assignmentDelegation;
	//private TaskController taskController;
	
	public TaskInfo(
			long id, 
			String name, 
			String description, 
			ProcessDefinition processDefinition, 
			boolean isBlocking,
			boolean isSignalling, 
			String condition, 
			String dueDate, 
			int priority, 
			TaskNode taskNode,
			StartState startState, 
			//TaskMgmtDefinition taskMgmtDefinition, 
			Swimlane swimlane, 
			String actorIdExpression,
			String pooledActorsExpression) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.processDefinition = processDefinition;
		this.isBlocking = isBlocking;
		this.isSignalling = isSignalling;
		this.condition = condition;
		this.dueDate = dueDate;
		this.priority = priority;
		this.taskNode = taskNode;
		this.startState = startState;
		//this.taskMgmtDefinition = taskMgmtDefinition;
		this.swimlane = swimlane;
		this.actorIdExpression = actorIdExpression;
		this.pooledActorsExpression = pooledActorsExpression;
		//this.assignmentDelegation = assignmentDelegation;
		//this.taskController = taskController;
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

	public boolean isBlocking() {
		return isBlocking;
	}

	public boolean isSignalling() {
		return isSignalling;
	}

	public String getCondition() {
		return condition;
	}

	public String getDueDate() {
		return dueDate;
	}

	public int getPriority() {
		return priority;
	}

	public TaskNodeInfo getTaskNode() {
		return ConversioTipusInfoHelper.toTaskNodeInfo(taskNode);
	}

	public NodeInfo getStartState() {
		return ConversioTipusInfoHelper.toNodeInfo(startState);
	}

//	public TaskMgmtDefinition getTaskMgmtDefinition() {
//		return taskMgmtDefinition;
//	}

	public SwimlaneInfo getSwimlane() {
		return ConversioTipusInfoHelper.toSwimlaneInfo(swimlane);
	}

	public String getActorIdExpression() {
		return actorIdExpression;
	}

	public String getPooledActorsExpression() {
		return pooledActorsExpression;
	}

//	public Delegation getAssignmentDelegation() {
//		return assignmentDelegation;
//	}

//	public TaskControllerInfo getTaskController() {
//		return ConversioTipusInfoHelper.toTaskControllerInfo(taskController);
//	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
