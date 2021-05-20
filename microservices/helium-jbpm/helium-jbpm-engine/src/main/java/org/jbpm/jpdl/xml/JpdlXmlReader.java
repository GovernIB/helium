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
package org.jbpm.jpdl.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jbpm.JbpmConfiguration;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.action.ActionTypes;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.ExceptionHandler;
import org.jbpm.graph.def.GraphElement;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.NodeCollection;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.NodeTypes;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.instantiation.Delegation;
import org.jbpm.jpdl.JpdlException;
import org.jbpm.mail.Mail;
import org.jbpm.scheduler.def.CancelTimerAction;
import org.jbpm.scheduler.def.CreateTimerAction;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.def.TaskMgmtDefinition;
import org.xml.sax.InputSource;

@SuppressWarnings({"unchecked", "rawtypes"})
public class JpdlXmlReader implements ProblemListener {
  
  private static final long serialVersionUID = 1L;

  protected InputSource inputSource = null;
  protected List problems = new ArrayList();
  protected ProblemListener problemListener = null;
  protected ProcessDefinition processDefinition = null;
  protected String initialNodeName = null;
  protected Collection unresolvedTransitionDestinations = null;
  protected Collection unresolvedActionReferences = null;

  /**
   * the parsed process definition as DOM tree (available after readProcessDefinition)
   */
  protected Document document;

  /** for autonumbering anonymous timers. */
  private int timerNumber;

  public JpdlXmlReader(InputSource inputSource) {
    this.inputSource = inputSource;
  }
  
  public JpdlXmlReader(InputSource inputSource, ProblemListener problemListener) {
    this.inputSource = inputSource;
    this.problemListener = problemListener;
  }
  
  public JpdlXmlReader(Reader reader) {
    this(new InputSource(reader));
  }

  public void close() throws IOException {
    InputStream byteStream = inputSource.getByteStream();
    if (byteStream != null) 
      byteStream.close();
    else {
      Reader charStream = inputSource.getCharacterStream();
      if (charStream != null)
        charStream.close();
    }
    document = null;
  }

  public ProcessDefinition getProcessDefinition() {
    return processDefinition;
  }

  public void addProblem(Problem problem) {
    problems.add(problem);
    if (problemListener!=null) problemListener.addProblem(problem);
  }
  
  public void addError(String description) {
    log.error("invalid process xml: "+description);
    addProblem(new Problem(Problem.LEVEL_ERROR, description));
  }

  public void addError(String description, Throwable exception) {
    log.error("invalid process xml: "+description, exception);
    addProblem(new Problem(Problem.LEVEL_ERROR, description, exception));
  }

  public void addWarning(String description) {
    log.warn("process xml warning: "+description);
    addProblem(new Problem(Problem.LEVEL_WARNING, description));
  }

  public ProcessDefinition readProcessDefinition() {
    // create a new definition
    processDefinition = ProcessDefinition.createNewProcessDefinition();

    // initialize lists
    problems = new ArrayList();
    unresolvedTransitionDestinations = new ArrayList();
    unresolvedActionReferences = new ArrayList();	
		
    try {
      // parse the document into a dom tree
      document = JpdlParser.parse(inputSource, this);
      Element root = document.getRootElement();
            
      // read the process name
      parseProcessDefinitionAttributes(root);
      
      // get the process description 
      String description = root.elementTextTrim("description");
      if (description!=null) {
        processDefinition.setDescription(description);
      }

      // first pass: read most content
      readSwimlanes(root);
      readActions(root, null, null);
      readNodes(root, processDefinition);
      readEvents(root, processDefinition);
      readExceptionHandlers(root, processDefinition);
      readTasks(root, null);

      // second pass processing
      resolveTransitionDestinations();
      resolveActionReferences();
      verifySwimlaneAssignments();

    } catch (Exception e) {
      log.error("couldn't parse process definition", e);
      addProblem(new Problem(Problem.LEVEL_ERROR, "couldn't parse process definition", e));
    }
    
    if (Problem.containsProblemsOfLevel(problems, Problem.LEVEL_ERROR)) {
      throw new JpdlException(problems);
    }
    
    if (problems!=null) {
      Iterator iter = problems.iterator();
      while (iter.hasNext()) {
        Problem problem = (Problem) iter.next();
        log.warn("process parse warning: "+problem.getDescription());
      }
    }
    
    return processDefinition;
  }

