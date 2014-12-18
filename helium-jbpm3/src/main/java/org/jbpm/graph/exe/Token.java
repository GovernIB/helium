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
package org.jbpm.graph.exe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.db.JobSession;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Identifiable;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.log.SignalLog;
import org.jbpm.graph.log.TokenCreateLog;
import org.jbpm.graph.log.TokenEndLog;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.logging.exe.LoggingInstance;
import org.jbpm.logging.log.CompositeLog;
import org.jbpm.logging.log.ProcessLog;
import org.jbpm.svc.Services;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;
import org.jbpm.util.Clock;
import org.jbpm.util.EqualsUtil;

/**
 * represents one path of execution and maintains a pointer to a node in the
 * {@link org.jbpm.graph.def.ProcessDefinition}. Most common way to get a hold of the token objects is with
 * {@link ProcessInstance#getRootToken()} or {@link org.jbpm.graph.exe.ProcessInstance#findToken(String)}.
 */
public class Token implements Identifiable, Serializable
{

  private static final long serialVersionUID = 1L;

  long id = 0;
  int version = 0;
  protected String name = null;
  protected Date start = null;
  protected Date end = null;
  protected Node node = null;
  protected Date nodeEnter = null;
  protected ProcessInstance processInstance = null;
  protected Token parent = null;
  protected Map<String,Token> children = null;
  protected List comments = null;
  protected ProcessInstance subProcessInstance = null;
  protected int nextLogIndex = 0;
  boolean isAbleToReactivateParent = true;
  boolean isTerminationImplicit = false;
  boolean isSuspended = false;
  String lock = null;

  // constructors
  // ////////////////////
  ///////////////////////////////////////////////////////

  public Token()
  {
  }

  /**
   * creates a root token.
   */
  public Token(ProcessInstance processInstance)
  {
    this.start = Clock.getCurrentTime();
    this.processInstance = processInstance;
    this.node = processInstance.getProcessDefinition().getStartState();
    this.isTerminationImplicit = processInstance.getProcessDefinition().isTerminationImplicit();

    // optimization: assigning an id is not necessary since the process instance will be saved shortly.
    // Services.assignId(this);
  }

  /**
   * creates a child token.
   */
  public Token(Token parent, String name)
  {
    this.start = Clock.getCurrentTime();
    this.processInstance = parent.getProcessInstance();
    this.name = name;
    this.node = parent.getNode();
    this.parent = parent;
    parent.addChild(this);
    this.isTerminationImplicit = parent.isTerminationImplicit();
    parent.addLog(new TokenCreateLog(this));

    // assign an id to this token before events get fired
    Services.assignId(this);
  }

  // operations
  // ///////////////////////////////////////////////////////////////////////////

  void addChild(Token token)
  {
    if (children == null)
    {
      children = new HashMap<String,Token>();
    }
    children.put(token.getName(), token);
  }

  public void removeChild(Token token)
  {
	  if (children != null)
		  children.remove(token.getName());
  }

  /**
   * provides a signal to the token. this method activates this token and leaves the current state over the default
   * transition.
   */
  public void signal()
  {
    if (node == null)
    {
      throw new JbpmException("token '" + this + "' can't be signalled cause it is currently not positioned in a node");
    }
    if (node.getDefaultLeavingTransition() == null)
    {
      throw new JbpmException("couldn't signal token '" + this + "' : node '" + node + "' doesn't have a default transition");
    }
    signal(node.getDefaultLeavingTransition(), new ExecutionContext(this));
  }

  /**
   * Provides a signal to the token. 
   * This leave the current state over the given transition name.
   */
  public void signal(String transitionName)
  {
    if (node == null)
      throw new JbpmException("token '" + this + "' can't be signalled cause it is currently not positioned in a node");
    
    Transition leavingTransition = node.getLeavingTransition(transitionName);
    
    if (leavingTransition == null)
    {
      // Fall back to the name of the target node
      for (Transition auxTrans : node.getLeavingTransitions())
      {
        if (transitionName.equals(auxTrans.getTo().getName()))
        {
          leavingTransition = auxTrans;
          break;
        }
      }
    }
    
    if (leavingTransition == null)
      throw new JbpmException("transition '" + transitionName + "' does not exist on " + node);
    
    signal(leavingTransition, new ExecutionContext(this));
  }

