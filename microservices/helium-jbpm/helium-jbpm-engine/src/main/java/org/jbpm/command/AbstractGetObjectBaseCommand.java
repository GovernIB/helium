package org.jbpm.command;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * abstract base class for "get" commands which also implements default pre-fetching.
 * 
 * Note: pre-fetching logs is not possible here, so you have to load Logs explicitly
 * with GetProcessInstanceLogCommand
 * 
 * @author Bernd Ruecker (bernd.ruecker@camunda.com)
 */
public abstract class AbstractGetObjectBaseCommand extends AbstractBaseCommand
{

  static final Log log = LogFactory.getLog(AbstractGetObjectBaseCommand.class);

  private static final long serialVersionUID = 1L;

  /**
   * if true, all process variables in the context (process instance / task) are prefetched too
   */
  private boolean includeAllVariables = false;

  /**
   * specify the names of the variables to prefetch
   */
  private String[] variablesToInclude = new String[0];

  private transient JbpmContext jbpmContext;

  public AbstractGetObjectBaseCommand()
  {
  }

  public AbstractGetObjectBaseCommand(boolean includeAllVariables, boolean includeLogs)
  {
    this.includeAllVariables = includeAllVariables;
  }

  public AbstractGetObjectBaseCommand(String[] variablesToInclude)
  {
    this.variablesToInclude = variablesToInclude;
  }

  public void retrieveTaskInstanceDetails(TaskInstance ti)
  {
    try
    {
      ti.getToken().getProcessInstance().getProcessDefinition().getName();

      // in TaskInstances created with jbpm 3.1, this association was
      // not present!
      ti.setProcessInstance(ti.getToken().getProcessInstance());
      ti.getToken().getNode().getName();
      ti.getTask().getName();

      ti.getAvailableTransitions();

      retrieveVariables(ti);
    }
    catch (Exception ex)
    {
      log.warn("exception while retrieving task instance data for task instance " + ti.getId(), ex);
    }
  }

  public ProcessInstance retrieveProcessInstance(ProcessInstance pi)
  {
    try
    {
      pi.getProcessDefinition().getName();
      retrieveToken(pi.getRootToken());

      // load at least the super process id and token id
      if (pi.getSuperProcessToken() != null)
      {
        pi.getSuperProcessToken().getId();
        pi.getSuperProcessToken().getProcessInstance().getId();
      }

      retrieveVariables(pi);
    }
    catch (Exception ex)
    {
      log.warn("exception while retrieving process instance data for process instance " + pi.getId(), ex);
    }
    return pi;
  }

  @SuppressWarnings("rawtypes")
public ProcessDefinition retrieveProcessDefinition(ProcessDefinition pd)
  {
    try
    {
      pd.getName();
      // often needed to start a process:
      Iterator iter = pd.getStartState().getLeavingTransitions().iterator();
      while (iter.hasNext())
      {
        Transition t = (Transition)iter.next();
        t.getName();
      }
    }
    catch (Exception ex)
    {
      log.warn("exception while retrieving process instance data for process definiton " + pd.getName(), ex);
    }
    return pd;
  }
  @SuppressWarnings("rawtypes")
  protected void retrieveToken(Token t)
  {
    retrieveNode(t.getNode());
    t.getAvailableTransitions();

    // if (includeLogs)
    // t.getProcessInstance().getLoggingInstance().
    if (t.getChildren() != null) {
	    Iterator iter = t.getChildren().values().iterator();
	    while (iter.hasNext())
	    {
	      retrieveToken((Token)iter.next());
	    }
    }
  }

  protected void retrieveNode(Node n)
  {
	if (n != null) {
	    n.getName();
	    n.getLeavingTransitions();
	    if (n.getSuperState() != null)
	      retrieveNode(n.getSuperState());
	}
  }

  public void retrieveVariables(ProcessInstance pi)
  {
    if (includeAllVariables)
    {
      pi.getContextInstance().getVariables();
    }
    else
    {
      for (int i = 0; i < variablesToInclude.length; i++)
      {
        pi.getContextInstance().getVariable(variablesToInclude[i]);
      }
    }
  }

  public void retrieveVariables(TaskInstance ti)
  {
    if (includeAllVariables)
    {
      ti.getVariables();
    }
    else
    {
      for (int i = 0; i < variablesToInclude.length; i++)
      {
        ti.getVariable(variablesToInclude[i]);
      }
    }
  }

  public boolean isIncludeAllVariables()
  {
    return includeAllVariables;
  }

  public void setIncludeAllVariables(boolean includeAllVariables)
  {
    this.includeAllVariables = includeAllVariables;
  }

  public String[] getVariablesToInclude()
  {
    return variablesToInclude;
  }

  public void setVariablesToInclude(String[] variablesToInclude)
  {
    this.variablesToInclude = variablesToInclude;
  }

  public void setVariablesToInclude(String variableToInclude)
  {
    this.variablesToInclude = new String[] { variableToInclude };
  }

  protected JbpmContext getJbpmContext()
  {
    return jbpmContext;
  }

  protected void setJbpmContext(JbpmContext jbpmContext)
  {
    this.jbpmContext = jbpmContext;
  }
  
  // methods for fluent programming
  
  public AbstractGetObjectBaseCommand variablesToInclude(String[] variablesToInclude)
  {
    setVariablesToInclude(variablesToInclude);
    return this;
  }

  public AbstractGetObjectBaseCommand variablesToInclude(String variableToInclude)
  {
    setVariablesToInclude(variableToInclude);
    return this;
  }
  
  public AbstractGetObjectBaseCommand includeAllVariables(boolean includeAllVariables)
  {
    setIncludeAllVariables( includeAllVariables );
    return this;
  }

}