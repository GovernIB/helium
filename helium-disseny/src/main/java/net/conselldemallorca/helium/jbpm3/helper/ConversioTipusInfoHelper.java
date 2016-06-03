/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.helper;

import java.util.HashSet;

import org.jbpm.context.exe.VariableInstance;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.job.Timer;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.ActionInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.EventInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.NodeInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.PooledActorInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ProcessDefinitionInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ProcessInstanceInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.SwimlaneInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.SwimlaneInstanceInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TaskInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TaskInstanceInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TaskMgmtInstanceInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TaskNodeInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TimerInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TokenInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TransitionInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.VariableInstanceInfo;

/**
 * Helper per a convertir entre diferents formats de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConversioTipusInfoHelper {

	public static TokenInfo toTokenInfo(Token token) {
		if (token == null)
			return null;
		
		return new TokenInfo(
				token.getId(),
				token.getName(),
				token.getStart(),
				token.getEnd(),
				token.getNode(),
				token.getNodeEnter(),
				token.getProcessInstance(),
				token.getParent(),
				token.getChildren(),
				token.getSubProcessInstance(),
				token.isAbleToReactivateParent(),
				token.isTerminatedImplicitly(),
				token.isSuspended(),
				token.getLockOwner());
	}
	
	public static NodeInfo toNodeInfo(Node node) {
		if (node == null)
			return null;
		
		return new NodeInfo(
				node.getId(),
				node.getName(),
				node.getDescription(),
				node.getProcessDefinition(),
				node.getLeavingTransitions(),
				node.getArrivingTransitions(),
				node.getAction(),
				node.isAsync(),
				node.isAsyncExclusive());
	}
	
	@SuppressWarnings("unchecked")
	public static ProcessDefinitionInfo toProcessDefinitionInfo(ProcessDefinition processDefinition) {
		if (processDefinition == null)
			return null;
		
		return new ProcessDefinitionInfo(
				processDefinition.getId(), 
				processDefinition.getName(), 
				processDefinition.getDescription(), 
				processDefinition.getVersion(),
				processDefinition.isTerminationImplicit(),
				processDefinition.getStartState(),
				processDefinition.getNodes(),
				processDefinition.getActions());
	}

	public static ProcessInstanceInfo toProcessInstanceInfo(ProcessInstance processInstance) {
		if (processInstance == null)
			return null;
		
		return new ProcessInstanceInfo(
				processInstance.getId(),
				processInstance.getVersion(),
				processInstance.getKey(),
				processInstance.getStart(),
				processInstance.getEnd(),
				processInstance.getProcessDefinition(),
				processInstance.getRootToken(),
				processInstance.getSuperProcessToken(),
				processInstance.isSuspended());
	}

	public static ActionInfo toActionInfo(Action action) {
		if (action == null)
			return null;
		
		return new ActionInfo(
				action.getId(),
				action.getName(),
				action.isPropagationAllowed(),
				action.isAsync(),
				action.isAsyncExclusive(),
				action.getActionExpression(),
				action.getProcessDefinition());
	}

	@SuppressWarnings("unchecked")
	public static EventInfo toEventInfo(Event event) {
		if (event == null)
			return null;
		
		return new EventInfo(
				event.getId(),
				event.getEventType(),
				event.getActions());
	}

	public static TransitionInfo toTransitionInfo(Transition transition) {
		if (transition == null)
			return null;
		
		return new TransitionInfo(
				transition.getId(), 
				transition.getName(), 
				transition.getDescription(), 
				transition.getFrom(), 
				transition.getTo(), 
				transition.getCondition());
	}

	public static TaskInfo toTaskInfo(Task task) {
		if (task == null)
			return null;
		
		return new TaskInfo(
				task.getId(), 
				task.getName(), 
				task.getDescription(), 
				task.getProcessDefinition(), 
				task.isBlocking(), 
				task.isSignalling(), 
				task.getCondition(), 
				task.getDueDate(), 
				task.getPriority(), 
				task.getTaskNode(), 
				task.getStartState(), 
				task.getSwimlane(), 
				task.getActorIdExpression(), 
				task.getPooledActorsExpression());
	}

	@SuppressWarnings("unchecked")
	public static TaskInstanceInfo toTaskInstanceInfo(TaskInstance taskInstance) {
		if (taskInstance == null)
			return null;
		
		return new TaskInstanceInfo(
				taskInstance.getId(), 
				taskInstance.getName(), 
				taskInstance.getDescription(), 
				taskInstance.getActorId(), 
				taskInstance.getCreate(), 
				taskInstance.getStart(), 
				taskInstance.getEnd(), 
				taskInstance.getDueDate(), 
				taskInstance.getPriority(), 
				taskInstance.isCancelled(), 
				taskInstance.isSuspended(), 
				taskInstance.isOpen(), 
				taskInstance.isSignalling(), 
				taskInstance.isBlocking(), 
				taskInstance.getTask(), 
				taskInstance.getToken(), 
				taskInstance.getSwimlaneInstance(), 
				taskInstance.getTaskMgmtInstance(), 
				taskInstance.getProcessInstance(), 
				taskInstance.getPooledActors(), 
				taskInstance.getVariableInstances());
	}

	public static TimerInfo toTimerInfo(Timer timer) {
		if (timer == null)
			return null;
		
		return new TimerInfo(
				timer.getId(), 
				timer.getVersion(), 
				timer.getDueDate(), 
				timer.getProcessInstance(), 
				timer.getToken(), 
				timer.getTaskInstance(), 
				timer.isSuspended(), 
				timer.isExclusive(), 
				timer.getLockOwner(), 
				timer.getLockTime(), 
				timer.getException(), 
				timer.getRetries(), 
				timer.getConfiguration(), 
				timer.getName(), 
				timer.getRepeat(), 
				timer.getTransitionName(), 
				timer.getAction());
	}

	@SuppressWarnings("unchecked")
	public static TaskNodeInfo toTaskNodeInfo(TaskNode taskNode) {
		if (taskNode == null)
			return null;
		
		return new TaskNodeInfo(
				taskNode.getId(),
				taskNode.getName(),
				taskNode.getDescription(),
				taskNode.getProcessDefinition(),
				taskNode.getLeavingTransitions(),
				taskNode.getArrivingTransitions(),
				taskNode.getAction(),
				taskNode.isAsync(),
				taskNode.isAsyncExclusive(),
				taskNode.getTasks(),
				taskNode.getSignal(),
				taskNode.getCreateTasks(),
				taskNode.isEndTasks());
	}

	@SuppressWarnings("unchecked")
	public static SwimlaneInfo toSwimlaneInfo(Swimlane swimlane) {
		if (swimlane == null)
			return null;
		
		return new SwimlaneInfo(
				swimlane.getId(), 
				swimlane.getName(), 
				swimlane.getActorIdExpression(), 
				swimlane.getPooledActorsExpression(), 
				swimlane.getTasks());
	}

	public static SwimlaneInstanceInfo toSwimlaneInstanceInfo(SwimlaneInstance swimlaneInstance) {
		if (swimlaneInstance == null)
			return null;
		
		return new SwimlaneInstanceInfo(
				swimlaneInstance.getId(), 
				swimlaneInstance.getName(), 
				swimlaneInstance.getActorId(), 
				swimlaneInstance.getPooledActors(), 
				swimlaneInstance.getSwimlane(), 
				swimlaneInstance.getTaskMgmtInstance());
	}

	public static TaskMgmtInstanceInfo toTaskMgmtInstanceInfo(TaskMgmtInstance taskMgmtInstance) {
		if (taskMgmtInstance == null)
			return null;
		
		return new TaskMgmtInstanceInfo(
				taskMgmtInstance.getId(), 
				taskMgmtInstance.getProcessInstance(), 
				taskMgmtInstance.getSwimlaneInstances(), 
				taskMgmtInstance.getTaskInstances() == null? null : new HashSet<TaskInstance>(taskMgmtInstance.getTaskInstances()));
	}

	@SuppressWarnings("unchecked")
	public static PooledActorInfo toPooledActorInfo(PooledActor pa) {
		if (pa == null)
			return null;
		
		return new PooledActorInfo(
				pa.getId(),
				pa.getActorId(),
				pa.getTaskInstances(), 
				pa.getSwimlaneInstance());
	}

	public static VariableInstanceInfo toVariableInstanceInfo(VariableInstance variableInstance) {
		if (variableInstance == null)
			return null;
		
		return new VariableInstanceInfo(
				variableInstance.getName(), 
				variableInstance.getToken(), 
				variableInstance.getProcessInstance(), 
				variableInstance.getValue());
	}
}
