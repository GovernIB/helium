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
package org.jbpm.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jbpm.JbpmException;
import org.jbpm.command.ChangeProcessInstanceVersionCommand;
import org.jbpm.graph.action.Script;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.GraphElement;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.ProcessInstanceExpedient;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.log.ActionLog;
import org.jbpm.graph.log.SignalLog;
import org.jbpm.graph.log.TransitionLog;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.instantiation.Delegation;
import org.jbpm.job.Job;
import org.jbpm.logging.log.CompositeLog;
import org.jbpm.logging.log.ProcessLog;
import org.jbpm.scheduler.def.CancelTimerAction;
import org.jbpm.scheduler.def.CreateTimerAction;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.def.TaskMgmtDefinition;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * are the graph related database operations.
 */
@SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
public class GraphSession {

	public static Map<Long, Set<ProcessInstanceExpedient>> errorsDelete = new HashMap<Long, Set<ProcessInstanceExpedient>>();
//	public static Map<Long, List<Object[]>> errorsDelete = new HashMap<Long, List<Object[]>>();
//	
//	public static enum DeleteErrorType {
//		TASK,
//		JOB, 
//		ACTION, 
//		TRANSITION;
//	}
	
	JbpmSession jbpmSession = null;
	Session session = null;

	public GraphSession(JbpmSession jbpmSession) {
		this.jbpmSession = jbpmSession;
		this.session = jbpmSession.getSession();
	}

	public GraphSession(Session session) {
		this.session = session;
		this.jbpmSession = new JbpmSession(session);
	}

	// process definitions
	// //////////////////////////////////////////////////////

	public void deployProcessDefinition(ProcessDefinition processDefinition) {
		String processDefinitionName = processDefinition.getName();
		// if the process definition has a name (process versioning only applies
		// to named process definitions)
		if (processDefinitionName != null) {
			// find the current latest process definition
			ProcessDefinition previousLatestVersion = findLatestProcessDefinition(processDefinitionName);
			// if there is a current latest process definition
			if (previousLatestVersion != null) {
				// take the next version number
				processDefinition.setVersion(previousLatestVersion.getVersion() + 1);
			} else {
				// start from 1
				processDefinition.setVersion(1);
			}

			session.save(processDefinition);

		} else {
			throw new JbpmException("process definition does not have a name");
		}
	}

	/**
	 * saves the process definitions. this method does not assign a version number. that is the responsibility of the
	 * {@link #deployProcessDefinition(ProcessDefinition)
	 * deployProcessDefinition} method.
	 */
	public void saveProcessDefinition(ProcessDefinition processDefinition) {
		try {
			session.save(processDefinition);
		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException("couldn't save process definition '" + processDefinition + "'", e);
		}
	}

	/**
	 * loads a process definition from the database by the identifier.
	 * 
	 * @throws JbpmException
	 *             in case the referenced process definition doesn't exist.
	 */
	public ProcessDefinition loadProcessDefinition(long processDefinitionId) {
		try {
			return (ProcessDefinition) session.load(ProcessDefinition.class, new Long(processDefinitionId));
		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException("couldn't load process definition '" + processDefinitionId + "'", e);
		}
	}

	/**
	 * gets a process definition from the database by the identifier.
	 * 
	 * @return the referenced process definition or null in case it doesn't
	 *         exist.
	 */
	public ProcessDefinition getProcessDefinition(long processDefinitionId) {
		try {
			return (ProcessDefinition) session.get(ProcessDefinition.class, new Long(processDefinitionId));
		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException("couldn't get process definition '" + processDefinitionId + "'", e);
		}
	}

	/**
	 * queries the database for a process definition with the given name and
	 * version.
	 */
	public ProcessDefinition findProcessDefinition(String name, int version) {
		ProcessDefinition processDefinition = null;
		try {
			Query query = session.getNamedQuery("GraphSession.findProcessDefinitionByNameAndVersion");
			query.setString("name", name);
			query.setInteger("version", version);
			processDefinition = (ProcessDefinition) query.uniqueResult();
		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException(
					"couldn't get process definition with name '" + name + "' and version '" + version + "'", e);
		}
		return processDefinition;
	}

	/**
	 * queries the database for the latest version of a process definition with
	 * the given name.
	 */
	public ProcessDefinition findLatestProcessDefinition(String name) {
		ProcessDefinition processDefinition = null;
		try {
			Query query = session.getNamedQuery("GraphSession.findLatestProcessDefinitionQuery");
			query.setString("name", name);
			query.setMaxResults(1);
			processDefinition = (ProcessDefinition) query.uniqueResult();
		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException("couldn't find process definition '" + name + "'", e);
		}
		return processDefinition;
	}

	/**
	 * queries the database for the latest version of each process definition.
	 * Process definitions are distinct by name.
	 */
	public List findLatestProcessDefinitions() {
		List processDefinitions = new ArrayList();
		Map processDefinitionsByName = new HashMap();
		try {
			Query query = session.getNamedQuery("GraphSession.findAllProcessDefinitions");
			Iterator iter = query.list().iterator();
			while (iter.hasNext()) {
				ProcessDefinition processDefinition = (ProcessDefinition) iter.next();
				String processDefinitionName = processDefinition.getName();
				ProcessDefinition previous = (ProcessDefinition) processDefinitionsByName.get(processDefinitionName);
				if ((previous == null) || (previous.getVersion() < processDefinition.getVersion())) {
					processDefinitionsByName.put(processDefinitionName, processDefinition);
				}
			}
			processDefinitions = new ArrayList(processDefinitionsByName.values());
		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException("couldn't find latest versions of process definitions", e);
		}
		return processDefinitions;
	}

	/**
	 * queries the database for all process definitions, ordered by name
	 * (ascending), then by version (descending).
	 */
	public List findAllProcessDefinitions() {
		try {
			Query query = session.getNamedQuery("GraphSession.findAllProcessDefinitions");
			return query.list();
		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException("couldn't find all process definitions", e);
		}
	}

