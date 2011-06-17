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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jbpm.JbpmException;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.logging.log.ProcessLog;

/**
 * are the graph related database operations.
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class GraphSession
{

  JbpmSession jbpmSession = null;
  Session session = null;

  public GraphSession(JbpmSession jbpmSession)
  {
    this.jbpmSession = jbpmSession;
    this.session = jbpmSession.getSession();
  }

  public GraphSession(Session session)
  {
    this.session = session;
    this.jbpmSession = new JbpmSession(session);
  }

  // process definitions //////////////////////////////////////////////////////

  public void deployProcessDefinition(ProcessDefinition processDefinition)
  {
    String processDefinitionName = processDefinition.getName();
    // if the process definition has a name (process versioning only applies to named process definitions)
    if (processDefinitionName != null)
    {
      // find the current latest process definition
      ProcessDefinition previousLatestVersion = findLatestProcessDefinition(processDefinitionName);
      // if there is a current latest process definition
      if (previousLatestVersion != null)
      {
        // take the next version number
        processDefinition.setVersion(previousLatestVersion.getVersion() + 1);
      }
      else
      {
        // start from 1
        processDefinition.setVersion(1);
      }

      session.save(processDefinition);

    }
    else
    {
      throw new JbpmException("process definition does not have a name");
    }
  }

  /**
   * saves the process definitions. this method does not assign a version number. that is the responsibility of the
   * {@link #deployProcessDefinition(ProcessDefinition) deployProcessDefinition} method.
   */
  public void saveProcessDefinition(ProcessDefinition processDefinition)
  {
    try
    {
      session.save(processDefinition);
    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't save process definition '" + processDefinition + "'", e);
    }
  }

  /**
   * loads a process definition from the database by the identifier.
   * 
   * @throws JbpmException in case the referenced process definition doesn't exist.
   */
  public ProcessDefinition loadProcessDefinition(long processDefinitionId)
  {
    try
    {
      return (ProcessDefinition)session.load(ProcessDefinition.class, new Long(processDefinitionId));
    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't load process definition '" + processDefinitionId + "'", e);
    }
  }

  /**
   * gets a process definition from the database by the identifier.
   * 
   * @return the referenced process definition or null in case it doesn't exist.
   */
  public ProcessDefinition getProcessDefinition(long processDefinitionId)
  {
    try
    {
      return (ProcessDefinition)session.get(ProcessDefinition.class, new Long(processDefinitionId));
    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't get process definition '" + processDefinitionId + "'", e);
    }
  }

  /**
   * queries the database for a process definition with the given name and version.
   */
  public ProcessDefinition findProcessDefinition(String name, int version)
  {
    ProcessDefinition processDefinition = null;
    try
    {
      Query query = session.getNamedQuery("GraphSession.findProcessDefinitionByNameAndVersion");
      query.setString("name", name);
      query.setInteger("version", version);
      processDefinition = (ProcessDefinition)query.uniqueResult();
    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't get process definition with name '" + name + "' and version '" + version + "'", e);
    }
    return processDefinition;
  }

  /**
   * queries the database for the latest version of a process definition with the given name.
   */
  public ProcessDefinition findLatestProcessDefinition(String name)
  {
    ProcessDefinition processDefinition = null;
    try
    {
      Query query = session.getNamedQuery("GraphSession.findLatestProcessDefinitionQuery");
      query.setString("name", name);
      query.setMaxResults(1);
      processDefinition = (ProcessDefinition)query.uniqueResult();
    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't find process definition '" + name + "'", e);
    }
    return processDefinition;
  }

  /**
   * queries the database for the latest version of each process definition. Process definitions are distinct by name.
   */
  public List findLatestProcessDefinitions()
  {
    List processDefinitions = new ArrayList();
    Map processDefinitionsByName = new HashMap();
    try
    {
      Query query = session.getNamedQuery("GraphSession.findAllProcessDefinitions");
      Iterator iter = query.list().iterator();
      while (iter.hasNext())
      {
        ProcessDefinition processDefinition = (ProcessDefinition)iter.next();
        String processDefinitionName = processDefinition.getName();
        ProcessDefinition previous = (ProcessDefinition)processDefinitionsByName.get(processDefinitionName);
        if ((previous == null) || (previous.getVersion() < processDefinition.getVersion()))
        {
          processDefinitionsByName.put(processDefinitionName, processDefinition);
        }
      }
      processDefinitions = new ArrayList(processDefinitionsByName.values());
    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't find latest versions of process definitions", e);
    }
    return processDefinitions;
  }

  /**
   * queries the database for all process definitions, ordered by name (ascending), then by version (descending).
   */
  public List findAllProcessDefinitions()
  {
    try
    {
      Query query = session.getNamedQuery("GraphSession.findAllProcessDefinitions");
      return query.list();
    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't find all process definitions", e);
    }
  }

  /**
   * queries the database for all versions of process definitions with the given name, ordered by version (descending).
   */
  public List findAllProcessDefinitionVersions(String name)
  {
    try
    {
      Query query = session.getNamedQuery("GraphSession.findAllProcessDefinitionVersions");
      query.setString("name", name);
      return query.list();
    }
    catch (HibernateException e)
    {
      log.error(e);
      throw new JbpmException("couldn't find all versions of process definition '" + name + "'", e);
    }
  }

  public void deleteProcessDefinition(long processDefinitionId)
  {
    deleteProcessDefinition(loadProcessDefinition(processDefinitionId));
  }

  public void deleteProcessDefinition(ProcessDefinition processDefinition)
  {
    if (processDefinition == null)
    {
      throw new JbpmException("processDefinition is null");
    }
    try
    {
      // delete all the process instances of this definition
      for (ProcessInstance processInstance; ((processInstance = findNextProcessInstance(processDefinition)) != null);)
      {
        deleteProcessInstance(processInstance);
      }

      List referencingProcessStates = findReferencingProcessStates(processDefinition);
      for (Iterator iter = referencingProcessStates.iterator(); iter.hasNext();)
      {
        ProcessState processState = (ProcessState)iter.next();
        processState.setSubProcessDefinition(null);
      }

      // then delete the process definition
      session.delete(processDefinition);

    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't delete process definition '" + processDefinition.getId() + "'", e);
    }
  }

  protected ProcessInstance findNextProcessInstance(ProcessDefinition processDefinition)
  {
    return (ProcessInstance)session.createCriteria(ProcessInstance.class).add(Restrictions.eq("processDefinition", processDefinition)).setMaxResults(1)
        .uniqueResult();
  }

  protected List findReferencingProcessStates(ProcessDefinition subProcessDefinition)
  {
    Query query = session.getNamedQuery("GraphSession.findReferencingProcessStates");
    query.setEntity("subProcessDefinition", subProcessDefinition);
    return query.list();
  }

  // process instances ////////////////////////////////////////////////////////

  /**
   * @deprecated use {@link org.jbpm.JbpmContext#save(ProcessInstance)} instead.
   * @throws UnsupportedOperationException
   */
  public void saveProcessInstance(ProcessInstance processInstance)
  {
    throw new UnsupportedOperationException("use JbpmContext.save(ProcessInstance) instead");
  }

  /**
   * loads a process instance from the database by the identifier. This throws an exception in case the process instance
   * doesn't exist.
   * 
   * @see #getProcessInstance(long)
   * @throws JbpmException in case the process instance doesn't exist.
   */
  public ProcessInstance loadProcessInstance(long processInstanceId)
  {
    try
    {
      ProcessInstance processInstance = (ProcessInstance)session.load(ProcessInstance.class, new Long(processInstanceId));
      return processInstance;
    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't load process instance '" + processInstanceId + "'", e);
    }
  }

  /**
   * gets a process instance from the database by the identifier. This method returns null in case the given process
   * instance doesn't exist.
   */
  public ProcessInstance getProcessInstance(long processInstanceId)
  {
    try
    {
      ProcessInstance processInstance = (ProcessInstance)session.get(ProcessInstance.class, new Long(processInstanceId));
      return processInstance;
    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't get process instance '" + processInstanceId + "'", e);
    }
  }

  /**
   * loads a token from the database by the identifier.
   * 
   * @return the token.
   * @throws JbpmException in case the referenced token doesn't exist.
   */
  public Token loadToken(long tokenId)
  {
    try
    {
      Token token = (Token)session.load(Token.class, new Long(tokenId));
      return token;
    }
    catch (Exception e)
    {
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
  public Token getToken(long tokenId)
  {
    try
    {
      Token token = (Token)session.get(Token.class, new Long(tokenId));
      return token;
    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't get token '" + tokenId + "'", e);
    }
  }

  /**
   * locks a process instance in the database.
   */
  public void lockProcessInstance(long processInstanceId)
  {
    lockProcessInstance(loadProcessInstance(processInstanceId));
  }

  /**
   * locks a process instance in the database.
   */
  public void lockProcessInstance(ProcessInstance processInstance)
  {
    try
    {
      session.lock(processInstance, LockMode.UPGRADE);
    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't lock process instance '" + processInstance.getId() + "'", e);
    }
  }

  /**
   * fetches all processInstances for the given process definition from the database. The returned list of process
   * instances is sorted start date, youngest first.
   */
  public List findProcessInstances(long processDefinitionId)
  {
    List processInstances = null;
    try
    {
      Query query = session.getNamedQuery("GraphSession.findAllProcessInstancesForADefinition");
      query.setLong("processDefinitionId", processDefinitionId);
      processInstances = query.list();

    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't load process instances for process definition '" + processDefinitionId + "'", e);
    }
    return processInstances;
  }

  public void deleteProcessInstance(long processInstanceId)
  {
    deleteProcessInstance(loadProcessInstance(processInstanceId));
  }

  public void deleteProcessInstance(ProcessInstance processInstance)
  {
    deleteProcessInstance(processInstance, true, true);
  }

  public void deleteProcessInstance(ProcessInstance processInstance, boolean includeTasks, boolean includeJobs)
  {
    if (processInstance == null)
      throw new JbpmException("processInstance is null in JbpmSession.deleteProcessInstance()");
    log.debug("deleting process instance " + processInstance.getId());

    try
    {
      // jobs
      if (includeJobs)
      {
        log.debug("deleting jobs for process instance " + processInstance.getId());
        Query query = session.getNamedQuery("GraphSession.deleteJobsForProcessInstance");
        query.setEntity("processInstance", processInstance);
        query.executeUpdate();
      }

      // tasks
      if (includeTasks)
      {
        Query query = session.getNamedQuery("GraphSession.findTaskInstanceIdsForProcessInstance");
        query.setEntity("processInstance", processInstance);
        List taskInstanceIds = query.list();

        if ((taskInstanceIds != null) && (!taskInstanceIds.isEmpty()))
        {
          log.debug("deleting tasks " + taskInstanceIds + " for process instance " + processInstance.getId());
          query = session.getNamedQuery("GraphSession.deleteTaskInstancesById");
          query.setParameterList("taskInstanceIds", taskInstanceIds);
        }
      }

      // delete the logs
      log.debug("deleting logs for process instance " + processInstance.getId());
      deleteLogs(processInstance);

      // delete the tokens and subprocess instances
      log.debug("deleting subprocesses for process instance " + processInstance.getId());
      deleteSubProcesses(processInstance.getRootToken());

      // null out the parent process token
      Token superProcessToken = processInstance.getSuperProcessToken();
      if (superProcessToken != null)
      {
        log.debug("nulling property subProcessInstance in superProcessToken " + superProcessToken.getId() + " which is referencing the process instance "
            + processInstance.getId() + " which is being deleted");
        superProcessToken.setSubProcessInstance(null);
      }

      // add the process instance
      log.debug("hibernate session delete for process instance " + processInstance.getId());
      session.delete(processInstance);
    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't delete process instance '" + processInstance.getId() + "'", e);
    }
  }

  void deleteLogs(ProcessInstance processInstance)
  {
    Query query = session.getNamedQuery("GraphSession.findLogsForProcessInstance");
    query.setEntity("processInstance", processInstance);
    List logs = query.list();
    Iterator iter = logs.iterator();
    while (iter.hasNext())
    {
      ProcessLog processLog = (ProcessLog)iter.next();
      session.delete(processLog);
    }
  }

  void deleteSubProcesses(Token token)
  {
    if (token != null)
    {
      Query query = session.getNamedQuery("GraphSession.findSubProcessInstances");
      query.setEntity("processInstance", token.getProcessInstance());
      List processInstances = query.list();

      if (processInstances == null || processInstances.isEmpty())
      {
        log.debug("no subprocesses to delete for token " + token.getId());
        return;
      }

      Iterator iter = processInstances.iterator();
      while (iter.hasNext())
      {
        ProcessInstance subProcessInstance = (ProcessInstance)iter.next();
        subProcessInstance.getSuperProcessToken().setSubProcessInstance(null);
        subProcessInstance.setSuperProcessToken(null);
        token.setSubProcessInstance(null);
        log.debug("deleting sub process " + subProcessInstance.getId());
        deleteProcessInstance(subProcessInstance);
      }
    }
  }

  public static class AverageNodeTimeEntry
  {
    private long nodeId;
    private String nodeName;
    private int count;
    private long averageDuration;
    private long minDuration;
    private long maxDuration;

    public long getNodeId()
    {
      return nodeId;
    }

    public void setNodeId(final long nodeId)
    {
      this.nodeId = nodeId;
    }

    public String getNodeName()
    {
      return nodeName;
    }

    public void setNodeName(final String nodeName)
    {
      this.nodeName = nodeName;
    }

    public int getCount()
    {
      return count;
    }

    public void setCount(final int count)
    {
      this.count = count;
    }

    public long getAverageDuration()
    {
      return averageDuration;
    }

    public void setAverageDuration(final long averageDuration)
    {
      this.averageDuration = averageDuration;
    }

    public long getMinDuration()
    {
      return minDuration;
    }

    public void setMinDuration(final long minDuration)
    {
      this.minDuration = minDuration;
    }

    public long getMaxDuration()
    {
      return maxDuration;
    }

    public void setMaxDuration(final long maxDuration)
    {
      this.maxDuration = maxDuration;
    }
  }

  public List calculateAverageTimeByNode(final long processDefinitionId, final long minumumDurationMillis)
  {
    List results = null;
    try
    {
      Query query = session.getNamedQuery("GraphSession.calculateAverageTimeByNode");
      query.setLong("processDefinitionId", processDefinitionId);
      query.setDouble("minimumDuration", minumumDurationMillis);
      List listResults = query.list();

      if (listResults != null)
      {
        results = new ArrayList();
        Iterator iter = listResults.iterator();
        while (iter.hasNext())
        {
          Object[] values = (Object[])iter.next();

          AverageNodeTimeEntry averageNodeTimeEntry = new AverageNodeTimeEntry();
          averageNodeTimeEntry.setNodeId(((Number)values[0]).longValue());
          averageNodeTimeEntry.setNodeName((String)values[1]);
          averageNodeTimeEntry.setCount(((Number)values[2]).intValue());
          averageNodeTimeEntry.setAverageDuration(((Number)values[3]).longValue());
          averageNodeTimeEntry.setMinDuration(((Number)values[4]).longValue());
          averageNodeTimeEntry.setMaxDuration(((Number)values[5]).longValue());

          results.add(averageNodeTimeEntry);
        }
      }
    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't load process instances for process definition '" + processDefinitionId + "'", e);
    }
    return results;
  }

  public List findActiveNodesByProcessInstance(ProcessInstance processInstance)
  {
    List results = null;
    try
    {
      Query query = session.getNamedQuery("GraphSession.findActiveNodesByProcessInstance");
      query.setEntity("processInstance", processInstance);
      results = query.list();

    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't active nodes for process instance '" + processInstance + "'", e);
    }
    return results;
  }

  public ProcessInstance getProcessInstance(ProcessDefinition processDefinition, String key)
  {
    ProcessInstance processInstance = null;
    try
    {
      Query query = session.getNamedQuery("GraphSession.findProcessInstanceByKey");
      query.setEntity("processDefinition", processDefinition);
      query.setString("key", key);
      processInstance = (ProcessInstance)query.uniqueResult();

    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't get process instance with key '" + key + "'", e);
    }
    return processInstance;
  }

  public ProcessInstance loadProcessInstance(ProcessDefinition processDefinition, String key)
  {
    ProcessInstance processInstance = null;
    try
    {
      Query query = session.getNamedQuery("GraphSession.findProcessInstanceByKey");
      query.setEntity("processDefinition", processDefinition);
      query.setString("key", key);
      processInstance = (ProcessInstance)query.uniqueResult();
      if (processInstance == null)
      {
        throw new JbpmException("no process instance was found with key " + key);
      }

    }
    catch (Exception e)
    {
      log.error(e);
      jbpmSession.handleException();
      throw new JbpmException("couldn't load process instance with key '" + key + "'", e);
    }
    return processInstance;
  }

  private static final Log log = LogFactory.getLog(GraphSession.class);
}
