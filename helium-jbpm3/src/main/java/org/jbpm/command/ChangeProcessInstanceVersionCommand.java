package org.jbpm.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.jbpm.JbpmException;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.GraphElement;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.NodeCollection;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.SuperState;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.log.ActionLog;
import org.jbpm.graph.log.NodeLog;
import org.jbpm.graph.log.SignalLog;
import org.jbpm.graph.log.TransitionLog;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.job.Job;
import org.jbpm.job.Timer;
import org.jbpm.logging.log.ProcessLog;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;

import net.conselldemallorca.helium.v3.core.api.exception.ChangeLogException;

/**
 * Modificat per Límit per evitar el problema de nodes amb el nom que conté un
 * caràcter '/'.
 * 
 * <b>THIS COMMAND IS NOT YET STABLE, BUT FEEL FREE TO TEST :-)</b><br>
 * 
 * Status update: Still not complete, but refactored and added simple test cases: 
 * {@link ChangeProcessInstanceVersionCommandTest}.<br>
 * 
 * Change the version of a running process instance. 
 * 
 * This works only, if the current node is also available in the new
 * version of the process definition or a name mapping has to be provided.<br> 
 * 
 * <b>Currently known limitations:</b>
 * <ul>
 *   <li> {@link Task}s cannot move "into" another node. If an active
 *        {@link TaskInstance} exists, the {@link Task} definition must exist in the 
 *        {@link TaskNode} with the same (or mapped) name. Otherwise the right node 
 *        cannot be found easily because it may be ambiguous.</li>
 *   <li> Sub processes aren't yet tested. Since the {@link ProcessState} is 
 *        a {@link Node} like any other, it should work anyway.</li>
 *   <li> Can have <b>negative impact on referential integrity</b>! Because
 *        one {@link ProcessInstance} can have {@link ProcessLog}s point to
 *        old {@link ProcessDefinition}s. Hence, delete a {@link ProcessDefinition}
 *        may not work and throw an Exception (Integrity constraint violation)</li>  
 *   <li> In combination with ESB the ESB uses {@link Token}.id <b>and</b> {@link Node}.id
 *        as correlation identifier. After changing the version of a {@link ProcessInstance}
 *        the {@link Node}.id has changed, so a signal from ESB will result in an exception
 *        and has to be corrected manually.</li>
 * </ul>
 * 
 * @author Bernd Ruecker (bernd.ruecker@camunda.com)
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ChangeProcessInstanceVersionCommand extends AbstractProcessInstanceBaseCommand {

	private static final long serialVersionUID = 2277080393930008224L;

	/**
	 * new version of process, if <=0, the latest process definition is used
	 */
	private int newVersion = -1;

	private static final Log log = LogFactory.getLog(ChangeProcessInstanceVersionCommand.class);

	/**
	 * the map configures for every node-name in the old process definition (as
	 * key) which node-name to use in the new process definition.
	 * 
	 * if a node is not mentioned in this Map, old node name = new node name is
	 * applied
	 */
	private Map<String, String> nodeNameMapping = new HashMap<String, String>();

	private Map<String, String> taskNameMapping = new HashMap<String, String>();

	public ChangeProcessInstanceVersionCommand() {
	}

	public ChangeProcessInstanceVersionCommand(long processId, int newVersion) {
		super();
		super.setProcessInstanceId(processId);
		this.newVersion = newVersion;
	}

	public String getAdditionalToStringInformation() {
		return ";newVersion=" + newVersion;
	}

	private ProcessDefinition loadNewProcessDefinition(String processName) {
		if (newVersion <= 0)
			return getJbpmContext().getGraphSession().findLatestProcessDefinition(processName);
		else
			return getJbpmContext().getGraphSession().findProcessDefinition(processName, newVersion);
	}

	public ProcessInstance execute(ProcessInstance pi) {
		ProcessDefinition oldDef = pi.getProcessDefinition();
		ProcessDefinition newDef = loadNewProcessDefinition(oldDef.getName());

		if (newDef == null) {
			throw new JbpmException(
					"Process definition " + oldDef.getName() + " in version " + newVersion + " not found.");
		}

		log.debug("Start changing process id " + pi.getId() + " from version " + pi.getProcessDefinition().getVersion()
				+ " to new version " + newDef.getVersion());
		pi.setProcessDefinition(newDef);

		changeTokenVersion(pi.getRootToken());

		log.debug("process id " + pi.getId() + " changed to version " + pi.getProcessDefinition().getVersion());
		return pi;
	}

	private ProcessDefinition getNewProcessDefinition(Token t) {
		return t.getProcessInstance().getProcessDefinition();
	}

	private void changeTokenVersion(Token token) {
		ProcessDefinition newDef = getNewProcessDefinition(token);
		log.debug("change token id " + token.getId() + " to new version " + newDef.getVersion());

		// change node reference on token (current node)
		Node oldNode = token.getNode();
		Node newNode = findReplacementNode(newDef, oldNode);
		token.setNode(newNode);

		// Change timers too!
		adjustTimersForToken(token);

		// change tasks
		adjustTaskInstancesForToken(token);

		// change children recursively
		if (token.getChildren() != null) {
			Iterator<Token> tokenIter = token.getChildren().values().iterator();
			while (tokenIter.hasNext()) {
				changeTokenVersion(tokenIter.next());
			}
		}
		
		try {
			// change log references
			adjustLogsForToken(newDef, token);
		} catch (JbpmException e) {
			throw new ChangeLogException(e);
		}
	}
	
	private void adjustLogsForToken(ProcessDefinition newDef, Token token) {
		Map<Long, Transition> transicions = new HashMap<Long, Transition>();
		List<TransitionLog> transiotionLogs = getTransitionLogs(token);
		for (TransitionLog log : transiotionLogs) {
			if (log.getTransition() != null) {
				Transition oldTransition = log.getTransition();
				Transition newTransition = transicions.get(oldTransition.getId());
				if (newTransition == null) {
					newTransition = findReplacementTransition(newDef, oldTransition);
					transicions.put(oldTransition.getId(), newTransition);
				}
				setLogTransition(log, newTransition);
			}
		}
		List<SignalLog> signalLogs = getSignalLogs(token);
		for (SignalLog log : signalLogs) {
			if (log.getTransition() != null) {
				Transition oldTransition = log.getTransition();
				Transition newTransition = transicions.get(oldTransition.getId());
				if (newTransition == null) {
					newTransition = findReplacementTransition(newDef, oldTransition);
					transicions.put(oldTransition.getId(), newTransition);
				}
				setLogSignal(log, newTransition);
			}
		}
		List<NodeLog> nodeLogs = getNodeLogs(token);
		for (NodeLog log : nodeLogs) {
			if (log.getNode() != null) {
				Node oldNode = log.getNode();
				Node newNode = findReplacementNode(newDef, oldNode);
				setLogNode(log, newNode);
			}
		}
		List<ActionLog> actionLogs = getActionLogs(token);
		for (ActionLog log : actionLogs) {
			if (log.getAction() != null) {
				Action oldAction = log.getAction();
				if (oldAction.getProcessDefinition() != null) {
					Action newAction = findReplacementAction(newDef, oldAction);
					log.setAction(newAction);
				}
			}
		}
	}
	
	private List<TransitionLog> getTransitionLogs(Token t) {
		Query q = getJbpmContext().getSession().createQuery(
				"select tl " +
				"from org.jbpm.graph.log.TransitionLog as tl " +
				"where tl.token.processInstance = :processInstance");
		q.setEntity("processInstance", t.getProcessInstance());
		return q.list();
	}
	
	private List<SignalLog> getSignalLogs(Token t) {
		Query q = getJbpmContext().getSession().createQuery(
				"select sl " +
				"from org.jbpm.graph.log.SignalLog as sl " +
				"where sl.token.processInstance = :processInstance");
		q.setEntity("processInstance", t.getProcessInstance());
		return q.list();
	}
	
	private List<NodeLog> getNodeLogs(Token t) {
		Query q = getJbpmContext().getSession().createQuery(
				"select nl " +
				"from org.jbpm.graph.log.NodeLog as nl " +
				"where nl.token.processInstance = :processInstance");
		q.setEntity("processInstance", t.getProcessInstance());
		return q.list();
	}
	
	private List<ActionLog> getActionLogs(Token t) {
		Query q = getJbpmContext().getSession().createQuery(
				"select al " +
				"from org.jbpm.graph.log.ActionLog as al " +
				"where al.token.processInstance = :processInstance");
		q.setEntity("processInstance", t.getProcessInstance());
		return q.list();
	}
	 
	private void setLogTransition(TransitionLog log, Transition transition) {
		Query updateQuery = getJbpmContext().getSession().createQuery(
				"update org.jbpm.graph.log.TransitionLog set "
				+ "transition = :transition, "
				+ "sourceNode = :nodeFrom, "
				+ "destinationNode = :nodeTo "
				+ "where id = :logId");
		updateQuery.setEntity("transition", transition);
		updateQuery.setEntity("nodeFrom", transition.getFrom());
		updateQuery.setEntity("nodeTo", transition.getTo());
		updateQuery.setLong("logId", log.getId());
		updateQuery.executeUpdate();
	}
	
	private void setLogSignal(SignalLog log, Transition transition) {
		Query updateQuery = getJbpmContext().getSession().createQuery(
				"update org.jbpm.graph.log.SignalLog set "
				+ "transition = :transition "
				+ "where id = :logId");
		updateQuery.setEntity("transition", transition);
		updateQuery.setLong("logId", log.getId());
		updateQuery.executeUpdate();
	}

	private void setLogNode(NodeLog log, Node node) {
		Query updateQuery = getJbpmContext().getSession().createQuery(
				"update org.jbpm.graph.log.NodeLog set "
				+ "node = :node "
				+ "where id = :logId");
		updateQuery.setEntity("node", node);
		updateQuery.setLong("logId", log.getId());
		updateQuery.executeUpdate();
	}

	private void adjustTaskInstancesForToken(Token token) {
		ProcessDefinition newDef = getNewProcessDefinition(token);
		Iterator<TaskInstance> iter = getTasksForToken(token).iterator();
		while (iter.hasNext()) {
			TaskInstance ti = iter.next();

			// find new task
			Task oldTask = ti.getTask();
			Node oldNode = oldTask.getTaskNode();

			Task newTask = findReplacementTask(newDef, oldNode, oldTask);
			ti.setTask(newTask);
			// Canviam el TaskMngmtInstance per a associar-lo a la nova definició de procés
			ti.getTaskMgmtInstance().setTaskMgmtDefinition(newDef.getTaskMgmtDefinition());

			log.debug("change dependent task-instance with id " + oldTask.getId());
			
//			// Canviam el processDefinition dels tasksMgmtInstance dels tasksInstance de la versió que s'intenta esborrar a la de la tasca.
//			TaskMgmtDefinition tmd = ti.getTaskMgmtInstance().getTaskMgmtDefinition();
//			tmd.setProcessDefinition(newDef);
//			if (tmd.getStartTask() != null) {
//				tmd.setStartTask(newDef.getTaskMgmtDefinition().getStartTask());
//			}
//			getJbpmContext().getSession().save(tmd);
		}
		
	}

	private void adjustTimersForToken(Token token) {
		ProcessDefinition newDef = getNewProcessDefinition(token);
		List<Job> jobs = getJbpmContext().getJobSession().findJobsByToken(token);
		for (Job job : jobs) {
			if (job instanceof Timer) {
				// check all timers if connected to a GraphElement
				Timer timer = (Timer) job;
				if (timer.getGraphElement() != null) {

					// and change the reference (take name mappings into
					// account!)
					if (timer.getGraphElement() instanceof Task) {
						// change to new task definition
						Task oldTask = (Task) timer.getGraphElement();
						TaskNode oldNode = oldTask.getTaskNode();
						timer.setGraphElement(findReplacementTask(newDef, oldNode, oldTask));
					} else {
						// change to new node
						GraphElement oldNode = timer.getGraphElement();
						// TODO: What with other GraphElements?
						timer.setGraphElement(findReplacementNode(newDef, oldNode));
					}
				}
			}
		}
	}

	private Node findReplacementNode(ProcessDefinition newDef, GraphElement oldNode) {
		String name = getReplacementNodeName(oldNode);
		log.debug("get replacement for node with name '" + name + "'");
		// Node newNode = newDef.findNode(name);
		Node newNode = findNode(newDef, name);
		if (newNode == null) {
			throw new JbpmException("node with name '" + name + "' not found in new process definition");
		}
		return newNode;
	}

	private Task findReplacementTask(ProcessDefinition newDef, Node oldNode, Task oldTask) {
		String replacementTaskName = getReplacementTaskName(oldTask);
		Node newTaskNode = findReplacementNode(newDef, oldNode);

		Query q = getJbpmContext().getSession().getNamedQuery("TaskMgmtSession.findTaskForNode");
		q.setString("taskName", replacementTaskName);
		q.setLong("taskNodeId", newTaskNode.getId());

		Task newTask = (Task) q.uniqueResult();
		if (newTask == null) {
			throw new JbpmException("Task '" + replacementTaskName + "' for node '" + newTaskNode.getName()
					+ "' not found in new process definition");
		}
		return newTask;
	}
	
	private Transition findReplacementTransition(ProcessDefinition newDef, Transition oldTransition) {
		Transition newTransition = null;
		Node newNodeFrom = findReplacementNode(newDef, oldTransition.getFrom());
		if (newNodeFrom != null) {
			Node newNodeTo = findReplacementNode(newDef, oldTransition.getTo());
			if (newNodeTo != null) {
				Query q = getJbpmContext().getSession().createQuery(
						"from org.jbpm.graph.def.Transition t "
						+ "where t.from = :nodeFrom "
						+ "  and t.to = :nodeTo");
				q.setEntity("nodeFrom", newNodeFrom);
				q.setEntity("nodeTo", newNodeTo);

				newTransition = (Transition) q.uniqueResult();
			}
		}
		
		if (newTransition == null) {
			throw new JbpmException("Transition for nodes from='" + oldTransition.getFrom().getName() + 
					"' and to='" + oldTransition.getTo().getName() + "' not found in new process definition");
		}
		return newTransition;
	}

	private Action findReplacementAction(ProcessDefinition newDef, Action oldAction) {
		Action newAction = null;
		
		if (oldAction.getName() != null) {
			newAction = newDef.getAction(oldAction.getName());
		}
		
		if (newAction == null) {
			throw new JbpmException("Action '" + oldAction.getName() + "' not found in new process definition");
		}
		return newAction;
	}

	/**
	 * @param oldNode
	 * @return the name of the new node (given in the map or return default
	 *         value, which is the old node name)
	 */
	private String getReplacementNodeName(GraphElement oldNode) {
		String oldName = (oldNode instanceof Node ? ((Node) oldNode).getFullyQualifiedName() : oldNode.getName());
		if (nodeNameMapping.containsKey(oldName)) {
			return (String) nodeNameMapping.get(oldName);
		}
		// return new node name = old node name as default
		return oldName;
	}

	private String getReplacementTaskName(Task oldTask) {
		String oldName = oldTask.getName();
		if (taskNameMapping.containsKey(oldName)) {
			return (String) taskNameMapping.get(oldName);
		}
		// return new node name = old node name as default
		return oldName;
	}

	public static Node findNode(NodeCollection nodeCollection, String hierarchicalName) {
		Node node = null;
		if ((hierarchicalName != null) && (!"".equals(hierarchicalName.trim()))) {

			// Afegit per evitar el problema de nodes amb el nom que
			// conté un caràcter '/'.
			node = nodeCollection.getNode(hierarchicalName);
			if (node != null) {
				return node;
			}
			//

			if ((hierarchicalName.startsWith("/")) && (nodeCollection instanceof SuperState)) {
				nodeCollection = ((SuperState) nodeCollection).getProcessDefinition();
			}

			StringTokenizer tokenizer = new StringTokenizer(hierarchicalName, "/");
			while (tokenizer.hasMoreElements()) {
				String namePart = tokenizer.nextToken();
				if ("..".equals(namePart)) {
					if (nodeCollection instanceof ProcessDefinition) {
						throw new JbpmException("couldn't find node '" + hierarchicalName
								+ "' because of a '..' on the process definition.");
					}
					nodeCollection = (NodeCollection) ((GraphElement) nodeCollection).getParent();
				} else if (tokenizer.hasMoreElements()) {
					nodeCollection = (NodeCollection) nodeCollection.getNode(namePart);
				} else {
					node = nodeCollection.getNode(namePart);
				}
			}
		}
		return node;
	}

	/**
	 * We may still have open tasks, even though their parent tokens have been
	 * ended. So we'll simply get all tasks from this process instance and
	 * cancel them if they are still active.
	 * 
	 */
	private List getTasksForToken(Token token) {
		Query query = getJbpmContext().getSession().getNamedQuery("TaskMgmtSession.findTaskInstancesByTokenId");
		query.setLong("tokenId", token.getId());
		return query.list();

	}

	public Map getNodeNameMapping() {
		return nodeNameMapping;
	}

	public void setNodeNameMapping(Map<String, String> nameMapping) {
		if (nameMapping == null) {
			this.nodeNameMapping = new HashMap<String, String>();
		} else {
			this.nodeNameMapping = nameMapping;
		}
	}

	public int getNewVersion() {
		return newVersion;
	}

	public void setNewVersion(int newVersion) {
		this.newVersion = newVersion;
	}

	public Map getTaskNameMapping() {
		return taskNameMapping;
	}

	public void setTaskNameMapping(Map<String, String> nameMapping) {
		if (nameMapping == null) {
			this.taskNameMapping = new HashMap<String, String>();
		} else {
			this.taskNameMapping = nameMapping;
		}
	}

	/**
	 * @deprecated use getProcessInstanceId instead
	 */
	public long getProcessId() {
		if (getProcessInstanceIds() != null && getProcessInstanceIds().length > 0)
			return getProcessInstanceIds()[0];
		else
			return 0;
	}

	/**
	 * @deprecated use setProcessInstanceId instead
	 */
	public void setProcessId(long processId) {
		super.setProcessInstanceId(processId);
	}

	/**
	 * @deprecated use getNodeNameMapping instead
	 */
	public Map getNameMapping() {
		return getNodeNameMapping();
	}

	/**
	 * @deprecated use setNodeNameMapping instead
	 */
	public void setNameMapping(Map nameMapping) {
		setNodeNameMapping(nameMapping);
	}

	// methods for fluent programming

	public ChangeProcessInstanceVersionCommand nodeNameMapping(Map<String, String> nameMapping) {
		setNodeNameMapping(nameMapping);
		return this;
	}

	public ChangeProcessInstanceVersionCommand newVersion(int newVersion) {
		setNewVersion(newVersion);
		return this;
	}

	public ChangeProcessInstanceVersionCommand taskNameMapping(Map<String, String> nameMapping) {
		setTaskNameMapping(nameMapping);
		return this;
	}

	public ChangeProcessInstanceVersionCommand nodeNameMappingAdd(String oldNodeName, String newNodeName) {
		if (nodeNameMapping == null) {
			this.nodeNameMapping = new HashMap<String, String>();
		}

		this.nodeNameMapping.put(oldNodeName, newNodeName);
		return this;
	}

	public ChangeProcessInstanceVersionCommand taskNameMappingAdd(String oldTaskName, String newNodeName) {
		if (taskNameMapping == null) {
			this.taskNameMapping = new HashMap<String, String>();
		}

		this.taskNameMapping.put(oldTaskName, newNodeName);
		return this;
	}
}