  /**
   * provides a signal to the token. this leave the current state over the given transition name.
   */
  public void signal(Transition transition)
  {
    signal(transition, new ExecutionContext(this));
  }

  void signal(ExecutionContext executionContext)
  {
    signal(node.getDefaultLeavingTransition(), executionContext);
  }

  void signal(Transition transition, ExecutionContext executionContext)
  {
    if (transition == null)
    {
      throw new JbpmException("couldn't signal without specifying  a leaving transition : transition is null");
    }
    if (executionContext == null)
    {
      throw new JbpmException("couldn't signal without an execution context: executionContext is null");
    }
    if (isSuspended)
    {
      throw new JbpmException("can't signal token '" + name + "' (" + id + "): it is suspended");
    }
    if (isLocked())
    {
      throw new JbpmException("this token is locked by " + lock);
    }

    startCompositeLog(new SignalLog(transition));
    try
    {
      // fire the event before-signal
      Node signalNode = node;
      signalNode.fireEvent(Event.EVENTTYPE_BEFORE_SIGNAL, executionContext);

      // start calculating the next state
      node.leave(executionContext, transition);

      // if required, check if this token is implicitly terminated
      checkImplicitTermination();

      // fire the event after-signal
      signalNode.fireEvent(Event.EVENTTYPE_AFTER_SIGNAL, executionContext);

    }
    finally
    {
      endCompositeLog();
    }
  }

  /**
   * a set of all the leaving transitions on the current node for which the condition expression resolves to true.
   */
  public Set getAvailableTransitions()
  {
    Set availableTransitions = new HashSet();
    if (node != null)
    {
      addAvailableTransitionsOfNode(node, availableTransitions);
    }
    return availableTransitions;
  }

  /**
   * adds available transitions of that node to the Set and after that calls itself recursivly for the SuperSate of the
   * Node if it has a super state
   */
  private void addAvailableTransitionsOfNode(Node currentNode, Set availableTransitions)
  {
    List leavingTransitions = currentNode.getLeavingTransitions();
    if (leavingTransitions != null)
    {
      Iterator iter = leavingTransitions.iterator();
      while (iter.hasNext())
      {
        Transition transition = (Transition)iter.next();
        String conditionExpression = transition.getCondition();
        if (conditionExpression != null)
        {
          Object result = JbpmExpressionEvaluator.evaluate(conditionExpression, new ExecutionContext(this));
          if ((result instanceof Boolean) && (((Boolean)result).booleanValue()))
          {
            availableTransitions.add(transition);
          }
        }
        else
        {
          availableTransitions.add(transition);
        }
      }
    }
    if (currentNode.getSuperState() != null)
    {
      addAvailableTransitionsOfNode(currentNode.getSuperState(), availableTransitions);
    }
  }

  /**
   * ends this token and all of its children (if any). this is the last active (=not-ended) child of a parent token, the
   * parent token will be ended as well and that verification will continue to propagate.
   */
  public void end()
  {
    end(true);
  }

  /**
   * ends this token with optional parent ending verification.
   * 
   * @param verifyParentTermination specifies if the parent token should be checked for termination. if
   *          verifyParentTermination is set to true and this is the last non-ended child of a parent token, the parent
   *          token will be ended as well and the verification will continue to propagate.
   */
  public void end(boolean verifyParentTermination)
  {
    // if not already ended
    if (end == null)
    {

      // ended tokens cannot reactivate parents
      isAbleToReactivateParent = false;

      // set the end date
      // the end date is also the flag that indicates that this token has ended.
      this.end = Clock.getCurrentTime();

      // end all this token's children
      if (children != null)
      {
        Iterator iter = children.values().iterator();
        while (iter.hasNext())
        {
          Token child = (Token)iter.next();
          if (!child.hasEnded())
          {
            child.end();
          }
        }
      }

      if (subProcessInstance != null)
      {
        subProcessInstance.end();
      }

      // only log the end of child-tokens. the process instance logs replace the root token logs.
      if (parent != null)
      {
        // add a log
        parent.addLog(new TokenEndLog(this));
      }

      // if there are tasks associated to this token, remove signaling capabilities
      TaskMgmtInstance taskMgmtInstance = (processInstance != null ? processInstance.getTaskMgmtInstance() : null);
      if (taskMgmtInstance != null)
      {
        taskMgmtInstance.removeSignalling(this);
      }

      if (verifyParentTermination)
      {
        // if this is the last active token of the parent,
        // the parent needs to be ended as well
        notifyParentOfTokenEnd();
      }
    }
  }