  protected void parseProcessDefinitionAttributes(Element root) {
    processDefinition.setName(root.attributeValue("name"));
    initialNodeName = root.attributeValue("initial");
  }
  
  protected void readSwimlanes(Element processDefinitionElement) {
    Iterator iter = processDefinitionElement.elementIterator("swimlane");
    TaskMgmtDefinition taskMgmtDefinition = processDefinition.getTaskMgmtDefinition();
    while (iter.hasNext()) {
      Element swimlaneElement = (Element) iter.next();
      String swimlaneName = swimlaneElement.attributeValue("name");
      if (swimlaneName==null) {
        addWarning("there's a swimlane without a name");
      } else {
        Swimlane swimlane = new Swimlane(swimlaneName);
        Element assignmentElement = swimlaneElement.element("assignment");

        if (assignmentElement!=null) {
          
          if ( (assignmentElement.attribute("actor-id")!=null)
              || (assignmentElement.attribute("pooled-actors")!=null)
            ) {
            swimlane.setActorIdExpression(assignmentElement.attributeValue("actor-id"));
            swimlane.setPooledActorsExpression(assignmentElement.attributeValue("pooled-actors"));
           
          } else {
            Delegation assignmentDelegation = readAssignmentDelegation(assignmentElement);
            swimlane.setAssignmentDelegation(assignmentDelegation);
          }
        } else {
          Task startTask = taskMgmtDefinition.getStartTask();
          if ( (startTask==null)
               || (startTask.getSwimlane()!=swimlane)
             ) {
            addWarning("swimlane '"+swimlaneName+"' does not have an assignment");
          }
        }
        taskMgmtDefinition.addSwimlane(swimlane);
      }
    }
  }

  public void readNodes(Element element, NodeCollection nodeCollection) {
    Iterator nodeElementIter = element.elementIterator();
    while (nodeElementIter.hasNext()) {
      Element nodeElement = (Element) nodeElementIter.next();
      String nodeName = nodeElement.getName();
      // get the node type
      Class nodeType = NodeTypes.getNodeType(nodeName);
      if (nodeType!=null) {

        Node node = null;
        try {
          // create a new instance
          node = (Node) nodeType.newInstance();
        } catch (Exception e) {
          log.error("couldn't instantiate node '"+nodeName+"', of type '"+nodeType.getName()+"'", e);
        }
        
        node.setProcessDefinition(processDefinition);
        
        // check for duplicate start-states
        if ( (node instanceof StartState)
             && (processDefinition.getStartState()!=null)
           ) {
          addError("max one start-state allowed in a process");
          
        } else {
          // read the common node parts of the element
          readNode(nodeElement, node, nodeCollection);
        
          // if the node is parsable 
          // (meaning: if the node has special configuration to parse, other then the 
          //  common node data)
          node.read(nodeElement, this);
        }
      }
    }
  }

  public void readTasks(Element element, TaskNode taskNode) {
    List elements = element.elements("task");
    TaskMgmtDefinition tmd = (TaskMgmtDefinition) processDefinition.getDefinition(TaskMgmtDefinition.class); 
    if (elements.size()>0) {
      if (tmd==null) {
        tmd = new TaskMgmtDefinition();
      }
      processDefinition.addDefinition(tmd);
      
      Iterator iter = elements.iterator();
      while (iter.hasNext()) {
        Element taskElement = (Element) iter.next();
        readTask(taskElement, tmd, taskNode);
      }
    }
  }

