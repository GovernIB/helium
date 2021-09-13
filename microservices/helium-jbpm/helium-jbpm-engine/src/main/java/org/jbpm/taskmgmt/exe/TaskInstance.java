/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jbpm.taskmgmt.exe;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.conselldemallorca.helium.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.api.exception.NoTrobatException;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.context.exe.VariableContainer;
import org.jbpm.context.exe.VariableInstance;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Identifiable;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.Comment;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.security.SecurityHelper;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.log.TaskAssignLog;
import org.jbpm.taskmgmt.log.TaskEndLog;
import org.jbpm.util.Clock;
import org.jbpm.util.EqualsUtil;

/**
 * is one task instance that can be assigned to an actor (read: put in someones
 * task list) and that can trigger the coninuation of execution of the token
 * upon completion.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class TaskInstance extends VariableContainer implements Identifiable,
		Assignable {

	private static final long serialVersionUID = 1L;

	long id = 0;
	int version = 0;
	protected String name = null;
	protected String description = null;
	protected String actorId = null;
	protected Date create = null;
	protected Date start = null;
	protected Date end = null;
	protected Date dueDate = null;
	protected int priority = Task.PRIORITY_NORMAL;
	protected boolean isCancelled = false;
	protected boolean isSuspended = false;
	protected boolean isOpen = true;
	protected boolean isSignalling = true;
	protected boolean isBlocking = false;
	protected Task task = null;
	protected Token token = null;
	protected SwimlaneInstance swimlaneInstance = null;
	protected TaskMgmtInstance taskMgmtInstance = null;
	protected ProcessInstance processInstance = null;
	protected Set<PooledActor> pooledActors = null;
	protected List comments = null;

	protected String previousActorId = null; // not persisted. just extra
												// information for listeners of
												// the assign-event
	
	//atributs per a finalització de tasques en segon pla
	protected Date marcadaFinalitzar;
	protected Date iniciFinalitzacio;
	protected String errorFinalitzacio;
	protected String selectedOutcome;
	protected String rols;
	//
	
	//flag titol actualitzat
	protected boolean titolActualitzat = false;
	//

	public TaskInstance() {
	}

	public TaskInstance(String taskName) {
		this.name = taskName;
	}

	public TaskInstance(String taskName, String actorId) {
		this.name = taskName;
		this.actorId = actorId;
	}

	public void setTask(Task task) {
		this.name = task.getName();
		this.description = task.getDescription();
		this.task = task;
		this.isBlocking = task.isBlocking();
		this.priority = task.getPriority();
		this.isSignalling = task.isSignalling();
	}

	void submitVariables() {
		TaskController taskController = (task != null ? task
				.getTaskController() : null);
		// if there is a task controller,
		if (taskController != null) {
			// the task controller is responsible for copying variables back
			// into the process
			taskController.submitParameters(this);

			// if there is no task controller
		} else if ((token != null) && (token.getProcessInstance() != null)) {
			// the default behaviour is that all task-local variables are
			// flushed to the process
			if (variableInstances != null) {
				ContextInstance contextInstance = token.getProcessInstance()
						.getContextInstance();
				Iterator iter = variableInstances.values().iterator();
				while (iter.hasNext()) {
					VariableInstance variableInstance = (VariableInstance) iter
							.next();
					log.debug("flushing variable '"
							+ variableInstance.getName() + "' from task '"
							+ name + "' to process variables");
					// This might be optimized, but this was the simplest way to
					// make a clone of the variable instance.
					contextInstance.setVariable(variableInstance.getName(),
							variableInstance.getValue(), token);
				}
			}
		}
	}

	void initializeVariables() {
		TaskController taskController = (task != null ? task
				.getTaskController() : null);
		if (taskController != null) {
			taskController.initializeVariables(this);
		}
	}

	public void create() {
		create(null);
	}

	public void create(ExecutionContext executionContext) {
		if (create != null) {
			throw new IllegalStateException("task instance '" + id
					+ "' was already created");
		}
		create = Clock.getCurrentTime();

		// if this task instance is associated with a task...
		if ((task != null) && (executionContext != null)) {
			// the TASK_CREATE event is fired
			executionContext.setTaskInstance(this);
			executionContext.setTask(task);
			task.fireEvent(Event.EVENTTYPE_TASK_CREATE, executionContext);
		}

		// WARNING: The events create and assign are fired in the right order,
		// but
		// the logs are still not ordered properly.
		// See also: TaskMgmtInstance.createTaskInstance
	}

	public void assign(ExecutionContext executionContext) {
		TaskMgmtInstance taskMgmtInstance = executionContext
				.getTaskMgmtInstance();

		Swimlane swimlane = task.getSwimlane();
		// if this task is in a swimlane
		if (swimlane != null) {

			// if this is a task assignment for a start-state
			if (isStartTaskInstance()) {
				// initialize the swimlane
				swimlaneInstance = new SwimlaneInstance(swimlane);
				taskMgmtInstance.addSwimlaneInstance(swimlaneInstance);
				// with the current authenticated actor
				swimlaneInstance.setActorId(SecurityHelper
						.getAuthenticatedActorId());

			} else {

				// lazy initialize the swimlane...
				// get the swimlane instance (if there is any)
				swimlaneInstance = taskMgmtInstance
						.getInitializedSwimlaneInstance(executionContext,
								swimlane);

				// copy the swimlaneInstance assignment into the taskInstance
				// assignment
				copySwimlaneInstanceAssignment(swimlaneInstance);
			}

		} else { // this task is not in a swimlane
			taskMgmtInstance.performAssignment(task.getAssignmentDelegation(),
					task.getActorIdExpression(), task
							.getPooledActorsExpression(), this,
					executionContext);
		}

		updatePooledActorsReferences(swimlaneInstance);
	}

	public boolean isStartTaskInstance() {
		boolean isStartTaskInstance = false;
		if ((taskMgmtInstance != null)
				&& (taskMgmtInstance.getTaskMgmtDefinition() != null)) {
			isStartTaskInstance = ((task != null) && (task
					.equals(taskMgmtInstance.getTaskMgmtDefinition()
							.getStartTask())));
		}
		return isStartTaskInstance;
	}

	void updatePooledActorsReferences(SwimlaneInstance swimlaneInstance) {
		if (pooledActors != null) {
			Iterator iter = pooledActors.iterator();
			while (iter.hasNext()) {
				PooledActor pooledActor = (PooledActor) iter.next();
				pooledActor.setSwimlaneInstance(swimlaneInstance);
				pooledActor.addTaskInstance(this);
			}
		}
	}

	/**
	 * copies the assignment (that includes both the swimlaneActorId and the set
	 * of pooledActors) of the given swimlane into this taskInstance.
	 */
	public void copySwimlaneInstanceAssignment(SwimlaneInstance swimlaneInstance) {
		setSwimlaneInstance(swimlaneInstance);
		setActorId(swimlaneInstance.getActorId());
		setPooledActors(swimlaneInstance.getPooledActors());
	}

	/**
	 * gets the pool of actors for this task instance. If this task has a
	 * simlaneInstance and no pooled actors, the pooled actors of the swimlane
	 * instance are returned.
	 */
	public Set<PooledActor> getPooledActors() {
		if ((swimlaneInstance != null)
				&& ((pooledActors == null) || (pooledActors.isEmpty()))) {
			return swimlaneInstance.getPooledActors();
		}
		return pooledActors;
	}

	/**
	 * (re)assign this task to the given actor. If this task is related to a
	 * swimlane instance, that swimlane instance will be updated as well.
	 */
	public void setActorId(String actorId) {
		setActorId(actorId, true, false);
	}
	
	/**
	 * (re)assign this task to the given actor. If this task is related to a
	 * swimlane instance, that swimlane instance will be updated as well.
	 */
	public void setActorId(String actorId, boolean overwriteSwimlane) {
		setActorId(actorId, overwriteSwimlane, false);
	}

	/**
	 * (re)assign this task to the given actor.
	 * 
	 * @param actorId
	 *            is reference to the person that is assigned to this task.
	 * @param overwriteSwimlane
	 *            specifies if the related swimlane should be overwritten with
	 *            the given swimlaneActorId.
	 * @param ignorarReassignacio
	 * 			  indica si hem d'ignorar la reassignació entre usuaris
	 */
	public void setActorId(String actorId, boolean overwriteSwimlane, boolean ignorarReassignacio) {
		// do the actual assignment
		this.previousActorId = this.actorId;
		this.actorId = actorId;
		String actor = actorId;
		if (!ignorarReassignacio && actorId != null) {
			String processInstanceId = null;
			if (this.getContextInstance() != null && this.getContextInstance().getProcessInstance() != null)
				processInstanceId = String.valueOf(this.getContextInstance().getProcessInstance().getId());
			ReassignacioDto reassignacio = Jbpm3HeliumBridge.getInstanceService().findReassignacioActivaPerUsuariOrigen(processInstanceId, actor);
  	  		if (reassignacio != null) {
  	  			actor = reassignacio.getUsuariDesti();
  	  			this.actorId = actor;
  	  		}
		}
		
		if ((swimlaneInstance != null) && (overwriteSwimlane)) {
			log.debug("assigning task '" + name + "' to '" + actor + "'");
			swimlaneInstance.setActorId(actor);
		}

		// fire the event
		if ((task != null) && (token != null)) {
			ExecutionContext executionContext = new ExecutionContext(token);
			executionContext.setTask(task);
			executionContext.setTaskInstance(this);

			// WARNING: The events create and assign are fired in the right
			// order, but
			// the logs are still not ordered properly.
			// See also: TaskMgmtInstance.createTaskInstance
			task.fireEvent(Event.EVENTTYPE_TASK_ASSIGN, executionContext);
		}

		// add the log
		if (token != null) {
			// log this assignment
			token.addLog(new TaskAssignLog(this, previousActorId, actor));
		}
	}

	/** takes a set of String's as the actorIds */
	public void setPooledActors(String[] actorIds) {
		this.pooledActors = PooledActor.createPool(actorIds, null, this);
		if ((task != null) && (token != null)) {
			ExecutionContext executionContext = new ExecutionContext(token);
			executionContext.setTask(task);
			executionContext.setTaskInstance(this);
		}
	}

	/**
	 * can optionally be used to indicate that the actor is starting to work on
	 * this task instance.
	 */
	public void start() {
		if (start != null) {
			throw new IllegalStateException("task instance '" + id
					+ "' is already started");
		}

		start = Clock.getCurrentTime();
		if ((task != null) && (token != null)) {
			ExecutionContext executionContext = new ExecutionContext(token);
			executionContext.setTask(task);
			executionContext.setTaskInstance(this);
			task.fireEvent(Event.EVENTTYPE_TASK_START, executionContext);
		}
	}

	/**
	 * convenience method that combines a {@link #setActorId(String)} and a
	 * {@link #start()}.
	 */
	public void start(String actorId) {
		start(actorId, true, false);
	}

	/**
	 * convenience method that combines a {@link #setActorId(String,boolean)}
	 * and a {@link #start()}.
	 */
	public void start(String actorId, boolean overwriteSwimlane, boolean ignorarReassignacio) {
		setActorId(actorId, overwriteSwimlane, ignorarReassignacio);
		start();
	}

	/**
	 * overwrite start date
	 */
	public void setStart(Date date) {
		start = null;
	}

	private void markAsCancelled() {
		this.isCancelled = true;
		this.isOpen = false;
	}

	/**
	 * cancels this task. This task intance will be marked as cancelled and as
	 * ended. But cancellation doesn't influence singalling and continuation of
	 * process execution.
	 */
	public void cancel() {
		markAsCancelled();
		end();
	}

	/**
	 * cancels this task, takes the specified transition. This task intance will
	 * be marked as cancelled and as ended. But cancellation doesn't influence
	 * singalling and continuation of process execution.
	 */
	public void cancel(Transition transition) {
		markAsCancelled();
		end(transition);
	}

	/**
	 * cancels this task, takes the specified transition. This task intance will
	 * be marked as cancelled and as ended. But cancellation doesn't influence
	 * singalling and continuation of process execution.
	 */
	public void cancel(String transitionName) {
		markAsCancelled();
		end(transitionName);
	}

	/**
	 * marks this task as done. If this task is related to a task node this
	 * might trigger a signal on the token.
	 * 
	 * @see #end(Transition)
	 */
	public void end() {
		end((Transition) null);
	}

	/**
	 * marks this task as done and specifies the name of a transition leaving
	 * the task-node for the case that the completion of this task instances
	 * triggers a signal on the token. If this task leads to a signal on the
	 * token, the given transition name will be used in the signal. If this task
	 * completion does not trigger execution to move on, the transitionName is
	 * ignored.
	 */
	public void end(String transitionName) {
		Transition leavingTransition = null;

		if (task != null) {
			Node node = task.getTaskNode();
			if (node == null) {
				node = (Node) task.getParent();
			}

			if (node != null) {
				leavingTransition = node.getLeavingTransition(transitionName);
			}
		}
		if (leavingTransition == null) {
			throw new JbpmException(
					"task node does not have leaving transition '"
							+ transitionName + "'");
		}
		end(leavingTransition);
	}

	/**
	 * marks this task as done and specifies a transition leaving the task-node
	 * for the case that the completion of this task instances triggers a signal
	 * on the token. If this task leads to a signal on the token, the given
	 * transition name will be used in the signal. If this task completion does
	 * not trigger execution to move on, the transition is ignored.
	 */
	public void end(Transition transition) {
		if (this.end != null) {
			throw new IllegalStateException("task instance '" + id
					+ "' is already ended");
		}
		if (this.isSuspended) {
			throw new JbpmException("task instance '" + id + "' is suspended");
		}

		// mark the end of this task instance
		this.end = Clock.getCurrentTime();
		this.isOpen = false;

		// fire the task instance end event
		if ((task != null) && (token != null)) {
			ExecutionContext executionContext = new ExecutionContext(token);
			executionContext.setTask(task);
			executionContext.setTaskInstance(this);
			task.fireEvent(Event.EVENTTYPE_TASK_END, executionContext);
		}

		// log this assignment
		if (token != null) {
			token.addLog(new TaskEndLog(this));
		}

		// submit the variables
		submitVariables();

		// verify if the end of this task triggers continuation of execution
		if (isSignalling) {
			this.isSignalling = false;

			if (this.isStartTaskInstance() // ending start tasks always leads to
											// a signal
					|| ((task != null) && (token != null)
							&& (task.getTaskNode() != null) && (task
							.getTaskNode().completionTriggersSignal(this)))) {

				if (transition == null) {
					log.debug("completion of task '" + task.getName()
							+ "' results in taking the default transition");
					token.signal();
				} else {
					log.debug("completion of task '" + task.getName()
							+ "' results in taking transition '" + transition
							+ "'");
					token.signal(transition);
				}
			}
		}
		try {
			Jbpm3HeliumBridge.getInstanceService().alertaEsborrarAmbTaskInstanceId(this.getId());
		} catch (NoTrobatException ex) {
			log.error("No s'ha trobat la taskInstance (id=" + this.getId() + ")", ex);
		}
	}

	public boolean hasEnded() {
		return (end != null);
	}

	/**
	 * suspends a process execution.
	 */
	public void suspend() {
		if (!isOpen) {
			throw new JbpmException(
					"a task that is not open cannot be suspended: "
							+ toString());
		}
		isSuspended = true;
	}

	/**
	 * resumes a process execution.
	 */
	public void resume() {
		if (!isOpen) {
			throw new JbpmException(
					"a task that is not open cannot be resumed: " + toString());
		}
		isSuspended = false;
	}

	// comments
	// /////////////////////////////////////////////////////////////////

	public void addComment(String message) {
		addComment(new Comment(message));
	}

	public void addComment(Comment comment) {
		if (comment != null) {
			if (comments == null)
				comments = new ArrayList();
			comments.add(comment);
			comment.setTaskInstance(this);
			if (token != null) {
				comment.setToken(token);
				token.addComment(comment);
			}
		}
	}

	public List getComments() {
		return comments;
	}

	// task form
	// ////////////////////////////////////////////////////////////////

	public boolean isLast() {
		return ((token != null) && (taskMgmtInstance != null) && (!taskMgmtInstance
				.hasUnfinishedTasks(token)));
	}

	/**
	 * is the list of transitions that can be used in the end method and it is
	 * null in case this is not the last task instance.
	 */
	public List getAvailableTransitions() {
		List transitions = null;
		if ((!isLast()) && (token != null)) {
			transitions = new ArrayList(token.getAvailableTransitions());
		}
		return transitions;
	}

	// equals
	// ///////////////////////////////////////////////////////////////////
	// hack to support comparing hibernate proxies against the real objects
	// since this always falls back to ==, we don't need to overwrite the
	// hashcode
	public boolean equals(Object o) {
		return EqualsUtil.equals(this, o);
	}

	public String toString() {
		return "TaskInstance"
				+ (name != null ? "(" + name + ")" : "@"
						+ Integer.toHexString(hashCode()));
	}

	// private
	// //////////////////////////////////////////////////////////////////

	/** takes a set of {@link PooledActor}s */
	public void setPooledActors(Set pooledActors) {
		if (pooledActors != null) {
			this.pooledActors = new HashSet(pooledActors);
			Iterator iter = pooledActors.iterator();
			while (iter.hasNext()) {
				PooledActor pooledActor = (PooledActor) iter.next();
				pooledActor.addTaskInstance(this);
			}
		} else {
			this.pooledActors = null;
		}
	}

	// protected
	// ////////////////////////////////////////////////////////////////

	protected VariableContainer getParentVariableContainer() {
		ContextInstance contextInstance = getContextInstance();
		return (contextInstance != null ? contextInstance
				.getOrCreateTokenVariableMap(token) : null);
	}

	// getters and setters
	// //////////////////////////////////////////////////////

	public String getActorId() {
		return actorId;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public void setCreate(Date create) {
		this.create = create;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getStart() {
		return start;
	}

	public TaskMgmtInstance getTaskMgmtInstance() {
		return taskMgmtInstance;
	}

	public void setTaskMgmtInstance(TaskMgmtInstance taskMgmtInstance) {
		this.taskMgmtInstance = taskMgmtInstance;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public void setSignalling(boolean isSignalling) {
		this.isSignalling = isSignalling;
	}

	public boolean isSignalling() {
		return isSignalling;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isBlocking() {
		return isBlocking;
	}

	public void setBlocking(boolean isBlocking) {
		this.isBlocking = isBlocking;
	}

	public Date getCreate() {
		return create;
	}

	public Task getTask() {
		return task;
	}

	public SwimlaneInstance getSwimlaneInstance() {
		return swimlaneInstance;
	}

	public void setSwimlaneInstance(SwimlaneInstance swimlaneInstance) {
		this.swimlaneInstance = swimlaneInstance;
	}

	public String getPreviousActorId() {
		return previousActorId;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isSuspended() {
		return isSuspended;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public Date getMarcadaFinalitzar() {
		return marcadaFinalitzar;
	}

	public void setMarcadaFinalitzar(Date marcadaFinalitzar) {
		this.marcadaFinalitzar = marcadaFinalitzar;
	}

	public Date getIniciFinalitzacio() {
		return iniciFinalitzacio;
	}

	public void setIniciFinalitzacio(Date iniciFinalitzacio) {
		this.iniciFinalitzacio = iniciFinalitzacio;
	}

	public String getErrorFinalitzacio() {
		return errorFinalitzacio;
	}

	public void setErrorFinalitzacio(String errorFinalitzacio) {
		this.errorFinalitzacio = errorFinalitzacio;
	}

	public String getSelectedOutcome() {
		return selectedOutcome;
	}

	public void setSelectedOutcome(String selectedOutcome) {
		this.selectedOutcome = selectedOutcome;
	}

	public String getRols() {
		return rols;
	}

	public void setRols(String rols) {
		this.rols = rols;
	}

	private static final Log log = LogFactory.getLog(TaskInstance.class);
}
