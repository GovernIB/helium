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
package org.jbpm.graph.def;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.core.model.dao.DaoProxy;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.RuntimeAction;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.log.ActionLog;
import org.jbpm.instantiation.UserCodeInterceptorConfig;
import org.jbpm.job.ExecuteActionJob;
import org.jbpm.msg.MessageService;
import org.jbpm.persistence.db.DbPersistenceService;
import org.jbpm.signal.EventService;
import org.jbpm.svc.Service;
import org.jbpm.svc.Services;
import org.jbpm.util.EqualsUtil;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class GraphElement implements Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  long id = 0;
  protected String name = null;
  protected String description = null;
  protected ProcessDefinition processDefinition = null;
  protected Map events = null;
  protected List exceptionHandlers = null;

  public GraphElement() {
  }

  public GraphElement(String name) {
    setName(name);
  }

  // events ///////////////////////////////////////////////////////////////////

  /**
   * indicative set of event types supported by this graph element. this is currently only used by the process designer
   * to know which event types to show on a given graph element. in process definitions and at runtime, there are no
   * contstraints on the event-types.
   */
  public abstract String[] getSupportedEventTypes();

  /**
   * gets the events, keyd by eventType (java.lang.String).
   */
  public Map getEvents() {
    return events;
  }

  public boolean hasEvents() {
    return ((events != null) && (events.size() > 0));
  }

  public Event getEvent(String eventType) {
    Event event = null;
    if (events != null) {
      event = (Event) events.get(eventType);
    }
    return event;
  }

  public boolean hasEvent(String eventType) {
    boolean hasEvent = false;
    if (events != null) {
      hasEvent = events.containsKey(eventType);
    }
    return hasEvent;
  }

  public Event addEvent(Event event) {
    if (event == null) {
      throw new IllegalArgumentException("can't add null event to graph element");
    }
    if (event.getEventType() == null) {
      throw new IllegalArgumentException("can't add an event without type to graph element");
    }
    if (events == null) {
      events = new HashMap();
    }
    events.put(event.getEventType(), event);
    event.graphElement = this;
    return event;
  }

  public Event removeEvent(Event event) {
    Event removedEvent = null;
    if (event == null) {
      throw new IllegalArgumentException("can't remove null event from graph element");
    }
    if (event.getEventType() == null) {
      throw new IllegalArgumentException("can't remove an event without type from graph element");
    }
    if (events != null) {
      removedEvent = (Event) events.remove(event.getEventType());
      if (removedEvent != null) {
        event.graphElement = null;
      }
    }
    return removedEvent;
  }

  // exception handlers ///////////////////////////////////////////////////////

  /**
   * is the list of exception handlers associated to this graph element.
   */
  public List getExceptionHandlers() {
    return exceptionHandlers;
  }

  public ExceptionHandler addExceptionHandler(ExceptionHandler exceptionHandler) {
    if (exceptionHandler == null) {
      throw new IllegalArgumentException("can't add null exceptionHandler to graph element");
    }
    if (exceptionHandlers == null) {
      exceptionHandlers = new ArrayList();
    }
    exceptionHandlers.add(exceptionHandler);
    exceptionHandler.graphElement = this;
    return exceptionHandler;
  }

  public void removeExceptionHandler(ExceptionHandler exceptionHandler) {
    if (exceptionHandler == null) {
      throw new IllegalArgumentException("can't remove null exceptionHandler from graph element");
    }
    if (exceptionHandlers != null && exceptionHandlers.remove(exceptionHandler)) {
      exceptionHandler.graphElement = null;
    }
  }

  public void reorderExceptionHandler(int oldIndex, int newIndex) {
    if ((exceptionHandlers != null)
        && (Math.min(oldIndex, newIndex) >= 0)
        && (Math.max(oldIndex, newIndex) < exceptionHandlers.size())) {
      Object o = exceptionHandlers.remove(oldIndex);
      exceptionHandlers.add(newIndex, o);
    } else {
      throw new IndexOutOfBoundsException("couldn't reorder element from index '" + oldIndex
          + "' to index '" + newIndex + "' in " + exceptionHandlers);
    }
  }

  // event handling ///////////////////////////////////////////////////////////

  public void fireEvent(String eventType, ExecutionContext executionContext) {
    log.debug("event '" + eventType + "' on '" + this + "' for '" + executionContext.getToken() + "'");
    try {
      executionContext.setEventSource(this);

      // TODO: Is it a valid condition that the jbpmContext is not there?
      JbpmContext jbpmContext = executionContext.getJbpmContext();
      if (jbpmContext != null) {
        Services services = jbpmContext.getServices();
        if (services != null) {
          EventService evService = (EventService) services.getService(EventService.SERVICE_NAME);
          if (evService != null) {
            evService.fireEvent(eventType, this, executionContext);
          }
        }
      }

      fireAndPropagateEvent(eventType, executionContext);
    } finally {
    	executionContext.setEventSource(null);
    }
  }

  public void fireAndPropagateEvent(String eventType, ExecutionContext executionContext) {
    // calculate if the event was fired on this element or if it was a
    // propagated event
    boolean isPropagated = !(this.equals(executionContext.getEventSource()));

    // execute static actions
    Event event = getEvent(eventType);
    if (event != null) {
      // update the context
      executionContext.setEvent(event);
      // execute the static actions specified in the process definition
      executeActions(event.getActions(), executionContext, isPropagated);
    }

    // execute the runtime actions
    List runtimeActions = getRuntimeActionsForEvent(executionContext, eventType);
    executeActions(runtimeActions, executionContext, isPropagated);

    // remove the event from the context
    executionContext.setEvent(null);

    // propagate the event to the parent element
    GraphElement parent = getParent();
    if (parent != null) {
      parent.fireAndPropagateEvent(eventType, executionContext);
    }
  }

  void executeActions(List actions, ExecutionContext executionContext, boolean isPropagated) {
    if (actions != null) {
      Iterator iter = actions.iterator();
      while (iter.hasNext()) {
        Action action = (Action) iter.next();
        if (action.acceptsPropagatedEvents() || (!isPropagated)) {
          if (action.isAsync()) {
            ExecuteActionJob job = createAsyncActionExecutionJob(executionContext.getToken(), action);
            MessageService messageService = (MessageService) Services.getCurrentService(Services.SERVICENAME_MESSAGE);
            messageService.send(job);
          } else {
            executeAction(action, executionContext);
          }
        }
      }
    }
  }

  protected ExecuteActionJob createAsyncActionExecutionJob(Token token, Action action) {
    ExecuteActionJob job = new ExecuteActionJob(token);
    job.setAction(action);
    job.setDueDate(new Date());
    job.setExclusive(action.isAsyncExclusive());
    return job;
  }

  public void executeAction(Action action, ExecutionContext executionContext) {
    Token token = executionContext.getToken();

    Expedient exp = null;
    if (MesuresTemporalsHelper.isActiu()) {
    	ProcessInstance pi = executionContext.getProcessInstance();
    	while (pi.getSuperProcessToken() != null) {
    		pi = pi.getSuperProcessToken().getProcessInstance();
    	}
    	exp = DaoProxy.getInstance().getExpedientDao().findAmbProcessInstanceId(String.valueOf(pi.getId()));
    	DaoProxy.getInstance().getAdminService().getMesuresTemporalsHelper().mesuraIniciar("ACCIO: " + (action != null ? action.getName() : "null"), "tasques", exp.getTipus().getNom());
    }
    
    // create action log
    ActionLog actionLog = new ActionLog(action);
    token.startCompositeLog(actionLog);

    // if this is an action being executed in an event,
    // the token needs to be locked. if this is an action
    // being executed as the node behaviour or if the token
    // is already locked, the token doesn't need to be locked.
    boolean actionMustBeLocked = (executionContext.getEvent() != null) && (!token.isLocked());

    try {
      // update the execution context
      executionContext.setAction(action);

      // execute the action
      log.debug("executing action '" + action + "'");
      String lockOwnerId = "token[" + token.getId() + "]";
      try {
        if (actionMustBeLocked) {
          token.lock(lockOwnerId);
        }

        if (UserCodeInterceptorConfig.userCodeInterceptor != null) {
          UserCodeInterceptorConfig.userCodeInterceptor.executeAction(action, executionContext);
        } else {
          action.execute(executionContext);
        }

      } finally {
        if (actionMustBeLocked) {
          token.unlock(lockOwnerId);
        }
      }

    } catch (Exception exception) {
      // NOTE that Errors are not caught because that might halt the JVM and mask the original Error
      //log.error("action threw exception: " + exception.getMessage(), exception);

      // log the action exception
      actionLog.setException(exception);

      // if an exception handler is available
      raiseException(exception, executionContext);
    } finally {
      executionContext.setAction(null);
      token.endCompositeLog();
      if (MesuresTemporalsHelper.isActiu())
    	  DaoProxy.getInstance().getAdminService().getMesuresTemporalsHelper().mesuraCalcular("ACCIO: " + (action != null ? action.getName() : "null"), "tasques", exp.getTipus().getNom());
    }
  }

  List getRuntimeActionsForEvent(ExecutionContext executionContext, String eventType) {
    List runtimeActionsForEvent = null;
    List runtimeActions = executionContext.getProcessInstance().getRuntimeActions();
    if (runtimeActions != null) {
      Iterator iter = runtimeActions.iterator();
      while (iter.hasNext()) {
        RuntimeAction runtimeAction = (RuntimeAction) iter.next();
        // if the runtime-action action is registered on this element and this eventType
        if ((this.equals(runtimeAction.getGraphElement()))
            && (eventType.equals(runtimeAction.getEventType()))) {
          // ... add its action to the list of runtime actions
          if (runtimeActionsForEvent == null)
            runtimeActionsForEvent = new ArrayList();
          runtimeActionsForEvent.add(runtimeAction.getAction());
        }
      }
    }
    return runtimeActionsForEvent;
  }

  /*
   * // the next instruction merges the actions specified in the process definition with the runtime actions List
   * actions = event.collectActions(executionContext);
   * 
   * // loop over all actions of this event Iterator iter = actions.iterator(); while (iter.hasNext()) { Action action =
   * (Action) iter.next(); executionContext.setAction(action);
   * 
   * if ( (!isPropagated) || (action.acceptsPropagatedEvents() ) ) {
   * 
   * // create action log ActionLog actionLog = new ActionLog(action);
   * executionContext.getToken().startCompositeLog(actionLog);
   * 
   * try { // execute the action action.execute(executionContext);
   * 
   * } catch (Exception exception) { // NOTE that Error's are not caught because that might halt the JVM and mask the
   * original Error. Event.log.error("action threw exception: "+exception.getMessage(), exception);
   * 
   * // log the action exception actionLog.setException(exception);
   * 
   * // if an exception handler is available event.graphElement.raiseException(exception, executionContext); } finally {
   * executionContext.getToken().endCompositeLog(); } } } }
   */

  /**
   * throws an ActionException if no applicable exception handler is found. An ExceptionHandler is searched for in this
   * graph element and then recursively up the parent hierarchy. If an exception handler is found, it is applied. If the
   * exception handler does not throw an exception, the exception is considered handled. Otherwise the search for an
   * applicable exception handler continues where it left of with the newly thrown exception.
   */
  public void raiseException(Throwable exception, ExecutionContext executionContext)
      throws DelegationException {
    if (isAbleToHandleExceptions(executionContext)) {
      if (exceptionHandlers != null) {
        try {
          ExceptionHandler exceptionHandler = findExceptionHandler(exception);
          if (exceptionHandler != null) {
            executionContext.setException(exception);
            exceptionHandler.handleException(this, executionContext);
            return;
          }
        } catch (Exception e) {
          // NOTE that Error's are not caught because that might halt the JVM
          // and mask the original Error.
          exception = e;
        }
      }

      GraphElement parent = getParent();
      // if this graph element has a parent
      if ((parent != null) && (!equals(parent))) {
        // raise to the parent
        parent.raiseException(exception, executionContext);
        return;
      }
    }

    // rollback the actions
    // rollbackActions(executionContext);

    // if there is no parent we need to throw a delegation exception to the client
    throw exception instanceof JbpmException ? (JbpmException) exception : new DelegationException(
        exception, executionContext);
  }

  /**
   * Tells whether the given context is valid for exception handling by checking for:
   * <ul>
   * <li>the absence of a previous exception</li>
   * <li>an active transaction, or no transaction at all</li>
   * </ul>
   */
  private static boolean isAbleToHandleExceptions(ExecutionContext executionContext) {
    /* if an exception is already set, we are already handling an exception;
     * in this case don't give the exception to the handlers but throw it to the client
     * see https://jira.jboss.org/jira/browse/JBPM-1887 */
    if (executionContext.getException() != null)
      return false;

    /* check whether the transaction is still active before scanning the exception handlers.
     * that way we can load the exception handlers lazily
     * see https://jira.jboss.org/jira/browse/JBPM-1775 */
    JbpmContext jbpmContext = executionContext.getJbpmContext();
    if (jbpmContext != null) {
      Services services = jbpmContext.getServices();
      if (services != null) {
        Service service = services.getPersistenceService();
        if (service instanceof DbPersistenceService) {
          DbPersistenceService persistenceService = (DbPersistenceService) service;
          return persistenceService.isTransactionActive() || persistenceService.getTransaction() == null;
        }
      }
    }

    // no transaction detected, probably running in memory only
    return true;
  }

  protected ExceptionHandler findExceptionHandler(Throwable exception) {
    ExceptionHandler exceptionHandler = null;

    if (exceptionHandlers != null) {
      Iterator iter = exceptionHandlers.iterator();
      while (iter.hasNext() && (exceptionHandler == null)) {
        ExceptionHandler candidate = (ExceptionHandler) iter.next();
        if (candidate.matches(exception)) {
          exceptionHandler = candidate;
        }
      }
    }

    return exceptionHandler;
  }

  public GraphElement getParent() {
    return processDefinition;
  }

  /**
   * @return all the parents of this graph element ordered by age.
   */
  public List getParents() {
    List parents = new ArrayList();
    GraphElement parent = getParent();
    if (parent != null) {
      parent.addParentChain(parents);
    }
    return parents;
  }

  /**
   * @return this graph element plus all the parents ordered by age.
   */
  public List getParentChain() {
    List parents = new ArrayList();
    this.addParentChain(parents);
    return parents;
  }

  void addParentChain(List parentChain) {
    parentChain.add(this);
    GraphElement parent = getParent();
    if (parent != null) {
      parent.addParentChain(parentChain);
    }
  }

  public String toString() {
    String className = getClass().getName();
    className = className.substring(className.lastIndexOf('.') + 1);
    if (name != null) {
      className = className + "(" + name + ")";
    } else {
      className = className + "(" + Integer.toHexString(System.identityHashCode(this)) + ")";
    }
    return className;
  }

  // equals ///////////////////////////////////////////////////////////////////
  // hack to support comparing hibernate proxies against the real objects
  // since this always falls back to ==, we don't need to overwrite the hashcode
  public boolean equals(Object o) {
    return EqualsUtil.equals(this, o);
  }

  // getters and setters //////////////////////////////////////////////////////

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ProcessDefinition getProcessDefinition() {
    return processDefinition;
  }

  public void setProcessDefinition(ProcessDefinition processDefinition) {
    this.processDefinition = processDefinition;
  }

  // logger ///////////////////////////////////////////////////////////////////
  private static final Log log = LogFactory.getLog(GraphElement.class);
}