  public Task readTask(Element taskElement, TaskMgmtDefinition taskMgmtDefinition, TaskNode taskNode) {
    Task task = new Task();
    task.setProcessDefinition(processDefinition);
    
    // get the task name
    String name = taskElement.attributeValue("name");
    if (name!=null) {
      task.setName(name);
      taskMgmtDefinition.addTask(task);
    } else if (taskNode!=null) {
      task.setName(taskNode.getName());
      taskMgmtDefinition.addTask(task);
    }
    
    // get the task description 
    String description = taskElement.elementTextTrim("description");
    if (description!=null) {
      task.setDescription(description);
    } else {
      task.setDescription(taskElement.attributeValue("description"));
    }
    
    // get the condition 
    String condition = taskElement.elementTextTrim("condition");
    if (condition!=null) {
      task.setCondition(condition);
    } else {
      task.setCondition(taskElement.attributeValue("condition"));
    }
    
    // parse common subelements
    readTaskTimers(taskElement, task);
    readEvents(taskElement, task);
    readExceptionHandlers(taskElement, task);

    // duedate and priority
    String duedateText = taskElement.attributeValue("duedate");
    if (duedateText==null) {
      duedateText = taskElement.attributeValue("dueDate");
    }
    task.setDueDate(duedateText);
    String priorityText = taskElement.attributeValue("priority");
    if (priorityText!=null) {
      task.setPriority(Task.parsePriority(priorityText));
    }
    
    // if this task is in the context of a taskNode, associate them
    if (taskNode!=null) {
      taskNode.addTask(task);
    }

    // blocking
    String blockingText = taskElement.attributeValue("blocking");
    if (blockingText!=null) {
      if ( ("true".equalsIgnoreCase(blockingText))
           || ("yes".equalsIgnoreCase(blockingText))
           || ("on".equalsIgnoreCase(blockingText)) ) {
        task.setBlocking(true);
      }
    }
    
    // signalling
    String signallingText = taskElement.attributeValue("signalling");
    if (signallingText!=null) {
      if ( ("false".equalsIgnoreCase(signallingText))
           || ("no".equalsIgnoreCase(signallingText))
           || ("off".equalsIgnoreCase(signallingText)) ) {
        task.setSignalling(false);
      }
    }
    
    // assignment
    String swimlaneName = taskElement.attributeValue("swimlane");
    Element assignmentElement = taskElement.element("assignment");

    // if there is a swimlane attribute specified
    if (swimlaneName!=null) {
      Swimlane swimlane = taskMgmtDefinition.getSwimlane(swimlaneName);
      if (swimlane==null) {
        addWarning("task references unknown swimlane '"+swimlaneName+"':"+taskElement.asXML());
      } else {
        task.setSwimlane(swimlane);
      }

    // else if there is a direct assignment specified
    } else if (assignmentElement!=null) {
      if ( (assignmentElement.attribute("actor-id")!=null)
           || (assignmentElement.attribute("pooled-actors")!=null)
         ) {
        task.setActorIdExpression(assignmentElement.attributeValue("actor-id"));
        task.setPooledActorsExpression(assignmentElement.attributeValue("pooled-actors"));
        
      } else {
        Delegation assignmentDelegation = readAssignmentDelegation(assignmentElement);
        task.setAssignmentDelegation(assignmentDelegation);
      }

    // if no assignment or swimlane is specified
    } else {
        // the user has to manage assignment manually, so we better inform him/her.
        log.info("process xml information: no swimlane or assignment specified for task '"+taskElement.asXML()+"'");
    }

    // notify
    String notificationsText = taskElement.attributeValue("notify");
    if ( notificationsText!=null
         && ("true".equalsIgnoreCase(notificationsText)
             ||  "on".equalsIgnoreCase(notificationsText)
             || "yes".equalsIgnoreCase(notificationsText)
            )
       ) {
      String notificationEvent = Event.EVENTTYPE_TASK_ASSIGN;
      Event event = task.getEvent(notificationEvent);
      if (event==null) {
        event = new Event(notificationEvent);
        task.addEvent(event);
      }
      Delegation delegation = createMailDelegation(notificationEvent, null, null, null, null);
      Action action = new Action(delegation);
      action.setProcessDefinition(processDefinition);
      action.setName(task.getName());
      event.addAction(action);
    }

    // task controller
    Element taskControllerElement = taskElement.element("controller");
    if (taskControllerElement!=null && taskControllerElement.attributeValue("class") != null) {
      task.setTaskController(readTaskController(taskControllerElement));
    } else {
    	//
    	// Helium afegit controlador per defecte
    	//
    	TaskController taskController = new TaskController();
        Delegation taskControllerDelegation = new Delegation("net.conselldemallorca.helium.jbpm3.integracio.DefaultControllerHandler");
        taskController.setTaskControllerDelegation(taskControllerDelegation);
        task.setTaskController(taskController);
    }
    
    return task;
  }