	/**
	 * queries the database for all versions of process definitions with the
	 * given name, ordered by version (descending).
	 */
	public List findAllProcessDefinitionVersions(String name) {
		try {
			Query query = session.getNamedQuery("GraphSession.findAllProcessDefinitionVersions");
			query.setString("name", name);
			return query.list();
		} catch (HibernateException e) {
			log.error(e);
			throw new JbpmException("couldn't find all versions of process definition '" + name + "'", e);
		}
	}

	public void deleteProcessDefinition(long processDefinitionId) {
		deleteProcessDefinition(loadProcessDefinition(processDefinitionId));
	}

	public void deleteProcessDefinition(ProcessDefinition processDefinition) {
		if (processDefinition == null) {
			throw new JbpmException("processDefinition is null");
		}
		
//		List<Object[]> errors = new ArrayList<Object[]>();
		Set<ProcessInstanceExpedient> expedientsError = new HashSet<ProcessInstanceExpedient>();
		
		try {
			// delete all the process instances of this definition
			for (ProcessInstance processInstance; ((processInstance = findNextProcessInstance(processDefinition)) != null);) {
				deleteProcessInstance(processInstance);
			}

//			CODI DE LA FUNCIONALITAT DE BORRAT DE DEFINICIONS DE PROCÉS -- Inici
			// INICI MODIFICACIONS per a poder eliminar DefProc que han tingut expedients ------------------------------------------------
			// Canviam tasques "orfes", encara referenciades per la definició de procés antiga
			log.debug("   - Eliminam la definició de procés " + processDefinition.getName() + "(" + processDefinition.getId() + ")");
			log.debug("   |- Modificam els TaskInstances relacionats amb la definició de procés antiga:");
//			Set<TaskMgmtDefinition> tmdBorrar = new HashSet<TaskMgmtDefinition>();
			for (TaskInstance ti : findReferencedDPTaskInstances(processDefinition.getId())) {
				log.debug("   ||- TasckInstance: " + ti.getName() + "(" + ti.getId() + ")");
				Task oldTask = ti.getTask();
				Node oldNode = oldTask.getTaskNode();
				Task newTask = findReplacementTask(ti.getProcessInstance().getProcessDefinition(), oldNode, oldTask);
				log.debug("   |||- Actualitzam Task:");
				log.debug("   ||||- Task antiga: " + oldTask.getName() + "(" + oldTask.getId() + ")");
				if (newTask != null) {
					log.debug("   ||||- Task nova: " + newTask.getName() + "(" + newTask.getId() + ")");
					ti.setTask(newTask);
					session.save(ti);
					// Task Managment
					TaskMgmtDefinition newTmd = newTask.getTaskMgmtDefinition();
					TaskMgmtDefinition oldTmd = ti.getTaskMgmtInstance().getTaskMgmtDefinition();
					if (oldTmd.getProcessDefinition().equals(processDefinition)) {
						log.debug("   |||- Actualitzam TaskMgmtDefinition del TaskMgmtinstance:");
						log.debug("   ||||- TaskMgmtDefinition Antiga: " + newTmd.getName() + "(" + newTmd.getId() + ")");
						log.debug("   ||||- TaskMgmtDefinition Nova: " + oldTmd.getName() + "(" + oldTmd.getId() + ")");
						ti.getTaskMgmtInstance().setTaskMgmtDefinition(newTmd);
						session.save(ti.getTaskMgmtInstance());
//						tmdBorrar.add(oldTmd);
					}
				} else {
					log.debug("   ||||- Task nova: NO S'HA TROBAT AQUESTA TASCA EN LA NOVA DEFINICIÓ DE PROCÉS.");
//					errors.add(new Object[]{
//									DeleteErrorType.TASK, 
//									ti.getProcessInstance().getExpedient(),
//									ti.getName(), 
//									ti.getId()});
//					errorsDelete.put(processDefinition.getId(), errors);
					expedientsError.add(ti.getProcessInstance().getExpedient());
					errorsDelete.put(processDefinition.getId(), expedientsError);
					throw new JbpmException("S'han torbat tasques lligades a la definició de procés, que no tenen un substitut en la definició de procés del seu expedient.");
				}
				log.debug("   ||| ");
				log.debug("   || ");
				log.debug("   | ");
			}
						
			// Canviam el processDefinition dels tasksMgmtInstance dels tasksInstance de la versió que s'intenta esborrar a la de la tasca.
			log.debug("   |- Modificam els TaskMgmtDefinition:");
			for (TaskInstance ti : findReferencedTaskInstances(processDefinition.getId())) {
				log.debug("   ||- TasckInstance: " + ti.getName() + "(" + ti.getId() + ")");
				TaskMgmtDefinition oldTmd = ti.getTaskMgmtInstance().getTaskMgmtDefinition();
				TaskMgmtDefinition newTmd = ti.getProcessInstance().getProcessDefinition().getTaskMgmtDefinition();
				if (oldTmd.getProcessDefinition().equals(processDefinition)) {
					log.debug("   |||- TaskMgmtDefinition Antiga: " + oldTmd.getName() + "(" + oldTmd.getId() + ")");
					log.debug("   |||- TaskMgmtDefinition Nova: " + newTmd.getName() + "(" + newTmd.getId() + ")");
					ti.getTaskMgmtInstance().setTaskMgmtDefinition(newTmd);
					session.save(ti.getTaskMgmtInstance());
//					tmdBorrar.add(oldTmd);
				}
//				tmd.setProcessDefinition(ti.getProcessInstance().getProcessDefinition());
//				if (tmd.getStartTask() != null) {
//					tmd.setStartTask(ti.getProcessInstance().getProcessDefinition().getTaskMgmtDefinition().getStartTask());
//				}
//				session.save(tmd);
				log.debug("   || ");
			}
			log.debug("   | ");
			
//			// Eliminam les StartStates que referencien la DefProc antiga
//			for (Task task: findReferencingStartStates(processDefinition.getId())) {
//				task.setStartState(null);
//				session.save(task);
//			}
//			for (Iterator iter = processDefinition.getNodes().iterator(); iter.hasNext();) {
//				Node node = (Node)iter.next();
//				if (node instanceof TaskNode)
//					iter.remove();
//			}
			List<Long> taskIds = new ArrayList<Long>();
			log.debug("   |- Dereferenciam les tasques:");
			for (Task task: findReferencedTask(processDefinition.getId())) {
				log.debug("   ||- " + task.getName() + "(" + task.getId() + ")");
				taskIds.add(task.getId());
				task.setStartState(null);
				task.setAssignmentDelegation(null);
				task.setTaskController(null);
				task.setProcessDefinition(null);
				session.save(task);
//				session.delete(task);
			}
			log.debug("   | ");
			session.flush();
			
			// Eliminam les delegacions
//			for (Task task : findReferencingDelegatingTasks(processDefinition.getId())) {
//				task.setAssignmentDelegation(null);
//				session.save(task);
////				session.flush();
//			}
			
			log.debug("   |- Dereferenciam els jobs:");
			for (Job job : findReferencedJobs(processDefinition.getId())) {
				log.debug("   ||- " + job.getId());
				Action oldAction = (Action)job.getClass().getMethod("getAction").invoke(job);
				Action newAction = job.getProcessInstance().getProcessDefinition().getAction(oldAction.getName()); 
				if (newAction != null) {
					job.getClass().getMethod("setAction", Action.class).invoke(job, newAction);
					session.save(job);
				} else {
					if (job.getRetries() <= 0) {
						session.delete(job);
					} else {
//						errors.add(new Object[]{
//										DeleteErrorType.JOB,
//										job.getProcessInstance().getExpedient(),
//										oldAction.toString(),
//										oldAction.getId(),										
//										job.getId()});
//						errorsDelete.put(processDefinition.getId(), errors);
						expedientsError.add(job.getProcessInstance().getExpedient());
						errorsDelete.put(processDefinition.getId(), expedientsError);
					//	throw new JbpmException("S'han torbat jobs lligades a la definició de procés, que no tenen una acció substituta en la definició de procés del seu expedient.");
					}
				}
//				session.delete(action);
//				session.flush();
			}
			log.debug("   | ");
			
			log.debug("   |- Dereferenciam les accions:");
			//for (Action action : findReferencingDelegatingActions(processDefinition.getId())) {
			List<Action> accions = findReferencedActions(processDefinition.getId());
			for (Action action : accions) {
				action.setActionDelegation(null);
				session.save(action);
//				session.delete(action);
//				session.flush();
				log.debug("   ||- " + action.getName() + "(" + action.getId() + ")");
				
			}
			
			log.debug("   | ");
			
			log.debug("   |- Dereferenciam les accions del logs:");
//			if (!processDefinition.getEvents().isEmpty()) {
				//for (ActionLog al : findReferencingEventActions(processDefinition)) {
			// Mapa de reemplaç de accions
			Map<Long, Action> reAccions = new HashMap<Long, Action>();
			for (ActionLog al : findReferencedActionLogs(accions)) {
				log.debug("   ||- Log: " + al.getId() + ", Action: " + al.getAction().getId());
				ProcessDefinition newProcessDefinition = al.getToken().getProcessInstance().getProcessDefinition();
				Action oldAction = al.getAction();
				Action newAction = null;
				if (reAccions.containsKey(oldAction.getId())) {
					newAction = reAccions.get(oldAction.getId());
				} else {
					newAction =	findReplacementEventAction(oldAction, newProcessDefinition);
					reAccions.put(oldAction.getId(), newAction);
				}
				if (newAction != null) {
					al.setAction(newAction);
				} else {
					log.debug("   |||- No s'ha trobat substitut per aquesta acció");
//					errors.add(new Object[]{
//									DeleteErrorType.ACTION, 
//									al.getToken().getProcessInstance().getExpedient(), 
//									oldAction.toString(), 
//									oldAction.getId(), 
//									al.getId()});
//					errorsDelete.put(processDefinition.getId(), errors);
					expedientsError.add(al.getToken().getProcessInstance().getExpedient());
					errorsDelete.put(processDefinition.getId(), expedientsError);
				}
			}
//			}
			
			log.debug("   | ");
			
			log.debug("   |- Dereferenciam les transicions dels logs:");
			// Mapa de reemplaç de transicions
			Map<Long, Transition> reTransicions = new HashMap<Long, Transition>();
			List<CompositeLog> logs = findReferencedTransitionLogs(processDefinition.getId());
			for (CompositeLog cl : logs) {
				TransitionLog tl = null;
				SignalLog sl = null;
				Transition oldTransition = null;
				if (cl instanceof TransitionLog) {
					tl = (TransitionLog)cl;
					oldTransition = tl.getTransition();
				} else {
					sl = (SignalLog)cl;
					oldTransition = sl.getTransition();
				}
				log.debug("   ||- Log: " + cl.getId() + ", Transition: " + oldTransition.getId());
				ProcessDefinition newProcessDefinition = cl.getToken().getProcessInstance().getProcessDefinition();
				
				Transition newTransition = null;
				if (reTransicions.containsKey(oldTransition.getId())) {
					newTransition = reTransicions.get(oldTransition.getId());
				} else {
					newTransition = findReplacementTransition(newProcessDefinition, oldTransition);
					reTransicions.put(oldTransition.getId(), newTransition);
				}
				if (newTransition != null) {
					if (tl != null) {
						tl.setTransition(newTransition);
						tl.setSourceNode(newTransition.getFrom());
						tl.setDestinationNode(newTransition.getTo());
					} else {
						sl.setTransition(newTransition);
					}
				} else {
					log.debug("   |||- No s'ha trobat substitut per aquesta transició");
//					errors.add(new Object[]{
//							DeleteErrorType.TRANSITION, 
//							tl.getToken().getProcessInstance().getExpedient(), 
//							oldTransition.toString(), 
//							oldTransition.getId(), 
//							tl.getId()});
//					errorsDelete.put(processDefinition.getId(), errors);
					expedientsError.add(cl.getToken().getProcessInstance().getExpedient());
					errorsDelete.put(processDefinition.getId(), expedientsError);
				}
			}
			
			log.debug("   | ");
			log.debug("   |- Eliminam els taskControllers:");
			for (TaskController tc : findReferencedTaskController(processDefinition.getId())) {
//				tc.setTaskControllerDelegation(null);
//				session.save(tc);
				log.debug("   ||- " + tc.getId());
				session.delete(tc);
//				session.flush();
			}
			log.debug("   | ");
			session.flush();
			
			log.debug("   |- Eliminam les delegacions:");
			for (Delegation delegation : findReferencedDelegations(processDefinition.getId())) {
				log.debug("   ||- " + delegation.getId());
				session.delete(delegation);
				session.flush();
			}
			log.debug("   | ");
			
			
			// ---------------------------------------------------------------------------------------------------------------------------
//			CODI DE LA FUNCIONALITAT DE BORRAT DE DEFINICIONS DE PROCÉS -- Fi
			
			// Eliminam els StartStates de les tasques de la definició de procés a esborrar
			List referencingProcessStates = findReferencingProcessStates(processDefinition);
			for (Iterator iter = referencingProcessStates.iterator(); iter.hasNext();) {
				ProcessState processState = (ProcessState) iter.next();
				processState.setSubProcessDefinition(null);
			}
//			CODI DE LA FUNCIONALITAT DE BORRAT DE DEFINICIONS DE PROCÉS -- Inici
			session.flush();
//			CODI DE LA FUNCIONALITAT DE BORRAT DE DEFINICIONS DE PROCÉS -- Fi
			
			// then delete the process definition
			session.delete(processDefinition);
			
//			CODI DE LA FUNCIONALITAT DE BORRAT DE DEFINICIONS DE PROCÉS -- Inici
			// ---------------------------------------------------------------------------------------------------------------------------
			// Eliminam les tasques que hagin quedat orfes
			log.debug("   |- Eliminam les tasques");
			log.debug("   ||- " + taskIds.toString());
			session.createQuery(
					  "delete from org.jbpm.taskmgmt.def.Task t "
					+ " where t.id in :taskIds")
			.setParameterList("taskIds", taskIds)
			.executeUpdate();

			log.debug("   | ");
			
//			log.debug("   |- Eliminam els taskMgmtDefinition:");
//			for (TaskMgmtDefinition tmd: tmdBorrar)
//				session.delete(tmd);
//			log.debug("   | ");
			// FI MODIFICACIONS per a poder eliminar DefProc que han tingut expedients ---------------------------------------------------
			// ---------------------------------------------------------------------------------------------------------------------------
//			CODI DE LA FUNCIONALITAT DE BORRAT DE DEFINICIONS DE PROCÉS -- Fi
			
		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			for (ProcessInstanceExpedient expedient: expedientsError) {
				expedient.getIdentificador();
//				expedient.getId();
			}
			throw new JbpmException("couldn't delete process definition '" + processDefinition.getId() + "'", e);
		}
	}