  // comments /////////////////////////////////////////////////////////////////

  public void addComment(String message)
  {
    addComment(new Comment(message));
  }

  public void addComment(Comment comment)
  {
    if (comments == null)
      comments = new ArrayList();
    comments.add(comment);
    comment.setToken(this);
  }

  public List getComments()
  {
    return comments;
  }

  // operations helper methods ////////////////////////////////////////////////

  /**
   * notifies a parent that one of its nodeMap has ended.
   */
  void notifyParentOfTokenEnd()
  {
    if (isRoot())
    {
      processInstance.end();
    }
    else
    {

      if (!parent.hasActiveChildren())
      {
        parent.end();
      }
    }
  }

  /**
   * tells if this token has child tokens that have not yet ended.
   */
  public boolean hasActiveChildren()
  {
    boolean foundActiveChildToken = false;
    // try and find at least one child token that is
    // still active (= not ended)
    if (children != null)
    {
      Iterator iter = children.values().iterator();
      while ((iter.hasNext()) && (!foundActiveChildToken))
      {
        Token child = (Token)iter.next();
        if (!child.hasEnded())
        {
          foundActiveChildToken = true;
        }
      }
    }
    return foundActiveChildToken;
  }

  // log convenience methods //////////////////////////////////////////////////

  /**
   * convenience method for adding a process log.
   */
  public void addLog(ProcessLog processLog)
  {
    LoggingInstance li = (LoggingInstance)processInstance.getInstance(LoggingInstance.class);
    if (li != null)
    {
      processLog.setToken(this);
      li.addLog(processLog);
    }
  }

  /**
   * convenience method for starting a composite log. When you add composite logs, make sure you put the
   * {@link #endCompositeLog()} in a finally block.
   */
  public void startCompositeLog(CompositeLog compositeLog)
  {
    LoggingInstance li = (LoggingInstance)processInstance.getInstance(LoggingInstance.class);
    if (li != null)
    {
      compositeLog.setToken(this);
      li.startCompositeLog(compositeLog);
    }
  }

  /**
   * convenience method for ending a composite log. Make sure you put this in a finally block.
   */
  public void endCompositeLog()
  {
    LoggingInstance li = (LoggingInstance)processInstance.getInstance(LoggingInstance.class);
    if (li != null)
    {
      li.endCompositeLog();
    }
  }

  // various information extraction methods ///////////////////////////////////

  public String toString()
  {
    return "Token(" + getFullName() + ")";
  }

  public boolean hasEnded()
  {
    return (end != null);
  }

  public boolean isRoot()
  {
    return (parent == null);
  }

  public boolean hasParent()
  {
    return (parent != null);
  }

  public boolean hasChild(String name)
  {
    return (children != null ? children.containsKey(name) : false);
  }

  public Token getChild(String name)
  {
    Token child = null;
    if (children != null)
    {
      child = (Token)children.get(name);
    }
    return child;
  }

  public String getFullName()
  {
    if (parent == null)
      return "/";
    if (parent.getParent() == null)
      return "/" + name;
    return parent.getFullName() + "/" + name;
  }

  public List getChildrenAtNode(Node aNode)
  {
    List foundChildren = new ArrayList();
    getChildrenAtNode(aNode, foundChildren);
    return foundChildren;
  }

  void getChildrenAtNode(Node aNode, List foundTokens)
  {
    if (aNode.equals(node))
    {
      foundTokens.add(this);
    }
    else if (children != null && !children.isEmpty())
    {
      for (Iterator it = children.values().iterator(); it.hasNext();)
      {
        Token aChild = (Token)it.next();
        aChild.getChildrenAtNode(aNode, foundTokens);
      }
    }
  }

  public void collectChildrenRecursively(List tokens)
  {
    if (children != null)
    {
      Iterator iter = children.values().iterator();
      while (iter.hasNext())
      {
        Token child = (Token)iter.next();
        tokens.add(child);
        child.collectChildrenRecursively(tokens);
      }
    }
  }