  protected Delegation readAssignmentDelegation(Element assignmentElement) {
    Delegation assignmentDelegation = new Delegation();
    String expression = assignmentElement.attributeValue("expression");
    String actorId = assignmentElement.attributeValue("actor-id");
    String pooledActors = assignmentElement.attributeValue("pooled-actors");
    
    if (expression!=null){
      assignmentDelegation.setProcessDefinition(processDefinition);
      assignmentDelegation.setClassName("org.jbpm.identity.assignment.ExpressionAssignmentHandler");
      assignmentDelegation.setConfiguration("<expression>"+expression+"</expression>");
      
    } else if ( (actorId!=null)
                || (pooledActors!=null) 
              ) {
      assignmentDelegation.setProcessDefinition(processDefinition);
      assignmentDelegation.setClassName("org.jbpm.taskmgmt.assignment.ActorAssignmentHandler");
      String configuration = "";
      if (actorId!=null) {
        configuration += "<actorId>"+actorId+"</actorId>";
      }
      if (pooledActors!=null) {
        configuration += "<pooledActors>"+pooledActors+"</pooledActors>";
      }
      assignmentDelegation.setConfiguration(configuration);
      
    } else {
      assignmentDelegation = new Delegation();
      assignmentDelegation.read(assignmentElement, this);
    }
    
    return assignmentDelegation;
  }

  protected TaskController readTaskController(Element taskControllerElement) {
    TaskController taskController = new TaskController();

    if (taskControllerElement.attributeValue("class")!=null) {
      Delegation taskControllerDelegation = new Delegation();
      taskControllerDelegation.read(taskControllerElement, this);
      taskController.setTaskControllerDelegation(taskControllerDelegation);

    } else {
      List variableAccesses = readVariableAccesses(taskControllerElement);
      taskController.setVariableAccesses(variableAccesses);
    }
    return taskController;
  }
  
  public List readVariableAccesses(Element element) {
    List variableAccesses = new ArrayList();
    Iterator iter = element.elementIterator("variable");
    while (iter.hasNext()) {
      Element variableElement = (Element) iter.next();
      
      String variableName = variableElement.attributeValue("name");
      if (variableName==null) {
        addProblem(new Problem(Problem.LEVEL_WARNING, "the name attribute of a variable element is required: "+variableElement.asXML()));
      }
      String access = variableElement.attributeValue("access", "read,write");
      String mappedName = variableElement.attributeValue("mapped-name");
      
      variableAccesses.add(new VariableAccess(variableName, access, mappedName));
    }
    return variableAccesses;
  }

  public void readStartStateTask(Element startTaskElement, StartState startState) {
    TaskMgmtDefinition taskMgmtDefinition = processDefinition.getTaskMgmtDefinition();
    Task startTask = readTask(startTaskElement, taskMgmtDefinition, null);
    startTask.setStartState(startState);
    if (startTask.getName()==null) {
      startTask.setName(startState.getName());
    }
    taskMgmtDefinition.setStartTask(startTask);
  }

  public void readNode(Element nodeElement, Node node, NodeCollection nodeCollection) {

    // first put the node in its collection.  this is done so that the 
    // setName later on will be able to differentiate between nodes contained in 
    // processDefinitions and nodes contained in superstates
    nodeCollection.addNode(node);

    // get the node name
    String name = nodeElement.attributeValue("name");
    if (name!=null) {
      node.setName(name);

      // check if this is the initial node
      if ( (initialNodeName!=null)
           && (initialNodeName.equals(node.getFullyQualifiedName()))
         ) {
        processDefinition.setStartState(node);
      }
    }

    // get the node description 
    String description = nodeElement.elementTextTrim("description");
    if (description!=null) {
      node.setDescription(description);
    }

    String asyncText = nodeElement.attributeValue("async");
    if ("true".equalsIgnoreCase(asyncText)) {
      node.setAsync(true);
    } else if ("exclusive".equalsIgnoreCase(asyncText)) {
      node.setAsync(true);
      node.setAsyncExclusive(true);
    }

    // parse common subelements
    readNodeTimers(nodeElement, node);
    readEvents(nodeElement, node);
    readExceptionHandlers(nodeElement, node);

    // save the transitions and parse them at the end
    addUnresolvedTransitionDestination(nodeElement, node);
  }