	protected ProcessInstance findNextProcessInstance(ProcessDefinition processDefinition) {
		return (ProcessInstance) session.createCriteria(ProcessInstance.class)
				.add(Restrictions.eq("processDefinition", processDefinition)).setMaxResults(1).uniqueResult();
	}

	protected List findReferencingProcessStates(ProcessDefinition subProcessDefinition) {
		Query query = session.getNamedQuery("GraphSession.findReferencingProcessStates");
		query.setEntity("subProcessDefinition", subProcessDefinition);
		return query.list();
	}
	
//	CODI DE LA FUNCIONALITAT DE BORRAT DE DEFINICIONS DE PROCÉS -- Inici
	protected List<Task> findReferencedTask(Long processDefinitionId) {
		Query q = session.createQuery(
				  "from org.jbpm.taskmgmt.def.Task t "
				+ "where t.processDefinition.id = :processDefinitionId");
		q.setLong("processDefinitionId", processDefinitionId);
		return (List<Task>)q.list();
	}
	
	protected List<TaskInstance> findReferencedTaskInstances(Long processDefinitionId) {
		Query q = session.createQuery(
				  "from org.jbpm.taskmgmt.exe.TaskInstance ti "
				+ "where ti.taskMgmtInstance.taskMgmtDefinition.processDefinition.id = :processDefinitionId ");
		q.setLong("processDefinitionId", processDefinitionId);
		return (List<TaskInstance>)q.list();
	}
	