  public Token findToken(String relativeTokenPath)
  {
    if (relativeTokenPath == null)
      return null;
    String path = relativeTokenPath.trim();
    if (("".equals(path)) || (".".equals(path)))
    {
      return this;
    }
    if ("..".equals(path))
    {
      return parent;
    }
    if (path.startsWith("/"))
    {
      Token root = processInstance.getRootToken();
      return root.findToken(path.substring(1));
    }
    if (path.startsWith("./"))
    {
      return findToken(path.substring(2));
    }
    if (path.startsWith("../"))
    {
      if (parent != null)
      {
        return parent.findToken(path.substring(3));
      }
      return null;
    }
    int slashIndex = path.indexOf('/');
    if (slashIndex == -1)
    {
      return (Token)(children != null ? children.get(path) : null);
    }
    Token token = null;
    String name = path.substring(0, slashIndex);
    token = (Token)children.get(name);
    if (token != null)
    {
      return token.findToken(path.substring(slashIndex + 1));
    }
    return null;
  }

  public Map getActiveChildren()
  {
    Map activeChildren = new HashMap();
    if (children != null)
    {
      Iterator iter = children.entrySet().iterator();
      while (iter.hasNext())
      {
        Map.Entry entry = (Map.Entry)iter.next();
        Token child = (Token)entry.getValue();
        if (!child.hasEnded())
        {
          String childName = (String)entry.getKey();
          activeChildren.put(childName, child);
        }
      }
    }
    return activeChildren;
  }

  public void checkImplicitTermination()
  {
    if (isTerminationImplicit && node.hasNoLeavingTransitions())
    {
      end();

      if (processInstance.isTerminatedImplicitly())
      {
        processInstance.end();
      }
    }
  }

  public boolean isTerminatedImplicitly()
  {
    if (end != null)
      return true;

    Map leavingTransitions = node.getLeavingTransitionsMap();
    if ((leavingTransitions != null) && (leavingTransitions.size() > 0))
    {
      // ok: found a non-terminated token
      return false;
    }

    // loop over all active child tokens
    Iterator iter = getActiveChildren().values().iterator();
    while (iter.hasNext())
    {
      Token child = (Token)iter.next();
      if (!child.isTerminatedImplicitly())
      {
        return false;
      }
    }
    // if none of the above, this token is terminated implicitly
    return true;
  }

  public int nextLogIndex()
  {
    return nextLogIndex++;
  }

  /**
   * suspends a process execution.
   */
  public void suspend()
  {
    isSuspended = true;

    suspendJobs();
    suspendTaskInstances();

    // propagate to child tokens
    if (children != null)
    {
      Iterator iter = children.values().iterator();
      while (iter.hasNext())
      {
        Token child = (Token)iter.next();
        child.suspend();
      }
    }
  }

  void suspendJobs()
  {
    JbpmContext jbpmContext = JbpmContext.getCurrentJbpmContext();
    JobSession jobSession = (jbpmContext != null ? jbpmContext.getJobSession() : null);
    if (jobSession != null)
    {
      jobSession.suspendJobs(this);
    }
  }

  void suspendTaskInstances()
  {
    TaskMgmtInstance taskMgmtInstance = (processInstance != null ? processInstance.getTaskMgmtInstance() : null);
    if (taskMgmtInstance != null)
    {
      taskMgmtInstance.suspend(this);
    }
  }

  /**
   * resumes a process execution.
   */
  public void resume()
  {
    isSuspended = false;

    resumeJobs();
    resumeTaskInstances();

    // propagate to child tokens
    if (children != null)
    {
      Iterator iter = children.values().iterator();
      while (iter.hasNext())
      {
        Token child = (Token)iter.next();
        child.resume();
      }
    }
  }

  void resumeJobs()
  {
    JbpmContext jbpmContext = JbpmContext.getCurrentJbpmContext();
    JobSession jobSession = (jbpmContext != null ? jbpmContext.getJobSession() : null);
    if (jobSession != null)
    {
      jobSession.resumeJobs(this);
    }
  }

  void resumeTaskInstances()
  {
    TaskMgmtInstance taskMgmtInstance = (processInstance != null ? processInstance.getTaskMgmtInstance() : null);
    if (taskMgmtInstance != null)
    {
      taskMgmtInstance.resume(this);
    }
  }

  // equals ///////////////////////////////////////////////////////////////////
  // hack to support comparing hibernate proxies against the real objects
  // since this always falls back to ==, we don't need to overwrite the hashcode
  public boolean equals(Object o)
  {
    return EqualsUtil.equals(this, o);
  }