  protected void readNodeTimers(Element nodeElement, Node node) {
    Iterator iter = nodeElement.elementIterator("timer");
    while (iter.hasNext()) {
      Element timerElement = (Element) iter.next();
      readNodeTimer(timerElement, node);
    }
  }

  protected void readNodeTimer(Element timerElement, Node node) {
    String name = timerElement.attributeValue("name", node.getName());
    if (name == null) name = generateTimerName();
    
    CreateTimerAction createTimerAction = new CreateTimerAction();
    createTimerAction.read(timerElement, this);
    createTimerAction.setTimerName(name);
    createTimerAction.setTimerAction(readSingleAction(timerElement));
    addAction(node, Event.EVENTTYPE_NODE_ENTER, createTimerAction);
    
    CancelTimerAction cancelTimerAction = new CancelTimerAction();
    cancelTimerAction.setTimerName(name);
    addAction(node, Event.EVENTTYPE_NODE_LEAVE, cancelTimerAction);
  }

  private String generateTimerName() {
    return "timer-" + (timerNumber++); 
  }

  protected void readTaskTimers(Element taskElement, Task task) {
    Iterator iter = taskElement.elementIterator();
    while (iter.hasNext()) {
      Element element = (Element) iter.next();
      if ( ("timer".equals(element.getName()))
           || ("reminder".equals(element.getName()))
         ) {
        readTaskTimer(element, task);
      }
    }
  }

  protected void readTaskTimer(Element timerElement, Task task) {
    String name = timerElement.attributeValue("name", task.getName());
    if (name==null) name = generateTimerName();

    CreateTimerAction createTimerAction = new CreateTimerAction();
    createTimerAction.read(timerElement, this);
    createTimerAction.setTimerName(name);
    Action action = null;
    if ("timer".equals(timerElement.getName())) {
      action = readSingleAction(timerElement);
    } else {
      Delegation delegation = createMailDelegation("task-reminder", null, null, null, null);
      action = new Action(delegation);
    }
    createTimerAction.setTimerAction(action);
    addAction(task, Event.EVENTTYPE_TASK_CREATE, createTimerAction);

    // read the cancel-event types
    Collection cancelEventTypes = new ArrayList();

    String cancelEventTypeText = timerElement.attributeValue("cancel-event");
    if (cancelEventTypeText!=null) {
      // cancel-event is a comma separated list of events
      StringTokenizer tokenizer = new StringTokenizer(cancelEventTypeText, ",");
      while (tokenizer.hasMoreTokens()) {
        cancelEventTypes.add(tokenizer.nextToken().trim());
      }
    } else {
      // set the default
      cancelEventTypes.add(Event.EVENTTYPE_TASK_END);
    }
    
    Iterator iter = cancelEventTypes.iterator();
    while (iter.hasNext()) {
      String cancelEventType = (String) iter.next();
      CancelTimerAction cancelTimerAction = new CancelTimerAction();
      cancelTimerAction.setTimerName(name);
      addAction(task, cancelEventType, cancelTimerAction);
    }
  }
  
  protected void readEvents(Element parentElement, GraphElement graphElement) {
    Iterator iter = parentElement.elementIterator("event");
    while (iter.hasNext()) {
      Element eventElement = (Element) iter.next();
      String eventType = eventElement.attributeValue("type");
      if (!graphElement.hasEvent(eventType)) {
        graphElement.addEvent(new Event(eventType));
      }
      readActions(eventElement, graphElement, eventType);
    }
  }

  public void readActions(Element eventElement, GraphElement graphElement, String eventType) {
    // for all the elements in the event element
    Iterator nodeElementIter = eventElement.elementIterator();
    while (nodeElementIter.hasNext()) {
      Element actionElement = (Element) nodeElementIter.next();
      String actionName = actionElement.getName();
      if (ActionTypes.hasActionName(actionName)) {
        Action action = createAction(actionElement);
        if ( (graphElement!=null)
             && (eventType!=null)
           ) {
          // add the action to the event
          addAction(graphElement, eventType, action);
        }
      }
    }
  }

  protected void addAction(GraphElement graphElement, String eventType, Action action) {
    Event event = graphElement.getEvent(eventType);
    if (event==null) {
      event = new Event(eventType); 
      graphElement.addEvent(event);
    }
    event.addAction(action);
  }
  
