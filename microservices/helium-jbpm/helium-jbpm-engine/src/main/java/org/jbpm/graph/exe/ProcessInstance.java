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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.caib.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import org.jbpm.JbpmException;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Identifiable;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.log.ProcessInstanceCreateLog;
import org.jbpm.graph.log.ProcessInstanceEndLog;
import org.jbpm.job.CleanUpProcessJob;
import org.jbpm.logging.exe.LoggingInstance;
import org.jbpm.logging.log.ProcessLog;
import org.jbpm.module.def.ModuleDefinition;
import org.jbpm.module.exe.ModuleInstance;
import org.jbpm.msg.MessageService;
import org.jbpm.svc.Services;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;
import org.jbpm.util.Clock;
import org.jbpm.util.EqualsUtil;


/**
 * is one execution of a {@link org.jbpm.graph.def.ProcessDefinition}. To create a new process execution of a process
 * definition, just use the {@link #ProcessInstance(ProcessDefinition)}.
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ProcessInstance implements Identifiable, Serializable
{

  private static final long serialVersionUID = 1L;

  long id;
  int version;
  protected String key;
  protected Date start;
  protected Date end;
  protected ProcessDefinition processDefinition;
  protected Token rootToken;
  protected Token superProcessToken;
  protected boolean isSuspended;
  protected Map instances;
  protected Map transientInstances;
  protected List runtimeActions;
  /** not persisted */
  protected List cascadeProcessInstances;
  protected ProcessInstanceExpedient expedient;

  // constructors /////////////////////////////////////////////////////////////

  public ProcessInstance()
  {
  }

  /**
   * creates a new process instance for the given process definition, puts the root-token (=main path of execution) in
   * the start state and executes the initial node. In case the initial node is a start-state, it will behave as a wait
   * state. For each of the optional module definitions contained in the {@link ProcessDefinition}, the corresponding
   * module instance will be created.
   * 
   * @throws JbpmException if processDefinition is null.
   */
  public ProcessInstance(ProcessDefinition processDefinition)
  {
    this(processDefinition, null, null);
  }

  /**
   * creates a new process instance for the given process definition, puts the root-token (=main path of execution) in
   * the start state and executes the initial node. In case the initial node is a start-state, it will behave as a wait
   * state. For each of the optional module definitions contained in the {@link ProcessDefinition}, the corresponding
   * module instance will be created.
   * 
   * @param variables will be inserted into the context variables after the context submodule has been created and
   *          before the process-start event is fired, which is also before the execution of the initial node.
   * @throws JbpmException if processDefinition is null.
   */
  public ProcessInstance(ProcessDefinition processDefinition, Map variables)
  {
    this(processDefinition, variables, null);
  }

  /**
   * creates a new process instance for the given process definition, puts the root-token (=main path of execution) in
   * the start state and executes the initial node. In case the initial node is a start-state, it will behave as a wait
   * state. For each of the optional module definitions contained in the {@link ProcessDefinition}, the corresponding
   * module instance will be created.
   * 
   * @param variables will be inserted into the context variables after the context submodule has been created and
   *          before the process-start event is fired, which is also before the execution of the initial node.
   * @throws JbpmException if processDefinition is null.
   */
  public ProcessInstance(ProcessDefinition processDefinition, Map variables, String key)
  {
    if (processDefinition == null)
      throw new JbpmException("can't create a process instance when processDefinition is null");

    // initialize the members
    this.processDefinition = processDefinition;
    this.rootToken = new Token(this);
    this.key = key;

    // if this process instance is created in the context of a persistent operation
    Services.assignId(this);

    // create the optional definitions
    addInitialModuleDefinitions(processDefinition);

    // add the creation log
    rootToken.addLog(new ProcessInstanceCreateLog());

    // set the variables
    addInitialContextVariables(variables);

    Node initialNode = rootToken.getNode();
    fireStartEvent(initialNode);
  }

  public void addInitialContextVariables(Map variables)
  {
    ContextInstance contextInstance = getContextInstance();
    if ((contextInstance != null) && (variables != null))
    {
      contextInstance.addVariables(variables);
    }
  }

  public void addInitialModuleDefinitions(ProcessDefinition processDefinition)
  {
    Map definitions = processDefinition.getDefinitions();
    // if the state-definition has optional definitions
    if (definitions != null)
    {
      instances = new HashMap();
      // loop over each optional definition
      Iterator iter = definitions.values().iterator();
      while (iter.hasNext())
      {
        ModuleDefinition definition = (ModuleDefinition)iter.next();
        // and create the corresponding optional instance
        ModuleInstance instance = definition.createInstance();
        if (instance != null)
        {
          addInstance(instance);
        }
      }
    }
  }

  public void fireStartEvent(Node initialNode)
  {
    this.start = Clock.getCurrentTime();
    
    // fire the process start event
    if (initialNode != null)
    {
      ExecutionContext executionContext = new ExecutionContext(rootToken);
      processDefinition.fireEvent(Event.EVENTTYPE_PROCESS_START, executionContext);

      // execute the start node
      initialNode.execute(executionContext);
    }
  }

  // optional module instances ////////////////////////////////////////////////

  /**
   * adds the given optional moduleinstance (bidirectional).
   */
  public ModuleInstance addInstance(ModuleInstance moduleInstance)
  {
    if (moduleInstance == null)
      throw new IllegalArgumentException("can't add a null moduleInstance to a process instance");
    if (instances == null)
      instances = new HashMap();
    instances.put(moduleInstance.getClass().getName(), moduleInstance);
    moduleInstance.setProcessInstance(this);
    return moduleInstance;
  }

  /**
   * removes the given optional moduleinstance (bidirectional).
   */
  public ModuleInstance removeInstance(ModuleInstance moduleInstance)
  {
    ModuleInstance removedModuleInstance = null;
    if (moduleInstance == null)
      throw new IllegalArgumentException("can't remove a null moduleInstance from a process instance");
    if (instances != null)
    {
      removedModuleInstance = (ModuleInstance)instances.remove(moduleInstance.getClass().getName());
      if (removedModuleInstance != null)
      {
        moduleInstance.setProcessInstance(null);
      }
    }
    return removedModuleInstance;
  }

  /**
   * looks up an optional module instance by its class.
   */
  public ModuleInstance getInstance(Class clazz)
  {
    ModuleInstance moduleInstance = null;
    if (instances != null)
    {
      moduleInstance = (ModuleInstance)instances.get(clazz.getName());
    }

    if (moduleInstance == null)
    {
      if (transientInstances == null)
        transientInstances = new HashMap();

      // client requested an instance that is not in the map of instances.
      // so we can safely assume that the client wants a transient instance
      moduleInstance = (ModuleInstance)transientInstances.get(clazz.getName());
      if (moduleInstance == null)
      {
        try
        {
          moduleInstance = (ModuleInstance)clazz.newInstance();
          moduleInstance.setProcessInstance(this);

        }
        catch (Exception e)
        {
          throw new JbpmException("couldn't instantiate transient module '" + clazz.getName() + "' with the default constructor");
        }
        transientInstances.put(clazz.getName(), moduleInstance);
      }
    }

    return moduleInstance;
  }

  /**
   * process instance extension for process variableInstances.
   */
  public ContextInstance getContextInstance()
  {
    return (ContextInstance)getInstance(ContextInstance.class);
  }

  /**
   * process instance extension for managing the tasks and actors.
   */
  public TaskMgmtInstance getTaskMgmtInstance()
  {
    return (TaskMgmtInstance)getInstance(TaskMgmtInstance.class);
  }

  /**
   * process instance extension for logging. Probably you don't need to access the logging instance directly. Mostly,
   * {@link Token#addLog(ProcessLog)} is sufficient and more convenient.
   */
  public LoggingInstance getLoggingInstance()
  {
    return (LoggingInstance)getInstance(LoggingInstance.class);
  }

  // operations ///////////////////////////////////////////////////////////////

  /**
   * instructs the main path of execution to continue by taking the default transition on the current node.
   * 
   * @throws IllegalStateException if the token is not active.
   */
  public void signal()
  {
    if (hasEnded())
    {
      throw new IllegalStateException("couldn't signal token : token has ended");
    }
    rootToken.signal();
  }

  /**
   * instructs the main path of execution to continue by taking the specified transition on the current node.
   * 
   * @throws IllegalStateException if the token is not active.
   */
  public void signal(String transitionName)
  {
    if (hasEnded())
    {
      throw new IllegalStateException("couldn't signal token : token has ended");
    }
    rootToken.signal(transitionName);
  }

  /**
   * instructs the main path of execution to continue by taking the specified transition on the current node.
   * 
   * @throws IllegalStateException if the token is not active.
   */
  public void signal(Transition transition)
  {
    if (hasEnded())
    {
      throw new IllegalStateException("couldn't signal token : token has ended");
    }
    rootToken.signal(transition);
  }

  /**
   * ends (=cancels) this process instance and all the tokens in it.
   */
  public void end()
  {
    // end the main path of execution
    rootToken.end();

    if (end == null)
    {
      // mark this process instance as ended
      end = Clock.getCurrentTime();
      
      // /////////////////////////////
      // Afegeix aquesta instància de procés a la llista per a verificar la seva
      // finalització.
      // Si no ho feim així quan es finalitza una instància de procés des d'un altre
      // executionContext l'expedient no es queda amb estat finalitzat.
      // /////////////////////////////
      Jbpm3HeliumBridge.getInstanceService().afegirInstanciaProcesPerVerificarFinalitzacio(
    		  new Long(id).toString());

      // fire the process-end event
      ExecutionContext executionContext = new ExecutionContext(rootToken);
      processDefinition.fireEvent(Event.EVENTTYPE_PROCESS_END, executionContext);

      // add the process instance end log
      rootToken.addLog(new ProcessInstanceEndLog());

      // check if this process was started as a subprocess of a super process
      if (superProcessToken != null)
      {
        addCascadeProcessInstance(superProcessToken.getProcessInstance());
        // /////////////////////////////
        // Només fa el signal si el superProcessToken no està finalitzat.
        // Si no feim això falla al aturar un expedient amb subprocessos
        // quan s'arriba a un node end-state i l'opció end-complete-process
        // està activada.
        // /////////////////////////////
        if (superProcessToken.getEnd() == null) {
          ExecutionContext superExecutionContext = new ExecutionContext(superProcessToken);
          superExecutionContext.setSubProcessInstance(this);
          superProcessToken.signal(superExecutionContext);
        }
      }

      // make sure all the timers for this process instance are canceled after the process end updates are posted to the
      // database
      // NOTE Only timers should be deleted, messages should be kept.
      MessageService messageService = (MessageService)Services.getCurrentService(Services.SERVICENAME_MESSAGE, false);
      if (messageService != null)
      {
        CleanUpProcessJob job = new CleanUpProcessJob(this);
        job.setDueDate(new Date());
        messageService.send(job);
      }
    }
  }

  /**
   * suspends this execution. This will make sure that tasks, timers and messages related to this process instance will
   * not show up in database queries.
   * 
   * @see #resume()
   */
  public void suspend()
  {
    isSuspended = true;
    rootToken.suspend();
  }

  /**
   * resumes a suspended execution. All timers that have been suspended might fire if the duedate has been passed. If an
   * admin resumes a process instance, the option should be offered to update, remove and create the timers and messages
   * related to this process instance.
   * 
   * @see #suspend()
   */
  public void resume()
  {
    isSuspended = false;
    rootToken.resume();
  }

  // runtime actions //////////////////////////////////////////////////////////

  /**
   * adds an action to be executed upon a process event in the future.
   */
  public RuntimeAction addRuntimeAction(RuntimeAction runtimeAction)
  {
    if (runtimeAction == null)
      throw new IllegalArgumentException("can't add a null runtimeAction to a process instance");
    if (runtimeActions == null)
      runtimeActions = new ArrayList();
    runtimeActions.add(runtimeAction);
    runtimeAction.processInstance = this;
    return runtimeAction;
  }

  /**
   * removes a runtime action.
   */
  public RuntimeAction removeRuntimeAction(RuntimeAction runtimeAction)
  {
    RuntimeAction removedRuntimeAction = null;
    if (runtimeAction == null)
      throw new IllegalArgumentException("can't remove a null runtimeAction from an process instance");
    if (runtimeActions != null)
    {
      if (runtimeActions.remove(runtimeAction))
      {
        removedRuntimeAction = runtimeAction;
        runtimeAction.processInstance = null;
      }
    }
    return removedRuntimeAction;
  }

  /**
   * is the list of all runtime actions.
   */
  public List getRuntimeActions()
  {
    return runtimeActions;
  }

  // various information retrieval methods ////////////////////////////////////

  /**
   * tells if this process instance is still active or not.
   */
  public boolean hasEnded()
  {
    return (end != null);
  }

  /**
   * calculates if this process instance has still options to continue.
   */
  public boolean isTerminatedImplicitly()
  {
    boolean isTerminatedImplicitly = true;
    if (end == null)
    {
      isTerminatedImplicitly = rootToken.isTerminatedImplicitly();
    }
    return isTerminatedImplicitly;
  }

  /**
   * looks up the token in the tree, specified by the slash-separated token path.
   * 
   * @param tokenPath is a slash-separated name that specifies a token in the tree.
   * @return the specified token or null if the token is not found.
   */
  public Token findToken(String tokenPath)
  {
    return (rootToken != null ? rootToken.findToken(tokenPath) : null);
  }

  /**
   * collects all instances for this process instance.
   */
  public List findAllTokens()
  {
    List tokens = new ArrayList();
    tokens.add(rootToken);
    rootToken.collectChildrenRecursively(tokens);
    return tokens;
  }

  void addCascadeProcessInstance(ProcessInstance cascadeProcessInstance)
  {
    if (cascadeProcessInstances == null)
    {
      cascadeProcessInstances = new ArrayList();
    }
    cascadeProcessInstances.add(cascadeProcessInstance);
  }

  public Collection removeCascadeProcessInstances()
  {
    Collection removed = cascadeProcessInstances;
    cascadeProcessInstances = null;
    return removed;
  }

  // equals ///////////////////////////////////////////////////////////////////
  // hack to support comparing hibernate proxies against the real objects
  // since this always falls back to ==, we don't need to overwrite the hashcode
  public boolean equals(Object o)
  {
    return EqualsUtil.equals(this, o);
  }

  public String toString()
  {
    return "ProcessInstance" + (key != null ? '(' + key + ')' : "@" + Integer.toHexString(hashCode()));
  }

  // getters and setters //////////////////////////////////////////////////////

  public long getId()
  {
    return id;
  }

  public Token getRootToken()
  {
    return rootToken;
  }

  public Date getStart()
  {
    return start;
  }

  public Date getEnd()
  {
    return end;
  }

  public Map getInstances()
  {
    return instances;
  }

  public ProcessDefinition getProcessDefinition()
  {
    return processDefinition;
  }

  public Token getSuperProcessToken()
  {
    return superProcessToken;
  }

  public void setSuperProcessToken(Token superProcessToken)
  {
    this.superProcessToken = superProcessToken;
  }

  public boolean isSuspended()
  {
    return isSuspended;
  }

  public int getVersion()
  {
    return version;
  }

  public void setVersion(int version)
  {
    this.version = version;
  }

  public void setEnd(Date end)
  {
    this.end = end;
  }

  public void setProcessDefinition(ProcessDefinition processDefinition)
  {
    this.processDefinition = processDefinition;
  }

  public void setRootToken(Token rootToken)
  {
    this.rootToken = rootToken;
  }

  public void setStart(Date start)
  {
    this.start = start;
  }

  /** a unique business key */
  public String getKey()
  {
    return key;
  }

  /** set the unique business key */
  public void setKey(String key)
  {
    this.key = key;
  }

  public ProcessInstanceExpedient getExpedient() {
	return expedient;
  }

  public void setExpedient(ProcessInstanceExpedient expedient) {
	this.expedient = expedient;
  }

  // private static Log log = LogFactory.getLog(ProcessInstance.class);
}