	protected List<TaskInstance> findReferencedDPTaskInstances(Long processDefinitionId) {
		Query q = session.createQuery(
				  "from org.jbpm.taskmgmt.exe.TaskInstance ti "
				+ "where ti.task.processDefinition.id = :processDefinitionId ");
		q.setLong("processDefinitionId", processDefinitionId);
		return (List<TaskInstance>)q.list();
	}
	
	protected List<Task> findReferencingStartStates(Long processDefinitionId) {
		Query query = session.createQuery(
				  " from org.jbpm.taskmgmt.def.Task t "
				+ "where t.processDefinition.id = :processDefinitionId "
				+ "  and t.startState is not null");
		query.setLong("processDefinitionId", processDefinitionId);
		return (List<Task>)query.list();
	}
	
//	protected List<Task> findReferencingDelegatingTasks(Long processDefinitionId) {
//		Query query = session.createQuery(
//				  " from org.jbpm.taskmgmt.def.Task t "
//				+ "where t.processDefinition.id = :processDefinitionId "
//				+ "  and t.assignmentDelegation is not null");
//		query.setLong("processDefinitionId", processDefinitionId);
//		return (List<Task>)query.list();
//	}
	
	protected List<Job> findReferencedJobs(Long processDefinitionId) {
		List<Job> jobs = new ArrayList<Job>();
		Query q = session.createQuery(
				  "from org.jbpm.job.Timer t "
				+ "where t.action.processDefinition.id = :processDefinitionId ");
		q.setLong("processDefinitionId", processDefinitionId);
		jobs.addAll(q.list());
		q = session.createQuery(
				  "from org.jbpm.job.ExecuteActionJob ea "
				+ "where ea.action.processDefinition.id = :processDefinitionId ");
		q.setLong("processDefinitionId", processDefinitionId);
		jobs.addAll(q.list());
		return jobs;
	}
	