  public Action readSingleAction(Element nodeElement) {
    Action action = null;
    // search for the first action element in the node
    Iterator iter = nodeElement.elementIterator();
    while (iter.hasNext() && (action==null)) {
      Element candidate = (Element) iter.next();
      if (ActionTypes.hasActionName(candidate.getName())) {
        // parse the action and assign it to this node
        action = createAction(candidate);
      }
    }
    return action;
  }

  public Action createAction(Element actionElement) {
    // create a new instance of the action
    Action action = null;
    String actionName = actionElement.getName();
    Class actionType = ActionTypes.getActionType(actionName);
    try {
      action = (Action) actionType.newInstance();
    } catch (Exception e) {
      log.error("couldn't instantiate action '"+actionName+"', of type '"+actionType.getName()+"'", e);
    }

    // read the common node parts of the action
    readAction(actionElement, action);
    
    return action;
  }

  public void readAction(Element element, Action action) {
    // if a name is specified for this action
    String actionName = element.attributeValue("name");
    if (actionName!=null) {
      action.setName(actionName);
      // add the action to the named process action repository 
      processDefinition.addAction(action);
    }

    // if the action is parsable 
    // (meaning: if the action has special configuration to parse, other then the common node data)
    action.read(element, this);
  }

  protected void readExceptionHandlers(Element graphElementElement, GraphElement graphElement) {
    Iterator iter = graphElementElement.elementIterator("exception-handler");
    while (iter.hasNext()) {
      Element exceptionHandlerElement = (Element) iter.next();
      readExceptionHandler(exceptionHandlerElement, graphElement);
    }
  }

  protected void readExceptionHandler(Element exceptionHandlerElement, GraphElement graphElement) {
    // create the exception handler
    ExceptionHandler exceptionHandler = new ExceptionHandler();
    exceptionHandler.setExceptionClassName(exceptionHandlerElement.attributeValue("exception-class"));
    // add it to the graph element
    graphElement.addExceptionHandler(exceptionHandler);

    // read the actions in the body of the exception-handler element
    Iterator iter = exceptionHandlerElement.elementIterator();
    while (iter.hasNext()) {
      Element childElement = (Element) iter.next();
      if (ActionTypes.hasActionName(childElement.getName())) {
        Action action = createAction(childElement);
        exceptionHandler.addAction(action);
      }
    }
  }

  // transition destinations are parsed in a second pass //////////////////////
  
  public void addUnresolvedTransitionDestination(Element nodeElement, Node node) {
    unresolvedTransitionDestinations.add(new Object[]{nodeElement, node});
  }

  public void resolveTransitionDestinations() {
    Iterator iter = unresolvedTransitionDestinations.iterator();
    while (iter.hasNext()) {
      Object[] unresolvedTransition = (Object[]) iter.next();
      Element nodeElement = (Element) unresolvedTransition[0];
      Node node = (Node) unresolvedTransition[1];
      resolveTransitionDestinations(nodeElement.elements("transition"), node);
    }
  }

  public void resolveTransitionDestinations(List transitionElements, Node node) {
    Iterator iter = transitionElements.iterator();
    while (iter.hasNext()) {
      Element transitionElement = (Element) iter.next();
      resolveTransitionDestination(transitionElement, node);
    }
  }

  /**
   * creates the transition object and configures it by the read attributes
   * @return the created <code>org.jbpm.graph.def.Transition</code> object
   *         (useful, if you want to override this method
   *         to read additional configuration properties)
   */
  public Transition resolveTransitionDestination(Element transitionElement, Node node) {
    Transition transition = new Transition();
    transition.setProcessDefinition(processDefinition);

    transition.setName(transitionElement.attributeValue("name"));
    transition.setDescription(transitionElement.elementTextTrim("description"));

    String condition = transitionElement.attributeValue("condition");
    if (condition==null) {
      Element conditionElement = transitionElement.element("condition");
      if (conditionElement!=null) {
        condition = conditionElement.getTextTrim();
        // for backwards compatibility
        if ( (condition==null)
             || (condition.length()==0)
           ) {
          condition = conditionElement.attributeValue("expression");
        }
      }
    }
    transition.setCondition(condition);

    // add the transition to the node
    node.addLeavingTransition(transition);

    // set destinationNode of the transition
    String toName = transitionElement.attributeValue("to");
    if (toName==null) {
      addWarning("node '"+node.getFullyQualifiedName()+"' has a transition without a 'to'-attribute to specify its destinationNode");
    } else {
      Node to = ((NodeCollection)node.getParent()).findNode(toName);
      if (to==null) {
        addWarning("transition to='"+toName+"' on node '"+node.getFullyQualifiedName()+"' cannot be resolved");
      } else {
        to.addArrivingTransition(transition);
      }
    }
    
    // read the actions
    readActions(transitionElement, transition, Event.EVENTTYPE_TRANSITION);
    
    readExceptionHandlers(transitionElement, transition);
    
    return transition;
  }
  
