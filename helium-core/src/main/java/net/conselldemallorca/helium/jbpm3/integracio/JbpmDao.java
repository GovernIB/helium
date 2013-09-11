/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.conselldemallorca.helium.core.model.exception.DeploymentException;

import org.hibernate.SessionFactory;
import org.jbpm.command.CancelTokenCommand;
import org.jbpm.command.ChangeProcessInstanceVersionCommand;
import org.jbpm.command.CommandService;
import org.jbpm.command.DeleteProcessDefinitionCommand;
import org.jbpm.command.DeployProcessCommand;
import org.jbpm.command.GetProcessInstanceCommand;
import org.jbpm.command.GetProcessInstancesCommand;
import org.jbpm.command.GetTaskInstanceCommand;
import org.jbpm.command.SignalCommand;
import org.jbpm.command.TaskInstanceEndCommand;
import org.jbpm.file.def.FileDefinition;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.job.Timer;
import org.jbpm.logging.log.ProcessLog;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Dao per a l'accés a la funcionalitat de jBPM3
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class JbpmDao {

	private CommandService commandService;



	public JbpmProcessDefinition desplegar(
			String nomArxiu,
			byte[] contingut) {
		JbpmProcessDefinition resposta = null;
		DeployProcessCommand command;
		if (nomArxiu.endsWith(".xml")) {
			try {
				command = new DeployProcessCommand(new String(contingut));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ZipOutputStream zos = new ZipOutputStream(baos);
				byte[] data = new byte[1024];
				InputStream is = new ByteArrayInputStream(contingut);
				zos.putNextEntry(new ZipEntry("processdefinition.xml"));
				int count;
				while ((count = is.read(data, 0, 1024)) != -1)
					zos.write(data, 0, count);
		        zos.closeEntry();
				zos.close();
				command = new DeployProcessCommand(baos.toByteArray());
			} catch (Exception ex) {
				throw new DeploymentException("No s'ha pogut deplegar l'arxiu", ex);
			}
		} else if (nomArxiu.endsWith("ar")) {
			command = new DeployProcessCommand(contingut);
		} else {
			throw new DeploymentException("Arxiu amb extensió no suportada " + nomArxiu + ". Només es suporten les extensions .xml i .*ar");
		}
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		resposta = new JbpmProcessDefinition(processDefinition);
		return resposta;
	}

	public JbpmProcessDefinition getProcessDefinition(String jbpmId) {
		JbpmProcessDefinition resposta = null;
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		if (processDefinition != null)
			resposta = new JbpmProcessDefinition((ProcessDefinition)commandService.execute(command));
		return resposta;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmProcessDefinition> getSubProcessDefinitions(String jbpmId) {
		List<JbpmProcessDefinition> resposta = new ArrayList<JbpmProcessDefinition>();
		final long pdid = Long.parseLong(jbpmId);
		GetSubProcessDefinitionsCommand command = new GetSubProcessDefinitionsCommand(pdid);
		for (ProcessDefinition pd: (List<ProcessDefinition>)commandService.execute(command)) {
			resposta.add(new JbpmProcessDefinition(pd));
		}
		return resposta;
	}

	public JbpmProcessInstance getProcessInstance(String processInstanceId) {
		JbpmProcessInstance resposta = null;
		final long piid = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(piid);
		resposta = new JbpmProcessInstance((ProcessInstance)commandService.execute(command));
		return resposta;
	}

	public String getStartTaskName(String jbpmId) {
		String resposta = null;
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		if (processDefinition != null) {
			org.jbpm.taskmgmt.def.Task startTask = processDefinition.getTaskMgmtDefinition().getStartTask();
			if (startTask != null)
				resposta = startTask.getName();
		}
		return resposta;
	}

	@SuppressWarnings("unchecked")
	public List<String> getTaskNamesFromDeployedProcessDefinition(JbpmProcessDefinition dpd) {
		List<String> taskNames = new ArrayList<String>();
		ProcessDefinition pd = dpd.getProcessDefinition();
		Map<String,Object> tasks = pd.getTaskMgmtDefinition().getTasks();
		if (tasks != null) {
			taskNames.addAll(tasks.keySet());
		}
		// Si la tasca del start-state no té name no surt llistada a pd.getTaskMgmtDefinition().getTasks()
		// Però en realitat sí que té name (el del start-state) i s'ha d'agafar de la següent forma:
		Task startTask = pd.getTaskMgmtDefinition().getStartTask();
		if (startTask != null && !taskNames.contains(startTask.getName())) {
			taskNames.add(startTask.getName());
		}
		return taskNames;
	}

	public void esborrarDesplegament(String jbpmId) {
		DeleteProcessDefinitionCommand command = new DeleteProcessDefinitionCommand();
		command.setId(Long.parseLong(jbpmId));
		commandService.execute(command);
	}

	@SuppressWarnings("unchecked")
	public Set<String> getResourceNames(String jbpmId) {
		Set<String> resources = null;
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		if (processDefinition != null) {
			FileDefinition fd = processDefinition.getFileDefinition();
			if (fd != null)
				resources = fd.getBytesMap().keySet();
			else
				resources = new HashSet<String>();
		} else {
			resources = new HashSet<String>();
		}
		return resources;
	}

	public byte[] getResourceBytes(String jbpmId, String resourceName) {
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		return processDefinition.getFileDefinition().getBytes(resourceName);
	}

	/*public JbpmProcessInstance startProcessInstanceByKey(
			String actorId,
			String processDefinitionKey,
			Map<String, Object> variables,
			String transitionName) {
		JbpmProcessInstance resultat = null;
		StartProcessInstanceCommand command = new StartProcessInstanceCommand();
		command.setProcessDefinitionName(processDefinitionKey);
		command.setActorId(actorId);
		if (variables != null)
			command.setVariables(variables);
		if (transitionName != null)
			command.setStartTransitionName(transitionName);
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		resultat = new JbpmProcessInstance(processInstance);
		return resultat;
	}*/
	public JbpmProcessInstance startProcessInstanceById(
			String actorId,
			String processDefinitionId,
			Map<String, Object> variables) {
		StartProcessInstanceCommand command = new StartProcessInstanceCommand();
		command.setProcessDefinitionId(Long.parseLong(processDefinitionId));
		command.setActorId(actorId);
		if (variables != null)
			command.setVariables(variables);
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		JbpmProcessInstance resultat = new JbpmProcessInstance(processInstance);
		return resultat;
	}
	public void signalProcessInstance(
			String processInstanceId,
			String transitionName) {
		final long id = Long.parseLong(processInstanceId);
		SignalProcessInstanceCommand command = new SignalProcessInstanceCommand(id);
		if (transitionName != null)
			command.setStartTransitionName(transitionName);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
	}
	public JbpmProcessInstance getRootProcessInstance(
			String processInstanceId) {
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(Long.parseLong(processInstanceId));
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		while (processInstance.getSuperProcessToken() != null) {
			final long id = processInstance.getSuperProcessToken().getProcessInstance().getId();
			command.setProcessInstanceId(id);
			processInstance = (ProcessInstance)commandService.execute(command);
		}
		JbpmProcessInstance resultat = new JbpmProcessInstance(processInstance);
		return resultat;
	}
	@SuppressWarnings("unchecked")
	public List<JbpmProcessInstance> getProcessInstanceTree(
			String rootProcessInstanceId) {
		List<JbpmProcessInstance> resposta = new ArrayList<JbpmProcessInstance>();
		final long id = Long.parseLong(rootProcessInstanceId);
		GetProcessInstancesTreeCommand command = new GetProcessInstancesTreeCommand(id);
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resposta.add(new JbpmProcessInstance(pd));
		}
		return resposta;
	}

	public void deleteProcessInstance(
			String processInstanceId) {
		final long id = Long.parseLong(processInstanceId);
		DeleteProcessInstanceCommand command = new DeleteProcessInstanceCommand(id);
		commandService.execute(command);
		/*AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);*/
	}

	public void suspendProcessInstance(
			String processInstanceId) {
		suspendProcessInstances(new String[] {processInstanceId});
	}
	public void suspendProcessInstances(
			String[] processInstanceIds) {
		long[] ids = new long[processInstanceIds.length];
		for (int i = 0; i < processInstanceIds.length; i++)
			ids[i] = Long.parseLong(processInstanceIds[i]);
		
		SuspendProcessInstancesCommand command = new SuspendProcessInstancesCommand(ids);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				ids,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
	}
	public void resumeProcessInstance(
			String processInstanceId) {
		resumeProcessInstances(new String[] {processInstanceId});
	}
	public void resumeProcessInstances(
			String[] processInstanceIds) {
		long[] ids = new long[processInstanceIds.length];
		for (int i = 0; i < processInstanceIds.length; i++)
			ids[i] = Long.parseLong(processInstanceIds[i]);
		ResumeProcessInstancesCommand command = new ResumeProcessInstancesCommand(ids);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				ids,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
	}
	public void describeProcessInstance(
			String processInstanceId,
			String description) {
		final long id = Long.parseLong(processInstanceId);
		DescribeProcessInstanceCommand command = new DescribeProcessInstanceCommand(id, description);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
	}

	@SuppressWarnings("unchecked")
	public List<JbpmProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId) {
		List<JbpmProcessInstance> resultat = new ArrayList<JbpmProcessInstance>();
		final long id = Long.parseLong(processDefinitionId);
		GetProcessInstancesCommand command = new GetProcessInstancesCommand();
		command.setProcessInstanceId(id); // Això està bé, el command agafa setProcessInstanceId com si fos setProcessDefinitionId
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resultat.add(new JbpmProcessInstance(pd));
		}
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmProcessInstance> findProcessInstancesWithProcessDefinitionName(String processName) {
		List<JbpmProcessInstance> resultat = new ArrayList<JbpmProcessInstance>();
		GetProcessInstancesCommand command = new GetProcessInstancesCommand();
		command.setProcessDefinitionName(processName);
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resultat.add(new JbpmProcessInstance(pd));
		}
		return resultat;
	}

	public JbpmProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) {
		JbpmProcessDefinition resultat = null;
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		if (pi != null)
			resultat = new JbpmProcessDefinition(pi.getProcessDefinition());
		return resultat;
	}

	public JbpmTask getTaskById(String taskId) {
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance ti = (TaskInstance)commandService.execute(command);
		if (ti != null)
			resposta = new JbpmTask(ti);
		return resposta;
	}

	public List<Long> findListPersonalTasks(Long entornId, String actorId, 
			String tasca, 
			String expedient, 
			Long tipusExpedient, 
			Date dataCreacioInici, 
			Date dataCreacioFi, 
			Integer prioritat, 
			Date dataLimitInici, 
			Date dataLimitFi) {		
		GetProcessInstancesForActiveTasksCommand command = new GetProcessInstancesForActiveTasksCommand(entornId, actorId, 
				tasca, 
				expedient, 
				tipusExpedient, 
				dataCreacioInici, 
				dataCreacioFi, 
				prioritat, 
				dataLimitInici, 
				dataLimitFi,
				false);
		return (List<Long>)commandService.execute(command);
	}

	public List<Long> findListGroupTasks(Long entornId, String actorId, 
			String tasca, 
			String expedient, 
			Long tipusExpedient, 
			Date dataCreacioInici, 
			Date dataCreacioFi, 
			Integer prioritat, 
			Date dataLimitInici, 
			Date dataLimitFi) {		
		GetProcessInstancesForActiveTasksCommand command = new GetProcessInstancesForActiveTasksCommand(entornId, actorId, 
				tasca, 
				expedient, 
				tipusExpedient, 
				dataCreacioInici, 
				dataCreacioFi, 
				prioritat, 
				dataLimitInici, 
				dataLimitFi,
				true);
		return (List<Long>)commandService.execute(command);
	}

	@SuppressWarnings("unchecked")
	public List<JbpmTask> findPersonalTasks(List<Long> ids, String usuariBo, int firstRow, int maxResults) {
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetPersonalTaskListCommand command = new GetPersonalTaskListCommand(usuariBo, ids);
		command.setFirstRow(firstRow);
		command.setMaxResults(maxResults);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmTask> findGroupTasks(List<Long> ids, String usuariBo, int firstRow, int maxResults) {
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetGroupTaskListCommand command = new GetGroupTaskListCommand(usuariBo, ids);
		command.setFirstRow(firstRow);
		command.setMaxResults(maxResults);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmTask> findPersonalTasks(String usuariBo) {
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetPersonalTaskListCommand command = new GetPersonalTaskListCommand(usuariBo);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<Long> findListIdsPersonalTasks(String actorId,Long entorn) {
		GetProcessInstancesForActiveTasksCommand command = new GetProcessInstancesForActiveTasksCommand(actorId, entorn, false);
		List<Long> resultado = (List<Long>)commandService.execute(command);
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Long> findListIdsGroupTasks(String actorId,Long entorn) {
		GetProcessInstancesForActiveTasksCommand command = new GetProcessInstancesForActiveTasksCommand(actorId, entorn, true);
		List<Long> resultado = (List<Long>)commandService.execute(command);
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<JbpmTask> findGroupTasks(String actorId,Long entorn) {
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		
		GetProcessInstancesForActiveTasksCommand commandIds = new GetProcessInstancesForActiveTasksCommand(actorId, entorn, true);
		List<Long> ids = (List<Long>)commandService.execute(commandIds);
		
		GetGroupTaskListCommand command = new GetGroupTaskListCommand(actorId, ids);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		return resultat;
	}

	public List<String> findStartTaskOutcomes(String jbpmId, String taskName) {
		List<String> resultat = new ArrayList<String>();
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		org.jbpm.taskmgmt.def.Task task = processDefinition.getTaskMgmtDefinition().getTask(taskName);
		if (task != null) {
			for (Transition transition: (List<Transition>)task.getStartState().getLeavingTransitions())
				resultat.add(transition.getName());
		}
		return resultat;
	}
	public List<String> findTaskInstanceOutcomes(String taskInstanceId) {
		List<String> resultat = new ArrayList<String>();
		final long id = Long.parseLong(taskInstanceId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		List<Transition> outcomes = (List<Transition>)taskInstance.getTask().getTaskNode().getLeavingTransitions();
		if (outcomes != null) {
			for (Transition transition: outcomes)
				resultat.add(transition.getName());
		}
		return resultat;
	}
	public void takeTaskInstance(String taskId, String actorId) {
		final long id = Long.parseLong(taskId);
		TakeTaskInstanceCommand command = new TakeTaskInstanceCommand(id, actorId);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
	}
	public void releaseTaskInstance(String taskId) {
		final long id = Long.parseLong(taskId);
		ReleaseTaskInstanceCommand command = new ReleaseTaskInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
	}
	public JbpmTask cloneTaskInstance(String taskId, String actorId, Map<String, Object> variables) {
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		CloneTaskInstanceCommand command = new CloneTaskInstanceCommand(
				id,
				actorId,
				false);
		command.setVariables(variables);
		resposta = new JbpmTask((TaskInstance)commandService.execute(command));
		return resposta;
	}
	public JbpmTask startTaskInstance(String taskId) {
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		StartTaskInstanceCommand command = new StartTaskInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		resposta = new JbpmTask((TaskInstance)commandService.execute(autoSaveCommand));
		return resposta;
	}
	public JbpmTask cancelTaskInstance(String taskId) {
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		CancelTaskInstanceCommand command = new CancelTaskInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		resposta = new JbpmTask((TaskInstance)commandService.execute(autoSaveCommand));
		return resposta;
	}
	public JbpmTask suspendTaskInstance(String taskId) {
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		SuspendTaskInstanceCommand command = new SuspendTaskInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		resposta = new JbpmTask((TaskInstance)commandService.execute(autoSaveCommand));
		return resposta;
	}
	public JbpmTask resumeTaskInstance(String taskId) {
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		ResumeTaskInstanceCommand command = new ResumeTaskInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		resposta = new JbpmTask((TaskInstance)commandService.execute(autoSaveCommand));
		return resposta;
	}
	public JbpmTask reassignTaskInstance(String taskId, String expression) {
		return reassignTaskInstance(taskId, expression, null);
	}
	public JbpmTask reassignTaskInstance(String taskId, String expression, Long entornId) {
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		ReassignTaskInstanceCommand command = new ReassignTaskInstanceCommand(id);
		command.setExpression(expression);
		command.setEntornId(entornId);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		resposta = new JbpmTask((TaskInstance)commandService.execute(autoSaveCommand));
		return resposta;
	}
	public void setTaskInstanceActorId(String taskInstanceId, String actorId) {
		final long id = Long.parseLong(taskInstanceId);
		ReassignTaskInstanceCommand command = new ReassignTaskInstanceCommand(id);
		command.setActorId(actorId);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
	}
	public void setTaskInstancePooledActors(String taskInstanceId, String[] pooledActors) {
		final long id = Long.parseLong(taskInstanceId);
		ReassignTaskInstanceCommand command = new ReassignTaskInstanceCommand(id);
		command.setPooledActors(pooledActors);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
	}
	public void setTaskInstanceVariable(String taskId, String codi, Object valor) {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put(codi, valor);
		setTaskInstanceVariables(taskId, vars, false);
	}
	public void setTaskInstanceVariables(
			String taskId,
			Map<String, Object> variables,
			boolean deleteFirst) {
		final long id = Long.parseLong(taskId);
		SaveTaskInstanceVariablesCommand command = new SaveTaskInstanceVariablesCommand(
				id,
				variables);
		command.setLocally(true);
		command.setDeleteFirst(deleteFirst);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
	}
	public Object getTaskInstanceVariable(String taskId, String varName) {
		Object resultat = null;
		final long id = Long.parseLong(taskId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		resultat = taskInstance.getVariableLocally(varName);
		return resultat;
	}
	@SuppressWarnings("unchecked")
	public Map<String, Object> getTaskInstanceVariables(String taskId) {
		Map<String, Object> resultat = null;
		final long id = Long.parseLong(taskId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		resultat = (Map<String, Object>)taskInstance.getVariablesLocally();
		return resultat;
	}
	public void deleteTaskInstanceVariable(String taskId, String varName) {
		//setTaskInstanceVariable(taskId, varName, null);
		final long id = Long.parseLong(taskId);
		DeleteTaskInstanceVariablesCommand command = new DeleteTaskInstanceVariablesCommand(
				id,
				new String[] {varName});
		command.setLocally(true);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
	}
	/*@SuppressWarnings("unchecked")
	public void deleteTaskInstanceVariablesAll(String taskId) {
		long id = new Long(taskId).longValue();
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		Map<String, Object> vars = taskInstance.getVariablesLocally();
		for (String codi: vars.keySet())
			taskInstance.deleteVariable(codi);
	}*/
	public void endTaskInstance(String taskId, String outcome) {
		final long id = Long.parseLong(taskId);
		TaskInstanceEndCommand command = new TaskInstanceEndCommand(id, outcome);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
	}
	/*public void renameTaskInstance(String taskId, String newName) {
		long id = new Long(taskId).longValue();
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		taskInstance.setName(newName);
	}*/
	public void describeTaskInstance(String taskId, String description) {
		final long id = Long.parseLong(taskId);
		DescribeTaskInstanceCommand command = new DescribeTaskInstanceCommand(id, description);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
	}
	public List<JbpmTask> findTaskInstancesForProcessInstance(String processInstanceId) {
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		for (TaskInstance ti : pi.getTaskMgmtInstance().getTaskInstances())
			resultat.add(new JbpmTask(ti));
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getProcessInstanceVariables(String processInstanceId) {
		Map<String, Object> resultat = null;
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		resultat = pi.getContextInstance().getVariables();
		return resultat;
	}
	public Object getProcessInstanceVariable(String processInstanceId, String varName) {
		Object resultat = null;
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		resultat = pi.getContextInstance().getVariable(varName);
		return resultat;
	}
	public void setProcessInstanceVariable(
			String processInstanceId,
			String varName,
			Object value) {
		final long id = Long.parseLong(processInstanceId);
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put(varName, value);
		SaveProcessInstanceVariablesCommand command = new SaveProcessInstanceVariablesCommand(id, vars);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
	}
	public void deleteProcessInstanceVariable(String processInstanceId, String varName) {
		//setProcessInstanceVariable(processInstanceId, varName, null);
		final long id = Long.parseLong(processInstanceId);
		DeleteProcessInstanceVariablesCommand command = new DeleteProcessInstanceVariablesCommand(id, new String[] {varName});
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
	}

	public JbpmToken getTokenById(String tokenId) {
		final long id = Long.parseLong(tokenId);
		GetTokenByIdCommand command = new GetTokenByIdCommand(id);
		return new JbpmToken((Token)commandService.execute(command));
	}
	public Map<String, JbpmToken> getActiveTokens(String processInstanceId) {
		Map<String, JbpmToken> resposta = new HashMap<String, JbpmToken>();
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(id);
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		Token root = processInstance.getRootToken();
		if (!root.hasEnded())
			resposta.put(root.getName(), new JbpmToken(root));
		Map<String, Token> activeTokens = getActiveTokens(processInstance.getRootToken());
		for (String tokenName: activeTokens.keySet()) {
			resposta.put(tokenName, new JbpmToken(activeTokens.get(tokenName)));
		}
		return resposta;
	}
	@SuppressWarnings("unchecked")
	private  Map<String, Token> getActiveTokens(Token token){
		Map<String, Token> activeTokens = new HashMap<String, Token>();
		if (token.hasActiveChildren()) {
			activeTokens = token.getActiveChildren();
			for (Token t: activeTokens.values()){
				activeTokens.putAll(getActiveTokens(t));
			}
		}
		return activeTokens;
	}
	public Map<String, JbpmToken> getAllTokens(String processInstanceId) {
		Map<String, JbpmToken> resposta = new HashMap<String, JbpmToken>();
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(id);
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		Token root = processInstance.getRootToken();
		if (!root.hasEnded())
			resposta.put(root.getName(), new JbpmToken(root));
		Map<String, Token> activeTokens = processInstance.getRootToken().getChildren();
		for (String tokenName: activeTokens.keySet()) {
			resposta.put(tokenName, new JbpmToken(activeTokens.get(tokenName)));
		}
		return resposta;
	}

	@SuppressWarnings("unchecked")
	public List<String> findArrivingNodeNames(String tokenId) {
		final long id = Long.parseLong(tokenId);
		FindArrivingNodeNamesCommand command = new FindArrivingNodeNamesCommand(id);
		return (List<String>)commandService.execute(command);
	}

	public void tokenRedirect(
			long tokenId,
			String nodeName,
			boolean cancelTasks,
			boolean enterNodeIfTask,
			boolean executeNode) {
		TokenRedirectCommand command = new TokenRedirectCommand(tokenId, nodeName);
		command.setCancelTasks(cancelTasks);
		command.setEnterNodeIfTask(enterNodeIfTask);
		command.setExecuteNode(executeNode);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				tokenId,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		commandService.execute(autoSaveCommand);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> evaluateScript(
			String processInstanceId,
			String script,
			Set<String> outputNames) {
		Map<String,Object> resultat = null;
		final long id = Long.parseLong(processInstanceId);
		EvaluateScriptCommand command = new EvaluateScriptCommand(
				id,
				script,
				outputNames);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		resultat = (Map<String,Object>)commandService.execute(autoSaveCommand);
		return resultat;
	}

	public Object evaluateExpression(
			String taskInstanceInstanceId,
			String processInstanceId,
			String expression,
			Map<String, Object> valors) {
		final long id = Long.parseLong(processInstanceId);
		EvaluateExpressionCommand command = new EvaluateExpressionCommand(
				id,
				expression);
		if (taskInstanceInstanceId != null)
			command.setTid(Long.parseLong(taskInstanceInstanceId));
		if (valors != null)
			command.setValors(valors);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		return commandService.execute(autoSaveCommand);
	}

	@SuppressWarnings("unchecked")
	public List<String> listActions(String jbpmId) {
		final long id = Long.parseLong(jbpmId);
		return (List<String>)commandService.execute(
				new ListActionsCommand(id));
	}
	public void executeActionInstanciaProces(
			String processInstanceId,
			String actionName) {
		final long id = Long.parseLong(processInstanceId);
		ExecuteActionCommand command = new ExecuteActionCommand(
				id,
				actionName);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
	}
	public void executeActionInstanciaTasca(
			String taskInstanceId,
			String actionName) {
		final long id = Long.parseLong(taskInstanceId);
		ExecuteActionCommand command = new ExecuteActionCommand(
				id,
				actionName);
		command.setTaskInstance(true);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
	}
	public void retrocedirAccio(
			String processInstanceId,
			String actionName,
			List<String> params) {
		final long id = Long.parseLong(processInstanceId);
		ExecuteActionCommand command = new ExecuteActionCommand(
				id,
				actionName);
		command.setGoBack(true);
		command.setParams(params);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
	}

	public void changeProcessInstanceVersion(
			String processInstanceId,
			int newVersion) {
		final long id = Long.parseLong(processInstanceId);
		ChangeProcessInstanceVersionCommand command = new ChangeProcessInstanceVersionCommand(
				id,
				newVersion);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
	}

	public void signalToken(
			long tokenId,
			String transitionName) {
		SignalCommand command = new SignalCommand(tokenId, transitionName);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				tokenId,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		commandService.execute(autoSaveCommand);
	}

	@SuppressWarnings("unchecked")
	public List<Timer> findTimersWithProcessInstanceId(
			String processInstanceId) {
		final long id = Long.parseLong(processInstanceId);
		FindProcessInstanceTimersCommand command = new FindProcessInstanceTimersCommand(id);
		return (List<Timer>)commandService.execute(command);
	}

	public void suspendTimer(
			long timerId,
			Date dueDate) {
		SuspendProcessInstanceTimerCommand command = new SuspendProcessInstanceTimerCommand(timerId);
		command.setDueDate(dueDate);
		commandService.execute(command);
	}
	public void resumeTimer(
			long timerId,
			Date dueDate) {
		ResumeProcessInstanceTimerCommand command = new ResumeProcessInstanceTimerCommand(timerId);
		command.setDueDate(dueDate);
		commandService.execute(command);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Token, List<ProcessLog>> getProcessInstanceLogs(String processInstanceId) {
		final long id = Long.parseLong(processInstanceId);
		FindProcessInstanceLogsCommand command = new FindProcessInstanceLogsCommand(id);
		return (Map)commandService.execute(command);
	}

	public long addProcessInstanceMessageLog(String processInstanceId, String message) {
		final long id = Long.parseLong(processInstanceId);
		AddProcessInstanceMessageLogCommand command = new AddProcessInstanceMessageLogCommand(id, message);
		return ((Long)commandService.execute(command)).longValue();
	}
	public long addTaskInstanceMessageLog(String taskInstanceId, String message) {
		final long id = Long.parseLong(taskInstanceId);
		AddTaskInstanceMessageLogCommand command = new AddTaskInstanceMessageLogCommand(id, message);
		return ((Long)commandService.execute(command)).longValue();
	}

	public Long getVariableIdFromVariableLog(long variableLogId) {
		GetVariableIdFromVariableLogCommand command = new GetVariableIdFromVariableLogCommand(variableLogId);
		return (Long)commandService.execute(command);
	}
	public Long getTaskIdFromVariableLog(long variableLogId) {
		GetTaskIdFromVariableLogCommand command = new GetTaskIdFromVariableLogCommand(variableLogId);
		return (Long)commandService.execute(command);
	}

	public void cancelProcessInstance(long id) {
		CancelProcessInstanceCommand command = new CancelProcessInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
	}
	public void revertProcessInstanceEnd(long id) {
		RevertProcessInstanceEndCommand command = new RevertProcessInstanceEndCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
	}

	public void cancelToken(long id) {
		CancelTokenCommand command = new CancelTokenCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		commandService.execute(autoSaveCommand);
	}
	public void revertTokenEnd(long id) {
		JbpmToken jtoken = getTokenById(String.valueOf(id));
		RevertTokenEndCommand command = new RevertTokenEndCommand(jtoken.getToken().getId());
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		commandService.execute(autoSaveCommand);
		this.sessionFactory.getCurrentSession().refresh(jtoken.getToken());
		jtoken.getToken().setAbleToReactivateParent(true);
	}

	public JbpmTask findEquivalentTaskInstance(long tokenId, long taskInstanceId) {
		GetTaskInstanceCommand commandGetTask = new GetTaskInstanceCommand(taskInstanceId);
		TaskInstance ti = (TaskInstance)commandService.execute(commandGetTask);
		FindTaskInstanceForTokenAndTaskCommand command = new FindTaskInstanceForTokenAndTaskCommand(tokenId, ti.getTask().getName());
		return new JbpmTask((TaskInstance)commandService.execute(command));
	}

	public boolean isProcessStateNodeJoinOrFork(long processInstanceId, String nodeName) {
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(processInstanceId);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		Node node = pi.getProcessDefinition().getNode(nodeName);
		String nodeClassName = node.toString();
		NodeType nodeType = node.getNodeType();
		return (nodeClassName.startsWith("ProcessState") || nodeType == NodeType.Fork || nodeType == NodeType.Join);
	}

	public boolean isJoinNode(long processInstanceId, String nodeName) {
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(processInstanceId);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		NodeType nodeType = pi.getProcessDefinition().getNode(nodeName).getNodeType();
		return nodeType == NodeType.Join;
	}
	
	public ProcessLog getProcessLogById(Long id){
		GetProcessLogByIdCommand command = new GetProcessLogByIdCommand(id.longValue());
		return (ProcessLog)commandService.execute(command);
	}

	public Node getNodeByName(long processInstanceId, String nodeName) {
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(processInstanceId);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		return pi.getProcessDefinition().getNode(nodeName);
	}
	
	public boolean hasStartBetweenLogs(long begin, long end, long taskInstanceId) {
		HasStartBetweenLogsCommand command = new HasStartBetweenLogsCommand(begin, end, taskInstanceId);
		Boolean hasStart = (Boolean)commandService.execute(command);
		return hasStart.booleanValue();
	}
	

	@Autowired
	public void setCommandService(CommandService commandService) {
		this.commandService = commandService;
	}
	
	@Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
	
	private SessionFactory sessionFactory;
}