	protected List<Action> findReferencedActions(Long processDefinitionId) {
		List<Action> referencedActions = null;
		
		// Action -> ActionDelegation -> ProcessDefinition
		Query query = session.createQuery(
				  " select a "
				  + " from org.jbpm.graph.def.Action a, "
				  + "      org.jbpm.instantiation.Delegation d "
				  + "where d.processDefinition.id = :processDefinitionId "
				  + "  and a.actionDelegation.id = d.id");
		query.setLong("processDefinitionId", processDefinitionId);
		referencedActions = (List<Action>)query.list();
		
		// Action -> Event -> Transition / Node -> ProcessDefinition
//		query = session.createQuery(
//		  " select a "
//		  + " from org.jbpm.graph.def.Action a "
//		  + "where a.event.graphElement.processDefinition.id = :processDefinitionId");
//query.setLong("processDefinitionId", processDefinitionId);
		query = session.createSQLQuery(
				    "select	a.* "
				  + "  from	jbpm_action a, "
				  + "		jbpm_event e, "
				  + "		jbpm_task t"
				  + " where	a.EVENT_ = e.ID_ "
				  + "	and e.TYPE_ = 'A' "
				  + "	and e.GRAPHELEMENT_ = t.ID_ "
				  + "	and t.PROCESSDEFINITION_ = :processDefinitionId")
				.addEntity(Action.class)
				.setLong("processDefinitionId", processDefinitionId);
		referencedActions.addAll((List<Action>)query.list());
		
		query = session.createSQLQuery(
				    "select	a.* "
				  + "  from	jbpm_action a, "
				  + "		jbpm_event e, "
				  + "		jbpm_transition t"
				  + " where	a.EVENT_ = e.ID_ "
				  + "	and e.TYPE_ = 'T' "
				  + "	and e.GRAPHELEMENT_ = t.ID_ "
				  + "	and t.PROCESSDEFINITION_ = :processDefinitionId")
				.addEntity(Action.class)
				.setLong("processDefinitionId", processDefinitionId);
		referencedActions.addAll((List<Action>)query.list());
		
		query = session.createSQLQuery(
				    "select	a.* "
				  + "  from	jbpm_action a, "
				  + "		jbpm_event e "
//				  + "		jbpm_processDefinition p"
				  + " where	a.EVENT_ = e.ID_ "
				  + "	and e.TYPE_ = 'P' "
//				  + "	and e.GRAPHELEMENT_ = t.ID_ "
				  + "	and e.PROCESSDEFINITION_ = :processDefinitionId")
				.addEntity(Action.class)
				.setLong("processDefinitionId", processDefinitionId);
		referencedActions.addAll((List<Action>)query.list());
		
		query = session.createSQLQuery(
				    "select	a.* "
				  + "  from	jbpm_action a, "
				  + "		jbpm_event e, "
				  + "		jbpm_node n "
				  + " where	a.EVENT_ = e.ID_"
				  + "	and e.TYPE_ not in ('A', 'T', 'P') "
				  + "	and e.GRAPHELEMENT_ = n.ID_ "
				  + "	and n.PROCESSDEFINITION_ = :processDefinitionId")
				.addEntity(Action.class)
				.setLong("processDefinitionId", processDefinitionId);
		referencedActions.addAll((List<Action>)query.list());
		
		return referencedActions;
	}
	
	protected List<Action> findReferencingDelegatingActions(Long processDefinitionId) {
		Query query = session.createQuery(
				  " select a "
				  + " from org.jbpm.graph.def.Action a, "
				  + "      org.jbpm.instantiation.Delegation d "
				  + "where d.processDefinition.id = :processDefinitionId "
				  + "  and a.actionDelegation.id = d.id");
		query.setLong("processDefinitionId", processDefinitionId);
		return (List<Action>)query.list();
	}
	
//	protected List<ActionLog> findReferencingEventActions(ProcessDefinition processDefinition) {
//		Query query = session.createQuery(
//				  " select al "
//				  + " from org.jbpm.graph.log.ActionLog al "
//				  + "where al.action in ("
//				  + "		select a "
//				  + " 		  from org.jbpm.graph.def.Action a "
//				  + "		 where a.event in :events)");
//		query.setParameterList("events", processDefinition.getEvents().values());
//		return (List<ActionLog>)query.list();
//	}
	
	protected List<ActionLog> findReferencedActionLogs(List<Action> accions) {
		Query query = session.createQuery(
				  " select al "
				  + " from org.jbpm.graph.log.ActionLog al "
				  + "where al.action in (:accions)");
		query.setParameterList("accions", accions);
		return (List<ActionLog>)query.list();
	}
	