  // action references are parsed in a second pass ////////////////////////////

  public void addUnresolvedActionReference(Element actionElement, Action action) {
    unresolvedActionReferences.add(new Object[]{actionElement, action});
  }

  public void resolveActionReferences() {
    Iterator iter = unresolvedActionReferences.iterator();
    while (iter.hasNext()) {
      Object[] unresolvedActionReference = (Object[]) iter.next();
      Element actionElement = (Element) unresolvedActionReference[0];
      Action action = (Action) unresolvedActionReference[1];
      String referencedActionName = actionElement.attributeValue("ref-name");
      Action referencedAction = processDefinition.getAction(referencedActionName);
      if (referencedAction==null) {
        addWarning("couldn't resolve action reference in "+actionElement.asXML());
      }
      action.setReferencedAction(referencedAction);
    }
  }

  // verify swimlane assignments in second pass ///////////////////////////////
  public void verifySwimlaneAssignments() {
    TaskMgmtDefinition taskMgmtDefinition = processDefinition.getTaskMgmtDefinition();
    if ( (taskMgmtDefinition!=null)
         && (taskMgmtDefinition.getSwimlanes()!=null)
       ) {
      Iterator iter = taskMgmtDefinition.getSwimlanes().values().iterator();
      while (iter.hasNext()) {
        Swimlane swimlane = (Swimlane) iter.next();
        
        Task startTask = taskMgmtDefinition.getStartTask();
        Swimlane startTaskSwimlane = (startTask!=null ? startTask.getSwimlane() : null);
        
        if ( (swimlane.getAssignmentDelegation()==null)
             && (swimlane!=startTaskSwimlane) 
           ) {
          addWarning("swimlane '"+swimlane.getName()+"' does not have an assignment");
        }
      }
    }
  }

  // mail delegations /////////////////////////////////////////////////////////

  public Delegation createMailDelegation(String template,
                                         String actors,
                                         String to,
                                         String subject,
                                         String text) {
    StringBuffer config = new StringBuffer();
    if (template!=null) {
      config.append("<template>");
      config.append(template);
      config.append("</template>");
    }
    if (actors!=null) {
      config.append("<actors>");
      config.append(actors);
      config.append("</actors>");
    }
    if (to!=null) {
      config.append("<to>");
      config.append(to);
      config.append("</to>");
    }
    if (subject!=null) {
      config.append("<subject>");
      config.append(subject);
      config.append("</subject>");
    }
    if (text!=null) {
      config.append("<text>");
      config.append(text);
      config.append("</text>");
    }

    String mailClassName = Mail.class.getName();
    if (JbpmConfiguration.Configs.hasObject("jbpm.mail.class.name")) {
      mailClassName = JbpmConfiguration.Configs.getString("jbpm.mail.class.name");
    } else if (JbpmConfiguration.Configs.hasObject("mail.class.name")) {
      mailClassName = JbpmConfiguration.Configs.getString("mail.class.name");
    }

    Delegation delegation = new Delegation(mailClassName);
    delegation.setProcessDefinition(processDefinition);
    delegation.setConfiguration(config.toString());
    return delegation;
  }

  public String getProperty(String property, Element element) {
    String value = element.attributeValue(property);
    if (value==null) {
      Element propertyElement = element.element(property);
      if (propertyElement!=null) {
        value = propertyElement.getText();
      }
    }
    return value;
  }

  private static final Log log = LogFactory.getLog(JpdlXmlReader.class);
}