  public ProcessInstance createSubProcessInstance(ProcessDefinition subProcessDefinition)
  {
    // create the new sub process instance
    subProcessInstance = new ProcessInstance(subProcessDefinition);
    // bind the subprocess to the super-process-token
    setSubProcessInstance(subProcessInstance);
    subProcessInstance.setSuperProcessToken(this);
    // make sure the process gets saved during super process save
    processInstance.addCascadeProcessInstance(subProcessInstance);
    return subProcessInstance;
  }

  /**
   * locks a process instance for further execution. A locked token cannot continue execution. This is a non-persistent
   * operation. This is used to prevent tokens being propagated during the execution of actions.
   * 
   * @see #unlock(String)
   */
  public void lock(String lockOwnerId)
  {
    if (lockOwnerId == null)
    {
      throw new JbpmException("can't lock with null value for the lockOwnerId");
    }
    if ((lock != null) && (!lock.equals(lockOwnerId)))
    {
      throw new JbpmException("token '" + id + "' can't be locked by '" + lockOwnerId + "' cause it's already locked by '" + lock + "'");
    }
    log.debug("token[" + id + "] is locked by " + lockOwnerId);
    lock = lockOwnerId;
  }

  /**
   * @see #lock(String)
   */
  public void unlock(String lockOwnerId)
  {
    if (lock == null)
    {
      log.warn("lock owner '" + lockOwnerId + "' tries to unlock token '" + id + "' which is not locked");
    }
    else if (!lock.equals(lockOwnerId))
    {
      throw new JbpmException("'" + lockOwnerId + "' can't unlock token '" + id + "' because it was already locked by '" + lock + "'");
    }
    log.debug("token[" + id + "] is unlocked by " + lockOwnerId);
    lock = null;
  }

  /**
   * force unlocking the token, even if the owner is not known. In some
   * use cases (e.g. in the jbpm esb integration) the lock is persistent,
   * so a state can be reached where the client needs a possibility to force
   * unlock of a token without knowing the owner.
   * 
   * See https://jira.jboss.org/jira/browse/JBPM-1888
   */
  public void foreUnlock()
  {
    if (lock == null)
    {
      log.warn("Unlock of token '" + id + "' forced, but it is not locked");
    }
    log.debug("Foce unlock of token[" + id + "] which was locked by " + lock);
    lock = null;
  }
  
  /**
   * return the current lock owner of the token
   * 
   * See https://jira.jboss.org/jira/browse/JBPM-1888
   */
  public String getLockOwner() {
	  return lock;
  }
  
  public boolean isLocked()
  {
    return lock != null;
  }

  // getters and setters //////////////////////////////////////////////////////

  public long getId()
  {
    return id;
  }

  public Date getStart()
  {
    return start;
  }

  public Date getEnd()
  {
    return end;
  }

  public String getName()
  {
    return name;
  }

  public ProcessInstance getProcessInstance()
  {
    return processInstance;
  }

  public Map<String,Token> getChildren()
  {
    return children;
  }

  public Node getNode()
  {
    return node;
  }

  public void setNode(Node node)
  {
    this.node = node;
  }

  public Token getParent()
  {
    return parent;
  }

  public void setParent(Token parent)
  {
    this.parent = parent;
  }

  public void setProcessInstance(ProcessInstance processInstance)
  {
    this.processInstance = processInstance;
  }

  public ProcessInstance getSubProcessInstance()
  {
    return subProcessInstance;
  }

  public Date getNodeEnter()
  {
    return nodeEnter;
  }

  public void setNodeEnter(Date nodeEnter)
  {
    this.nodeEnter = nodeEnter;
  }

  public boolean isAbleToReactivateParent()
  {
    return isAbleToReactivateParent;
  }

  public void setAbleToReactivateParent(boolean isAbleToReactivateParent)
  {
    this.isAbleToReactivateParent = isAbleToReactivateParent;
  }

  public boolean isTerminationImplicit()
  {
    return isTerminationImplicit;
  }

  public void setTerminationImplicit(boolean isTerminationImplicit)
  {
    this.isTerminationImplicit = isTerminationImplicit;
  }

  public boolean isSuspended()
  {
    return isSuspended;
  }

  public void setSubProcessInstance(ProcessInstance subProcessInstance)
  {
    this.subProcessInstance = subProcessInstance;
  }

  private static final Log log = LogFactory.getLog(Token.class);
}