	private Action findReplacementEventAction(Action oldAction, ProcessDefinition newProcessDefinition) {
//		Query query = session.createQuery(
//				    " from org.jbpm.scheduler.def.CreateTimerAction ta "
//				  + "where ta.timerName = :timerName "
//				  + "  and ta.event.graphElement.name = :nodeName "
//				  + "  and ta.event.graphElement.processDefinition.id = :processDefinitionId)");
//		query.setLong("processDefinitionId", newProcessDefinition.getId());
//		List<Action> accions = (List<Action>)query.list();
//		return accions.isEmpty() ? null :  accions.get(0);		
		Boolean evalCreateTimer = false;
		Boolean evalCancelTimer = false;
		Boolean evalScript = false;
		
		CreateTimerAction oldCreateTimer = null;
		CancelTimerAction oldCancelTimer = null;
		Script oldScript = null;
		if (oldAction instanceof CreateTimerAction) {
			oldCreateTimer = (CreateTimerAction)oldAction;
			evalCreateTimer = oldCreateTimer.getTimerName() != null && !oldCreateTimer.getTimerName().isEmpty();
		} else if (oldAction instanceof CancelTimerAction) {
			oldCancelTimer = (CancelTimerAction)oldAction;
			evalCancelTimer = oldCancelTimer.getTimerName() != null && !oldCancelTimer.getTimerName().isEmpty();
		} else if (oldAction instanceof Script) {
			oldScript = (Script)oldAction;
			evalScript = oldScript.getExpression() != null && !oldScript.getExpression().isEmpty();
		}
		
		List<Action> novesAccions = findReferencedActions(newProcessDefinition.getId());
		for (Action novaAccio: novesAccions) {
			if (oldAction.getName() != null && !oldAction.getName().isEmpty()) {
				if (oldAction.getName().equals(novaAccio.getName()))
					return novaAccio;
			} else {
				if (evalCreateTimer) {
					if (novaAccio instanceof CreateTimerAction) {
						CreateTimerAction ncta = (CreateTimerAction)novaAccio;
						if (oldCreateTimer.getTimerName().equals(ncta.getTimerName()))
							return novaAccio;
					}
				} else if (evalCancelTimer) {
					if (novaAccio instanceof CancelTimerAction) {
						CancelTimerAction ncta = (CancelTimerAction)novaAccio;
						if (oldCancelTimer.getTimerName().equals(ncta.getTimerName()))
							return novaAccio;
					}
				} else if (evalScript) {
					if (novaAccio instanceof Script) {
						Script ns = (Script)novaAccio;
						if (oldScript.getExpression().equals(ns.getExpression()))
							return novaAccio;
					}
				}
			}
		}
//		String nom = oldCreateTimer != null ? 	oldCreateTimer.getTimerName() : 
//												oldCancelTimer != null ? 	oldCancelTimer.getTimerName() :
//																			oldScript != null ? oldScript.getExpression() : oldAction.getName();
//		throw new JbpmException("Action ='" + nom + "' (" + oldAction.getId() + ")" + " not found in new process definition");
		return null;
	}
	
	protected List<TaskController> findReferencedTaskController(Long processDefinitionId) {
		Query query = session.createQuery(
				  " select tc "
				  + " from org.jbpm.taskmgmt.def.TaskController tc, "
				  + "    org.jbpm.instantiation.Delegation d "
				  + "where d.processDefinition.id = :processDefinitionId "
				  + "  and tc.taskControllerDelegation.id = d.id");
		query.setLong("processDefinitionId", processDefinitionId);
		return (List<TaskController>)query.list();
	}
	
//	protected List<Task> findReferencingTaskControllerTasks(Long processDefinitionId, List<TaskController> taskControllers) {
//		Query query = session.createQuery(
//				  " from org.jbpm.taskmgmt.def.Task t "
//				+ "where t.processDefinition.id = :processDefinitionId "
//				+ "  and t.taskController in :taskController");
//		query.setLong("processDefinitionId", processDefinitionId);
//		query.setParameterList("taskControllers", taskControllers);
//		return (List<Task>)query.list();
//	}
	
	protected List<Delegation> findReferencedDelegations(Long processDefinitionId) {
		Query query = session.createQuery(
				  " from org.jbpm.instantiation.Delegation d "
				+ "where d.processDefinition.id = :processDefinitionId");
		query.setLong("processDefinitionId", processDefinitionId);
		return (List<Delegation>)query.list();
	}
	
	protected List<CompositeLog> findReferencedTransitionLogs(Long processDefinitionId) {
		
//		Query query = session.createSQLQuery(
//				  	"select l.* "
//				  + " from 	jbpm_log l, "
//				  + "		jbpm_transition t "
//				  + " where	l.TRANSITION_ = t.ID_ "
//				  + "   and	t.PROCESSDEFINITION_ = :processDefinitionId")
//				.addEntity(TransitionLog.class)
		List<CompositeLog> logs = null;
		Query query = session.createQuery(
				"select tl " +
				"  from org.jbpm.graph.log.TransitionLog as tl " +
				" where tl.transition.processDefinition.id = :processDefinitionId")
				.setLong("processDefinitionId", processDefinitionId);
		logs = (List<CompositeLog>)query.list();
		
		query = session.createQuery(
				"select sl " +
				"  from org.jbpm.graph.log.SignalLog as sl " +
				" where sl.transition.processDefinition.id = :processDefinitionId")
				.setLong("processDefinitionId", processDefinitionId);
		logs.addAll((List<CompositeLog>)query.list());
		
		return logs;
	}
	
	private Transition findReplacementTransition(ProcessDefinition newDef, Transition oldTransition) {
		Transition newTransition = null;
		Node newNodeFrom = findReplacementNode(newDef, oldTransition.getFrom());
		if (newNodeFrom != null) {
			Node newNodeTo = findReplacementNode(newDef, oldTransition.getTo());
			if (newNodeTo != null) {
				Query q = session.createQuery(
						"from org.jbpm.graph.def.Transition t "
						+ "where t.from = :nodeFrom "
						+ "  and t.to = :nodeTo");
				q.setEntity("nodeFrom", newNodeFrom);
				q.setEntity("nodeTo", newNodeTo);

				List<Transition> transicions = (List<Transition>)q.list();
				if (!transicions.isEmpty()) {
					if (transicions.size() == 1) 
						newTransition = transicions.get(0);
					else {
						for (Transition t: transicions) {
							if (oldTransition.getName() == null) {
								if (t.getName() == null)
									newTransition = t;
							} else if (t.getName() != null && t.getName().equals(oldTransition.getName())) {
								newTransition = t;
								break;
							}
						}
					}
				}
			}
		}
		
//		if (newTransition == null) {
//			throw new JbpmException("Transition for nodes from='" + oldTransition.getFrom().getName() + 
//					"' and to='" + oldTransition.getTo().getName() + "' not found in new process definition");
//		}
		return newTransition;
	}
	
	private Task findReplacementTask(ProcessDefinition newDef, Node oldNode, Task oldTask) {
		Task newTask = null;
		String replacementTaskName = oldTask.getName();
		Node newTaskNode = findReplacementNode(newDef, oldNode);

		if (newTaskNode != null) {
			Query q = session.getNamedQuery("TaskMgmtSession.findTaskForNode");
			q.setString("taskName", replacementTaskName);
			q.setLong("taskNodeId", newTaskNode.getId());

			newTask = (Task) q.uniqueResult();
		}
		return newTask;
	}
	
	private Node findReplacementNode(ProcessDefinition newDef, GraphElement oldNode) {
		String name = getReplacementNodeName(oldNode);
		log.debug("get replacement for node with name '" + name + "'");
		// Node newNode = newDef.findNode(name);
		Node newNode = ChangeProcessInstanceVersionCommand.findNode(newDef, name);
		return newNode;
	}
	
	private String getReplacementNodeName(GraphElement oldNode) {
		return (oldNode instanceof Node ? ((Node) oldNode).getFullyQualifiedName() : oldNode.getName());
	}
//	CODI DE LA FUNCIONALITAT DE BORRAT DE DEFINICIONS DE PROCÉS -- Fi
	
	// process instances
	// ////////////////////////////////////////////////////////

	/**
	 * @deprecated use {@link org.jbpm.JbpmContext#save(ProcessInstance)}
	 *             instead.
	 * @throws UnsupportedOperationException
	 */
	public void saveProcessInstance(ProcessInstance processInstance) {
		throw new UnsupportedOperationException("use JbpmContext.save(ProcessInstance) instead");
	}

	/**
	 * loads a process instance from the database by the identifier. This throws
	 * an exception in case the process instance doesn't exist.
	 * 
	 * @see #getProcessInstance(long)
	 * @throws JbpmException
	 *             in case the process instance doesn't exist.
	 */
	public ProcessInstance loadProcessInstance(long processInstanceId) {
		try {
			ProcessInstance processInstance = (ProcessInstance) session.load(ProcessInstance.class,
					new Long(processInstanceId));
			return processInstance;
		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException("couldn't load process instance '" + processInstanceId + "'", e);
		}
	}

	/**
	 * gets a process instance from the database by the identifier. This method
	 * returns null in case the given process instance doesn't exist.
	 */
	public ProcessInstance getProcessInstance(long processInstanceId) {
		try {
			ProcessInstance processInstance = (ProcessInstance) session.get(ProcessInstance.class,
					new Long(processInstanceId));
			return processInstance;
		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException("couldn't get process instance '" + processInstanceId + "'", e);
		}
	}

	/**
	 * loads a token from the database by the identifier.
	 * 
	 * @return the token.
	 * @throws JbpmException
	 *             in case the referenced token doesn't exist.
	 */
	public Token loadToken(long tokenId) {
		try {
			Token token = (Token) session.load(Token.class, new Long(tokenId));
			return token;
		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException("couldn't load token '" + tokenId + "'", e);
		}
	}

	/**
	 * gets a token from the database by the identifier.
	 * 
	 * @return the token or null in case the token doesn't exist.
	 */
	public Token getToken(long tokenId) {
		try {
			Token token = (Token) session.get(Token.class, new Long(tokenId));
			return token;
		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException("couldn't get token '" + tokenId + "'", e);
		}
	}

	/**
	 * locks a process instance in the database.
	 */
	public void lockProcessInstance(long processInstanceId) {
		lockProcessInstance(loadProcessInstance(processInstanceId));
	}

	/**
	 * locks a process instance in the database.
	 */
	public void lockProcessInstance(ProcessInstance processInstance) {
		try {
			session.lock(processInstance, LockMode.UPGRADE);
		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException("couldn't lock process instance '" + processInstance.getId() + "'", e);
		}
	}

	/**
	 * fetches all processInstances for the given process definition from the
	 * database. The returned list of process instances is sorted start date,
	 * youngest first.
	 */
	public List findProcessInstances(long processDefinitionId) {
		List processInstances = null;
		try {
			Query query = session.getNamedQuery("GraphSession.findAllProcessInstancesForADefinition");
			query.setLong("processDefinitionId", processDefinitionId);
			processInstances = query.list();

		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException(
					"couldn't load process instances for process definition '" + processDefinitionId + "'", e);
		}
		return processInstances;
	}

	public void deleteProcessInstance(long processInstanceId) {
		deleteProcessInstance(loadProcessInstance(processInstanceId));
	}

	public void deleteProcessInstance(ProcessInstance processInstance) {
		deleteProcessInstance(processInstance, true, true);
	}

	public void deleteProcessInstance(ProcessInstance processInstance, boolean includeTasks, boolean includeJobs) {
		if (processInstance == null)
			throw new JbpmException("processInstance is null in JbpmSession.deleteProcessInstance()");
		log.debug("deleting process instance " + processInstance.getId());

		try {
			// jobs
			if (includeJobs) {
				log.debug("deleting jobs for process instance " + processInstance.getId());
				Query query = session.getNamedQuery("GraphSession.deleteJobsForProcessInstance");
				query.setEntity("processInstance", processInstance);
				query.executeUpdate();
			}

			// tasks
			if (includeTasks) {
				Query query = session.getNamedQuery("GraphSession.findTaskInstanceIdsForProcessInstance");
				query.setEntity("processInstance", processInstance);
				List taskInstanceIds = query.list();

				if ((taskInstanceIds != null) && (!taskInstanceIds.isEmpty())) {
					log.debug("deleting tasks " + taskInstanceIds + " for process instance " + processInstance.getId());
					query = session.getNamedQuery("GraphSession.deleteTaskInstancesById");
					query.setParameterList("taskInstanceIds", taskInstanceIds);
				}
			}

			// delete the logs
			log.debug("deleting logs for process instance " + processInstance.getId());
			deleteLogs(processInstance);

			List<Token> tokens = new ArrayList<Token>();

			Query qTok = session
					.createQuery("select token from org.jbpm.graph.exe.Token token where token.processInstance.id=?")
					.setLong(0, processInstance.getId());
			tokens.addAll(qTok.list());

			// delete the tokens and subprocess instances
			log.debug("deleting subprocesses for process instance " + processInstance.getId());
			deleteSubProcesses(processInstance.getRootToken());

			// null out the parent process token
			Token superProcessToken = processInstance.getSuperProcessToken();
			if (superProcessToken != null) {
				log.debug("nulling property subProcessInstance in superProcessToken " + superProcessToken.getId()
				+ " which is referencing the process instance " + processInstance.getId()
				+ " which is being deleted");
				superProcessToken.setSubProcessInstance(null);
			}

			for (Token token : tokens) {
				token.setProcessInstance(null);
				session.delete(token);
			}
			// add the process instance
			log.debug("hibernate session delete for process instance " + processInstance.getId());
			session.delete(processInstance);
		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException("couldn't delete process instance '" + processInstance.getId() + "'", e);
		}
	}

	void deleteLogs(ProcessInstance processInstance) {
		Query query = session.getNamedQuery("GraphSession.findLogsForProcessInstance");
		query.setEntity("processInstance", processInstance);
		List logs = query.list();
		Iterator iter = logs.iterator();
		while (iter.hasNext()) {
			ProcessLog processLog = (ProcessLog) iter.next();
			session.delete(processLog);
		}
	}

	void deleteSubProcesses(Token token) {
		if (token != null) {
			Query query = session.getNamedQuery("GraphSession.findSubProcessInstances");
			query.setEntity("processInstance", token.getProcessInstance());
			List processInstances = query.list();

			if (processInstances == null || processInstances.isEmpty()) {
				log.debug("no subprocesses to delete for token " + token.getId());
				return;
			}

			Iterator iter = processInstances.iterator();
			while (iter.hasNext()) {
				ProcessInstance subProcessInstance = (ProcessInstance) iter.next();
				subProcessInstance.getSuperProcessToken().setSubProcessInstance(null);
				subProcessInstance.setSuperProcessToken(null);
				token.setSubProcessInstance(null);
				log.debug("deleting sub process " + subProcessInstance.getId());
				deleteProcessInstance(subProcessInstance);
			}
		}
	}

	public static class AverageNodeTimeEntry {
		private long nodeId;
		private String nodeName;
		private int count;
		private long averageDuration;
		private long minDuration;
		private long maxDuration;

		public long getNodeId() {
			return nodeId;
		}

		public void setNodeId(final long nodeId) {
			this.nodeId = nodeId;
		}

		public String getNodeName() {
			return nodeName;
		}

		public void setNodeName(final String nodeName) {
			this.nodeName = nodeName;
		}

		public int getCount() {
			return count;
		}

		public void setCount(final int count) {
			this.count = count;
		}

		public long getAverageDuration() {
			return averageDuration;
		}

		public void setAverageDuration(final long averageDuration) {
			this.averageDuration = averageDuration;
		}

		public long getMinDuration() {
			return minDuration;
		}

		public void setMinDuration(final long minDuration) {
			this.minDuration = minDuration;
		}

		public long getMaxDuration() {
			return maxDuration;
		}

		public void setMaxDuration(final long maxDuration) {
			this.maxDuration = maxDuration;
		}
	}

	public List calculateAverageTimeByNode(final long processDefinitionId, final long minumumDurationMillis) {
		List results = null;
		try {
			Query query = session.getNamedQuery("GraphSession.calculateAverageTimeByNode");
			query.setLong("processDefinitionId", processDefinitionId);
			query.setDouble("minimumDuration", minumumDurationMillis);
			List listResults = query.list();

			if (listResults != null) {
				results = new ArrayList();
				Iterator iter = listResults.iterator();
				while (iter.hasNext()) {
					Object[] values = (Object[]) iter.next();

					AverageNodeTimeEntry averageNodeTimeEntry = new AverageNodeTimeEntry();
					averageNodeTimeEntry.setNodeId(((Number) values[0]).longValue());
					averageNodeTimeEntry.setNodeName((String) values[1]);
					averageNodeTimeEntry.setCount(((Number) values[2]).intValue());
					averageNodeTimeEntry.setAverageDuration(((Number) values[3]).longValue());
					averageNodeTimeEntry.setMinDuration(((Number) values[4]).longValue());
					averageNodeTimeEntry.setMaxDuration(((Number) values[5]).longValue());

					results.add(averageNodeTimeEntry);
				}
			}
		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException(
					"couldn't load process instances for process definition '" + processDefinitionId + "'", e);
		}
		return results;
	}

	public List findActiveNodesByProcessInstance(ProcessInstance processInstance) {
		List results = null;
		try {
			Query query = session.getNamedQuery("GraphSession.findActiveNodesByProcessInstance");
			query.setEntity("processInstance", processInstance);
			results = query.list();

		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException("couldn't active nodes for process instance '" + processInstance + "'", e);
		}
		return results;
	}

	public ProcessInstance getProcessInstance(ProcessDefinition processDefinition, String key) {
		ProcessInstance processInstance = null;
		try {
			Query query = session.getNamedQuery("GraphSession.findProcessInstanceByKey");
			query.setEntity("processDefinition", processDefinition);
			query.setString("key", key);
			processInstance = (ProcessInstance) query.uniqueResult();

		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException("couldn't get process instance with key '" + key + "'", e);
		}
		return processInstance;
	}

	public ProcessInstance loadProcessInstance(ProcessDefinition processDefinition, String key) {
		ProcessInstance processInstance = null;
		try {
			Query query = session.getNamedQuery("GraphSession.findProcessInstanceByKey");
			query.setEntity("processDefinition", processDefinition);
			query.setString("key", key);
			processInstance = (ProcessInstance) query.uniqueResult();
			if (processInstance == null) {
				throw new JbpmException("no process instance was found with key " + key);
			}

		} catch (Exception e) {
			log.error(e);
			jbpmSession.handleException();
			throw new JbpmException("couldn't load process instance with key '" + key + "'", e);
		}
		return processInstance;
	}

	private static final Log log = LogFactory.getLog(GraphSession.class);
}
