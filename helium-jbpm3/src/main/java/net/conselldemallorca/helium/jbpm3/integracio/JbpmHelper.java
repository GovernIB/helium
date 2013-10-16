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

import javax.annotation.Resource;

import net.conselldemallorca.helium.jbpm3.command.AddProcessInstanceMessageLogCommand;
import net.conselldemallorca.helium.jbpm3.command.AddTaskInstanceMessageLogCommand;
import net.conselldemallorca.helium.jbpm3.command.AddToAutoSaveCommand;
import net.conselldemallorca.helium.jbpm3.command.CancelProcessInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.CancelTaskInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.CloneTaskInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.DeleteProcessInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.DeleteProcessInstanceVariablesCommand;
import net.conselldemallorca.helium.jbpm3.command.DeleteTaskInstanceVariablesCommand;
import net.conselldemallorca.helium.jbpm3.command.DescribeProcessInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.DescribeTaskInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.EvaluateExpressionCommand;
import net.conselldemallorca.helium.jbpm3.command.EvaluateScriptCommand;
import net.conselldemallorca.helium.jbpm3.command.ExecuteActionCommand;
import net.conselldemallorca.helium.jbpm3.command.FindArrivingNodeNamesCommand;
import net.conselldemallorca.helium.jbpm3.command.FindProcessInstanceLogsCommand;
import net.conselldemallorca.helium.jbpm3.command.FindProcessInstanceTimersCommand;
import net.conselldemallorca.helium.jbpm3.command.FindTaskInstanceForTokenAndTaskCommand;
import net.conselldemallorca.helium.jbpm3.command.GetGroupTaskListCommand;
import net.conselldemallorca.helium.jbpm3.command.GetPersonalTaskListCommand;
import net.conselldemallorca.helium.jbpm3.command.GetProcessDefinitionByIdCommand;
import net.conselldemallorca.helium.jbpm3.command.GetProcessInstancesTreeCommand;
import net.conselldemallorca.helium.jbpm3.command.GetProcessLogByIdCommand;
import net.conselldemallorca.helium.jbpm3.command.GetRootProcessInstancesForActiveTasksCommand;
import net.conselldemallorca.helium.jbpm3.command.GetRootProcessInstancesForExpedientsWithActiveTasksCommand;
import net.conselldemallorca.helium.jbpm3.command.GetSubProcessDefinitionsCommand;
import net.conselldemallorca.helium.jbpm3.command.GetTaskIdFromVariableLogCommand;
import net.conselldemallorca.helium.jbpm3.command.GetTokenByIdCommand;
import net.conselldemallorca.helium.jbpm3.command.GetVariableIdFromVariableLogCommand;
import net.conselldemallorca.helium.jbpm3.command.HasStartBetweenLogsCommand;
import net.conselldemallorca.helium.jbpm3.command.ListActionsCommand;
import net.conselldemallorca.helium.jbpm3.command.ReassignTaskInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.ReleaseTaskInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.ResumeProcessInstanceTimerCommand;
import net.conselldemallorca.helium.jbpm3.command.ResumeProcessInstancesCommand;
import net.conselldemallorca.helium.jbpm3.command.ResumeTaskInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.RevertProcessInstanceEndCommand;
import net.conselldemallorca.helium.jbpm3.command.RevertTokenEndCommand;
import net.conselldemallorca.helium.jbpm3.command.SaveProcessInstanceVariablesCommand;
import net.conselldemallorca.helium.jbpm3.command.SaveTaskInstanceVariablesCommand;
import net.conselldemallorca.helium.jbpm3.command.SignalProcessInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.StartProcessInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.StartTaskInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.SuspendProcessInstanceTimerCommand;
import net.conselldemallorca.helium.jbpm3.command.SuspendProcessInstancesCommand;
import net.conselldemallorca.helium.jbpm3.command.SuspendTaskInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.TakeTaskInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.TokenRedirectCommand;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;

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
public class JbpmHelper {

	private CommandService commandService;
	
	@Resource
	private AdminService adminService;

	public JbpmProcessDefinition desplegar(
			String nomArxiu,
			byte[] contingut) {
		adminService.mesuraIniciar("jBPM desplegar", "jbpmDao");
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
				adminService.mesuraCalcular("jBPM desplegar", "jbpmDao");
				throw new RuntimeException("No s'ha pogut deplegar l'arxiu", ex);
			}
		} else if (nomArxiu.endsWith("ar")) {
			command = new DeployProcessCommand(contingut);
		} else {
			adminService.mesuraCalcular("jBPM desplegar", "jbpmDao");
			throw new RuntimeException("Arxiu amb extensió no suportada " + nomArxiu + ". Només es suporten les extensions .xml i .*ar");
		}
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		resposta = new JbpmProcessDefinition(processDefinition);
		adminService.mesuraCalcular("jBPM desplegar", "jbpmDao");
		return resposta;
	}

	public JbpmProcessDefinition getProcessDefinition(String jbpmId) {
		adminService.mesuraIniciar("jBPM getProcessDefinition", "jbpmDao");
		JbpmProcessDefinition resposta = null;
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		if (processDefinition != null)
			resposta = new JbpmProcessDefinition((ProcessDefinition)commandService.execute(command));
		adminService.mesuraCalcular("jBPM getProcessDefinition", "jbpmDao");
		return resposta;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmProcessDefinition> getSubProcessDefinitions(String jbpmId) {
		adminService.mesuraIniciar("jBPM getSubProcessDefinitions", "jbpmDao");
		List<JbpmProcessDefinition> resposta = new ArrayList<JbpmProcessDefinition>();
		final long pdid = Long.parseLong(jbpmId);
		GetSubProcessDefinitionsCommand command = new GetSubProcessDefinitionsCommand(pdid);
		for (ProcessDefinition pd: (List<ProcessDefinition>)commandService.execute(command)) {
			resposta.add(new JbpmProcessDefinition(pd));
		}
		adminService.mesuraCalcular("jBPM getSubProcessDefinitions", "jbpmDao");
		return resposta;
	}

	public JbpmProcessInstance getProcessInstance(String processInstanceId) {
		adminService.mesuraIniciar("jBPM getProcessInstance", "jbpmDao");
		JbpmProcessInstance resposta = null;
		final long piid = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(piid);
		resposta = new JbpmProcessInstance((ProcessInstance)commandService.execute(command));
		adminService.mesuraCalcular("jBPM getProcessInstance", "jbpmDao");
		return resposta;
	}

	public String getStartTaskName(String jbpmId) {
		adminService.mesuraIniciar("jBPM getStartTaskName", "jbpmDao");
		String resposta = null;
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		if (processDefinition != null) {
			org.jbpm.taskmgmt.def.Task startTask = processDefinition.getTaskMgmtDefinition().getStartTask();
			if (startTask != null)
				resposta = startTask.getName();
		}
		adminService.mesuraCalcular("jBPM getStartTaskName", "jbpmDao");
		return resposta;
	}

	@SuppressWarnings("unchecked")
	public List<String> getTaskNamesFromDeployedProcessDefinition(JbpmProcessDefinition dpd) {
		adminService.mesuraIniciar("jBPM getTaskNamesFromDeployedProcessDefinition", "jbpmDao");
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
		adminService.mesuraCalcular("jBPM getTaskNamesFromDeployedProcessDefinition", "jbpmDao");
		return taskNames;
	}

	public void esborrarDesplegament(String jbpmId) {
		adminService.mesuraIniciar("jBPM esborrarDesplegament", "jbpmDao");
		DeleteProcessDefinitionCommand command = new DeleteProcessDefinitionCommand();
		command.setId(Long.parseLong(jbpmId));
		commandService.execute(command);
		adminService.mesuraCalcular("jBPM esborrarDesplegament", "jbpmDao");
	}

	@SuppressWarnings("unchecked")
	public Set<String> getResourceNames(String jbpmId) {
		adminService.mesuraIniciar("jBPM getResourceNames", "jbpmDao");
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
		adminService.mesuraCalcular("jBPM getResourceNames", "jbpmDao");
		return resources;
	}

	public byte[] getResourceBytes(String jbpmId, String resourceName) {
		adminService.mesuraIniciar("jBPM getResourceBytes", "jbpmDao");
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		byte[] bytes = processDefinition.getFileDefinition().getBytes(resourceName);
		adminService.mesuraCalcular("jBPM getResourceBytes", "jbpmDao");
		return bytes;
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
		adminService.mesuraIniciar("jBPM startProcessInstanceById", "jbpmDao");
		StartProcessInstanceCommand command = new StartProcessInstanceCommand();
		command.setProcessDefinitionId(Long.parseLong(processDefinitionId));
		command.setActorId(actorId);
		if (variables != null)
			command.setVariables(variables);
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		JbpmProcessInstance resultat = new JbpmProcessInstance(processInstance);
		adminService.mesuraCalcular("jBPM startProcessInstanceById", "jbpmDao");
		return resultat;
	}
	public void signalProcessInstance(
			String processInstanceId,
			String transitionName) {
		adminService.mesuraIniciar("jBPM signalProcessInstance", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		SignalProcessInstanceCommand command = new SignalProcessInstanceCommand(id);
		if (transitionName != null)
			command.setStartTransitionName(transitionName);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM signalProcessInstance", "jbpmDao");
	}
	public JbpmProcessInstance getRootProcessInstance(
			String processInstanceId) {
		adminService.mesuraIniciar("jBPM getRootProcessInstance", "jbpmDao");
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(Long.parseLong(processInstanceId));
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		while (processInstance.getSuperProcessToken() != null) {
			final long id = processInstance.getSuperProcessToken().getProcessInstance().getId();
			command.setProcessInstanceId(id);
			processInstance = (ProcessInstance)commandService.execute(command);
		}
		JbpmProcessInstance resultat = new JbpmProcessInstance(processInstance);
		adminService.mesuraCalcular("jBPM getRootProcessInstance", "jbpmDao");
		return resultat;
	}
	@SuppressWarnings("unchecked")
	public List<JbpmProcessInstance> getProcessInstanceTree(
			String rootProcessInstanceId) {
		adminService.mesuraIniciar("jBPM getProcessInstanceTree", "jbpmDao");
		List<JbpmProcessInstance> resposta = new ArrayList<JbpmProcessInstance>();
		final long id = Long.parseLong(rootProcessInstanceId);
		GetProcessInstancesTreeCommand command = new GetProcessInstancesTreeCommand(id);
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resposta.add(new JbpmProcessInstance(pd));
		}
		adminService.mesuraCalcular("jBPM getProcessInstanceTree", "jbpmDao");
		return resposta;
	}

	public void deleteProcessInstance(
			String processInstanceId) {
		adminService.mesuraIniciar("jBPM deleteProcessInstance", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		DeleteProcessInstanceCommand command = new DeleteProcessInstanceCommand(id);
		commandService.execute(command);
		adminService.mesuraCalcular("jBPM deleteProcessInstance", "jbpmDao");
		/*AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);*/
	}

	public void suspendProcessInstance(
			String processInstanceId) {
		adminService.mesuraIniciar("jBPM suspendProcessInstance", "jbpmDao");
		suspendProcessInstances(new String[] {processInstanceId});
		adminService.mesuraCalcular("jBPM suspendProcessInstance", "jbpmDao");
	}
	public void suspendProcessInstances(
			String[] processInstanceIds) {
		adminService.mesuraIniciar("jBPM suspendProcessInstances", "jbpmDao");
		long[] ids = new long[processInstanceIds.length];
		for (int i = 0; i < processInstanceIds.length; i++)
			ids[i] = Long.parseLong(processInstanceIds[i]);
		
		SuspendProcessInstancesCommand command = new SuspendProcessInstancesCommand(ids);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				ids,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM suspendProcessInstances", "jbpmDao");
	}
	public void resumeProcessInstance(
			String processInstanceId) {
		adminService.mesuraIniciar("jBPM resumeProcessInstance", "jbpmDao");
		resumeProcessInstances(new String[] {processInstanceId});
		adminService.mesuraCalcular("jBPM resumeProcessInstance", "jbpmDao");
	}
	public void resumeProcessInstances(
			String[] processInstanceIds) {
		adminService.mesuraIniciar("jBPM resumeProcessInstances", "jbpmDao");
		long[] ids = new long[processInstanceIds.length];
		for (int i = 0; i < processInstanceIds.length; i++)
			ids[i] = Long.parseLong(processInstanceIds[i]);
		ResumeProcessInstancesCommand command = new ResumeProcessInstancesCommand(ids);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				ids,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM resumeProcessInstances", "jbpmDao");
	}
	public void describeProcessInstance(
			String processInstanceId,
			String description) {
		adminService.mesuraIniciar("jBPM describeProcessInstance", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		DescribeProcessInstanceCommand command = new DescribeProcessInstanceCommand(id, description);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM describeProcessInstance", "jbpmDao");
	}

	@SuppressWarnings("unchecked")
	public List<JbpmProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId) {
		adminService.mesuraIniciar("jBPM findProcessInstancesWithProcessDefinitionId", "jbpmDao");
		List<JbpmProcessInstance> resultat = new ArrayList<JbpmProcessInstance>();
		final long id = Long.parseLong(processDefinitionId);
		GetProcessInstancesCommand command = new GetProcessInstancesCommand();
		command.setProcessInstanceId(id); // Això està bé, el command agafa setProcessInstanceId com si fos setProcessDefinitionId
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resultat.add(new JbpmProcessInstance(pd));
		}
		adminService.mesuraCalcular("jBPM findProcessInstancesWithProcessDefinitionId", "jbpmDao");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmProcessInstance> findProcessInstancesWithProcessDefinitionName(String processName) {
		adminService.mesuraIniciar("jBPM findProcessInstancesWithProcessDefinitionName", "jbpmDao");
		List<JbpmProcessInstance> resultat = new ArrayList<JbpmProcessInstance>();
		GetProcessInstancesCommand command = new GetProcessInstancesCommand();
		command.setProcessDefinitionName(processName);
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resultat.add(new JbpmProcessInstance(pd));
		}
		adminService.mesuraCalcular("jBPM findProcessInstancesWithProcessDefinitionName", "jbpmDao");
		return resultat;
	}

	public JbpmProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) {
		adminService.mesuraIniciar("jBPM findProcessDefinitionWithProcessInstanceId", "jbpmDao");
		JbpmProcessDefinition resultat = null;
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		if (pi != null)
			resultat = new JbpmProcessDefinition(pi.getProcessDefinition());
		adminService.mesuraCalcular("jBPM findProcessDefinitionWithProcessInstanceId", "jbpmDao");
		return resultat;
	}

	public JbpmTask getTaskById(String taskId) {
		adminService.mesuraIniciar("jBPM getTaskById", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance ti = (TaskInstance)commandService.execute(command);
		if (ti != null)
			resposta = new JbpmTask(ti);
		adminService.mesuraCalcular("jBPM getTaskById", "jbpmDao");
		return resposta;
	}

	public List<String> findStartTaskOutcomes(String jbpmId, String taskName) {
		adminService.mesuraIniciar("jBPM findStartTaskOutcomes", "jbpmDao");
		List<String> resultat = new ArrayList<String>();
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		org.jbpm.taskmgmt.def.Task task = processDefinition.getTaskMgmtDefinition().getTask(taskName);
		if (task != null) {
			for (Transition transition: (List<Transition>)task.getStartState().getLeavingTransitions())
				resultat.add(transition.getName());
		}
		adminService.mesuraCalcular("jBPM findStartTaskOutcomes", "jbpmDao");
		return resultat;
	}
	public List<String> findTaskInstanceOutcomes(String taskInstanceId) {
		adminService.mesuraIniciar("jBPM findTaskInstanceOutcomes", "jbpmDao");
		List<String> resultat = new ArrayList<String>();
		final long id = Long.parseLong(taskInstanceId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		List<Transition> outcomes = (List<Transition>)taskInstance.getTask().getTaskNode().getLeavingTransitions();
		if (outcomes != null) {
			for (Transition transition: outcomes)
				resultat.add(transition.getName());
		}
		adminService.mesuraCalcular("jBPM findTaskInstanceOutcomes", "jbpmDao");
		return resultat;
	}
	public void takeTaskInstance(String taskId, String actorId) {
		adminService.mesuraIniciar("jBPM takeTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		TakeTaskInstanceCommand command = new TakeTaskInstanceCommand(id, actorId);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM takeTaskInstance", "jbpmDao");
	}
	public void releaseTaskInstance(String taskId) {
		adminService.mesuraIniciar("jBPM releaseTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		ReleaseTaskInstanceCommand command = new ReleaseTaskInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM releaseTaskInstance", "jbpmDao");
	}
	public JbpmTask cloneTaskInstance(String taskId, String actorId, Map<String, Object> variables) {
		adminService.mesuraIniciar("jBPM cloneTaskInstance", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		CloneTaskInstanceCommand command = new CloneTaskInstanceCommand(
				id,
				actorId,
				false);
		command.setVariables(variables);
		resposta = new JbpmTask((TaskInstance)commandService.execute(command));
		adminService.mesuraCalcular("jBPM cloneTaskInstance", "jbpmDao");
		return resposta;
	}
	public JbpmTask startTaskInstance(String taskId) {
		adminService.mesuraIniciar("jBPM startTaskInstance", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		StartTaskInstanceCommand command = new StartTaskInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		resposta = new JbpmTask((TaskInstance)commandService.execute(autoSaveCommand));
		adminService.mesuraCalcular("jBPM startTaskInstance", "jbpmDao");
		return resposta;
	}
	public JbpmTask cancelTaskInstance(String taskId) {
		adminService.mesuraIniciar("jBPM cancelTaskInstance", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		CancelTaskInstanceCommand command = new CancelTaskInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		resposta = new JbpmTask((TaskInstance)commandService.execute(autoSaveCommand));
		adminService.mesuraCalcular("jBPM cancelTaskInstance", "jbpmDao");
		return resposta;
	}
	public JbpmTask suspendTaskInstance(String taskId) {
		adminService.mesuraIniciar("jBPM suspendTaskInstance", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		SuspendTaskInstanceCommand command = new SuspendTaskInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		resposta = new JbpmTask((TaskInstance)commandService.execute(autoSaveCommand));
		adminService.mesuraCalcular("jBPM suspendTaskInstance", "jbpmDao");
		return resposta;
	}
	public JbpmTask resumeTaskInstance(String taskId) {
		adminService.mesuraIniciar("jBPM resumeTaskInstance", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		ResumeTaskInstanceCommand command = new ResumeTaskInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		resposta = new JbpmTask((TaskInstance)commandService.execute(autoSaveCommand));
		adminService.mesuraCalcular("jBPM resumeTaskInstance", "jbpmDao");
		return resposta;
	}
	public JbpmTask reassignTaskInstance(String taskId, String expression) {
		adminService.mesuraIniciar("jBPM reassignTaskInstance", "jbpmDao");
		JbpmTask resposta = reassignTaskInstance(taskId, expression, null);
		adminService.mesuraCalcular("jBPM reassignTaskInstance", "jbpmDao");
		return resposta;
	}
	public JbpmTask reassignTaskInstance(String taskId, String expression, Long entornId) {
		adminService.mesuraIniciar("jBPM reassignTaskInstance entorn", "jbpmDao");
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
		adminService.mesuraCalcular("jBPM reassignTaskInstance entorn", "jbpmDao");
		return resposta;
	}
	public void setTaskInstanceActorId(String taskInstanceId, String actorId) {
		adminService.mesuraIniciar("jBPM setTaskInstanceActorId", "jbpmDao");
		final long id = Long.parseLong(taskInstanceId);
		ReassignTaskInstanceCommand command = new ReassignTaskInstanceCommand(id);
		command.setActorId(actorId);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM setTaskInstanceActorId", "jbpmDao");
	}
	public void setTaskInstancePooledActors(String taskInstanceId, String[] pooledActors) {
		adminService.mesuraIniciar("jBPM setTaskInstancePooledActors", "jbpmDao");
		final long id = Long.parseLong(taskInstanceId);
		ReassignTaskInstanceCommand command = new ReassignTaskInstanceCommand(id);
		command.setPooledActors(pooledActors);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM setTaskInstancePooledActors", "jbpmDao");
	}
	public void setTaskInstanceVariable(String taskId, String codi, Object valor) {
		adminService.mesuraIniciar("jBPM setTaskInstanceVariable", "jbpmDao");
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put(codi, valor);
		setTaskInstanceVariables(taskId, vars, false);
		adminService.mesuraCalcular("jBPM setTaskInstanceVariable", "jbpmDao");
	}
	public void setTaskInstanceVariables(
			String taskId,
			Map<String, Object> variables,
			boolean deleteFirst) {
		adminService.mesuraIniciar("jBPM setTaskInstanceVariables", "jbpmDao");
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
		adminService.mesuraCalcular("jBPM setTaskInstanceVariables", "jbpmDao");
	}
	public Object getTaskInstanceVariable(String taskId, String varName) {
		adminService.mesuraIniciar("jBPM getTaskInstanceVariable", "jbpmDao");
		Object resultat = null;
		final long id = Long.parseLong(taskId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		resultat = taskInstance.getVariableLocally(varName);
		adminService.mesuraCalcular("jBPM getTaskInstanceVariable", "jbpmDao");
		return resultat;
	}
	@SuppressWarnings("unchecked")
	public Map<String, Object> getTaskInstanceVariables(String taskId) {
		adminService.mesuraIniciar("jBPM getTaskInstanceVariables", "jbpmDao");
		Map<String, Object> resultat = null;
		final long id = Long.parseLong(taskId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		resultat = (Map<String, Object>)taskInstance.getVariablesLocally();
		adminService.mesuraCalcular("jBPM getTaskInstanceVariables", "jbpmDao");
		return resultat;
	}
	public void deleteTaskInstanceVariable(String taskId, String varName) {
		adminService.mesuraIniciar("jBPM deleteTaskInstanceVariable", "jbpmDao");
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
		adminService.mesuraCalcular("jBPM deleteTaskInstanceVariable", "jbpmDao");
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
		adminService.mesuraIniciar("jBPM endTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		TaskInstanceEndCommand command = new TaskInstanceEndCommand(id, outcome);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM endTaskInstance", "jbpmDao");
	}
	/*public void renameTaskInstance(String taskId, String newName) {
		long id = new Long(taskId).longValue();
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		taskInstance.setName(newName);
	}*/
	public void describeTaskInstance(String taskId, String description) {
		adminService.mesuraIniciar("jBPM describeTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		DescribeTaskInstanceCommand command = new DescribeTaskInstanceCommand(id, description);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM describeTaskInstance", "jbpmDao");
	}
	public List<JbpmTask> findTaskInstancesForProcessInstance(String processInstanceId) {
		adminService.mesuraIniciar("jBPM findTaskInstancesForProcessInstance", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		for (TaskInstance ti : pi.getTaskMgmtInstance().getTaskInstances())
			resultat.add(new JbpmTask(ti));
		adminService.mesuraCalcular("jBPM findTaskInstancesForProcessInstance", "jbpmDao");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getProcessInstanceVariables(String processInstanceId) {
		adminService.mesuraIniciar("jBPM getProcessInstanceVariables", "jbpmDao");
		Map<String, Object> resultat = null;
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		adminService.mesuraCalcular("jBPM getProcessInstanceVariables", "jbpmDao");
		resultat = pi.getContextInstance().getVariables();
		return resultat;
	}
	public Object getProcessInstanceVariable(String processInstanceId, String varName) {
		adminService.mesuraIniciar("jBPM getProcessInstanceVariable", "jbpmDao");
		Object resultat = null;
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		resultat = pi.getContextInstance().getVariable(varName);
		adminService.mesuraCalcular("jBPM getProcessInstanceVariable", "jbpmDao");
		return resultat;
	}
	public void setProcessInstanceVariable(
			String processInstanceId,
			String varName,
			Object value) {
		adminService.mesuraIniciar("jBPM setProcessInstanceVariable", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put(varName, value);
		SaveProcessInstanceVariablesCommand command = new SaveProcessInstanceVariablesCommand(id, vars);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM setProcessInstanceVariable", "jbpmDao");
	}
	public void deleteProcessInstanceVariable(String processInstanceId, String varName) {
		adminService.mesuraIniciar("jBPM deleteProcessInstanceVariable", "jbpmDao");
		//setProcessInstanceVariable(processInstanceId, varName, null);
		final long id = Long.parseLong(processInstanceId);
		DeleteProcessInstanceVariablesCommand command = new DeleteProcessInstanceVariablesCommand(id, new String[] {varName});
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM deleteProcessInstanceVariable", "jbpmDao");
	}

	public JbpmToken getTokenById(String tokenId) {
		adminService.mesuraIniciar("jBPM getTokenById", "jbpmDao");
		final long id = Long.parseLong(tokenId);
		GetTokenByIdCommand command = new GetTokenByIdCommand(id);
		JbpmToken resultat = new JbpmToken((Token)commandService.execute(command));
		adminService.mesuraCalcular("jBPM getTokenById", "jbpmDao");
		return resultat;
	}
	public Map<String, JbpmToken> getActiveTokens(String processInstanceId) {
		adminService.mesuraIniciar("jBPM getActiveTokens", "jbpmDao");
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
		adminService.mesuraCalcular("jBPM getActiveTokens", "jbpmDao");
		return resposta;
	}
	@SuppressWarnings("unchecked")
	private  Map<String, Token> getActiveTokens(Token token){
		adminService.mesuraIniciar("jBPM getActiveTokens", "jbpmDao");
		Map<String, Token> activeTokens = new HashMap<String, Token>();
		if (token.hasActiveChildren()) {
			activeTokens = token.getActiveChildren();
			for (Token t: activeTokens.values()){
				activeTokens.putAll(getActiveTokens(t));
			}
		}
		adminService.mesuraCalcular("jBPM getActiveTokens", "jbpmDao");
		return activeTokens;
	}
	public Map<String, JbpmToken> getAllTokens(String processInstanceId) {
		adminService.mesuraIniciar("jBPM getAllTokens", "jbpmDao");
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
		adminService.mesuraCalcular("jBPM getAllTokens", "jbpmDao");
		return resposta;
	}

	@SuppressWarnings("unchecked")
	public List<String> findArrivingNodeNames(String tokenId) {
		adminService.mesuraIniciar("jBPM findArrivingNodeNames", "jbpmDao");
		final long id = Long.parseLong(tokenId);
		FindArrivingNodeNamesCommand command = new FindArrivingNodeNamesCommand(id);
		List<String> resultat = (List<String>)commandService.execute(command);
		adminService.mesuraCalcular("jBPM findArrivingNodeNames", "jbpmDao");
		return resultat;
	}

	public void tokenRedirect(
			long tokenId,
			String nodeName,
			boolean cancelTasks,
			boolean enterNodeIfTask,
			boolean executeNode) {
		adminService.mesuraIniciar("jBPM tokenRedirect", "jbpmDao");
		TokenRedirectCommand command = new TokenRedirectCommand(tokenId, nodeName);
		command.setCancelTasks(cancelTasks);
		command.setEnterNodeIfTask(enterNodeIfTask);
		command.setExecuteNode(executeNode);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				tokenId,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM tokenRedirect", "jbpmDao");
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> evaluateScript(
			String processInstanceId,
			String script,
			Set<String> outputNames) {
		adminService.mesuraIniciar("jBPM evaluateScript", "jbpmDao");
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
		adminService.mesuraCalcular("jBPM evaluateScript", "jbpmDao");
		return resultat;
	}

	public Object evaluateExpression(
			String taskInstanceInstanceId,
			String processInstanceId,
			String expression,
			Map<String, Object> valors) {
		adminService.mesuraIniciar("jBPM evaluateExpression", "jbpmDao");
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
		Object resultat = commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM evaluateExpression", "jbpmDao");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<String> listActions(String jbpmId) {
		adminService.mesuraIniciar("jBPM listActions", "jbpmDao");
		final long id = Long.parseLong(jbpmId);
		List<String> llista = (List<String>)commandService.execute(
				new ListActionsCommand(id));
		adminService.mesuraCalcular("jBPM listActions", "jbpmDao");
		return llista;
	}
	public void executeActionInstanciaProces(
			String processInstanceId,
			String actionName) {
		adminService.mesuraIniciar("jBPM executeActionInstanciaProces", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		ExecuteActionCommand command = new ExecuteActionCommand(
				id,
				actionName);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM executeActionInstanciaProces", "jbpmDao");
	}
	public void executeActionInstanciaTasca(
			String taskInstanceId,
			String actionName) {
		adminService.mesuraIniciar("jBPM executeActionInstanciaTasca", "jbpmDao");
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
		adminService.mesuraCalcular("jBPM executeActionInstanciaTasca", "jbpmDao");
	}
	public void retrocedirAccio(
			String processInstanceId,
			String actionName,
			List<String> params) {
		adminService.mesuraIniciar("jBPM retrocedirAccio", "jbpmDao");
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
		adminService.mesuraCalcular("jBPM retrocedirAccio", "jbpmDao");
	}

	public void changeProcessInstanceVersion(
			String processInstanceId,
			int newVersion) {
		adminService.mesuraIniciar("jBPM changeProcessInstanceVersion", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		ChangeProcessInstanceVersionCommand command = new ChangeProcessInstanceVersionCommand(
				id,
				newVersion);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM changeProcessInstanceVersion", "jbpmDao");
	}

	public void signalToken(
			long tokenId,
			String transitionName) {
		adminService.mesuraIniciar("jBPM signalToken", "jbpmDao");
		SignalCommand command = new SignalCommand(tokenId, transitionName);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				tokenId,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM signalToken", "jbpmDao");
	}

	@SuppressWarnings("unchecked")
	public List<Timer> findTimersWithProcessInstanceId(
			String processInstanceId) {
		adminService.mesuraIniciar("jBPM findTimersWithProcessInstanceId", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		FindProcessInstanceTimersCommand command = new FindProcessInstanceTimersCommand(id);
		List<Timer> llista = (List<Timer>)commandService.execute(command);
		adminService.mesuraCalcular("jBPM findTimersWithProcessInstanceId", "jbpmDao");
		return llista;
	}

	public void suspendTimer(
			long timerId,
			Date dueDate) {
		adminService.mesuraIniciar("jBPM suspendTimer", "jbpmDao");
		SuspendProcessInstanceTimerCommand command = new SuspendProcessInstanceTimerCommand(timerId);
		command.setDueDate(dueDate);
		commandService.execute(command);
		adminService.mesuraCalcular("jBPM suspendTimer", "jbpmDao");
	}
	public void resumeTimer(
			long timerId,
			Date dueDate) {
		adminService.mesuraIniciar("jBPM resumeTimer", "jbpmDao");
		ResumeProcessInstanceTimerCommand command = new ResumeProcessInstanceTimerCommand(timerId);
		command.setDueDate(dueDate);
		commandService.execute(command);
		adminService.mesuraCalcular("jBPM resumeTimer", "jbpmDao");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Token, List<ProcessLog>> getProcessInstanceLogs(String processInstanceId) {
		adminService.mesuraIniciar("jBPM getProcessInstanceLogs", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		FindProcessInstanceLogsCommand command = new FindProcessInstanceLogsCommand(id);
		Map resultat = (Map)commandService.execute(command);
		adminService.mesuraCalcular("jBPM getProcessInstanceLogs", "jbpmDao");
		return resultat;
	}

	public long addProcessInstanceMessageLog(String processInstanceId, String message) {
		adminService.mesuraIniciar("jBPM addProcessInstanceMessageLog", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		AddProcessInstanceMessageLogCommand command = new AddProcessInstanceMessageLogCommand(id, message);
		long resultat = ((Long)commandService.execute(command)).longValue();
		adminService.mesuraCalcular("jBPM addProcessInstanceMessageLog", "jbpmDao");
		return resultat;
	}
	public long addTaskInstanceMessageLog(String taskInstanceId, String message) {
		adminService.mesuraIniciar("jBPM addTaskInstanceMessageLog", "jbpmDao");
		final long id = Long.parseLong(taskInstanceId);
		AddTaskInstanceMessageLogCommand command = new AddTaskInstanceMessageLogCommand(id, message);
		long resultat = ((Long)commandService.execute(command)).longValue();
		adminService.mesuraCalcular("jBPM addTaskInstanceMessageLog", "jbpmDao");
		return resultat;
	}

	public Long getVariableIdFromVariableLog(long variableLogId) {
		adminService.mesuraIniciar("jBPM getVariableIdFromVariableLog", "jbpmDao");
		GetVariableIdFromVariableLogCommand command = new GetVariableIdFromVariableLogCommand(variableLogId);
		Long resultat = (Long)commandService.execute(command);
		adminService.mesuraCalcular("jBPM getVariableIdFromVariableLog", "jbpmDao");
		return resultat;
	}
	public Long getTaskIdFromVariableLog(long variableLogId) {
		adminService.mesuraIniciar("jBPM getTaskIdFromVariableLog", "jbpmDao");
		GetTaskIdFromVariableLogCommand command = new GetTaskIdFromVariableLogCommand(variableLogId);
		Long resultat = (Long)commandService.execute(command);
		adminService.mesuraCalcular("jBPM getTaskIdFromVariableLog", "jbpmDao");
		return resultat;
	}

	public void cancelProcessInstance(long id) {
		adminService.mesuraIniciar("jBPM cancelProcessInstance", "jbpmDao");
		CancelProcessInstanceCommand command = new CancelProcessInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM cancelProcessInstance", "jbpmDao");
	}
	public void revertProcessInstanceEnd(long id) {
		adminService.mesuraIniciar("jBPM revertProcessInstanceEnd", "jbpmDao");
		RevertProcessInstanceEndCommand command = new RevertProcessInstanceEndCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM revertProcessInstanceEnd", "jbpmDao");
	}

	public void cancelToken(long id) {
		adminService.mesuraIniciar("jBPM cancelToken", "jbpmDao");
		CancelTokenCommand command = new CancelTokenCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		commandService.execute(autoSaveCommand);
		adminService.mesuraCalcular("jBPM cancelToken", "jbpmDao");
	}
	public void revertTokenEnd(long id) {
		adminService.mesuraIniciar("jBPM revertTokenEnd", "jbpmDao");
		JbpmToken jtoken = getTokenById(String.valueOf(id));
		RevertTokenEndCommand command = new RevertTokenEndCommand(jtoken);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		commandService.execute(autoSaveCommand);
//		this.sessionFactory.getCurrentSession().refresh(jtoken.getToken());
		jtoken.getToken().setAbleToReactivateParent(true);
		adminService.mesuraCalcular("jBPM revertTokenEnd", "jbpmDao");
	}

	public JbpmTask findEquivalentTaskInstance(long tokenId, long taskInstanceId) {
		adminService.mesuraIniciar("jBPM findEquivalentTaskInstance", "jbpmDao");
		GetTaskInstanceCommand commandGetTask = new GetTaskInstanceCommand(taskInstanceId);
		TaskInstance ti = (TaskInstance)commandService.execute(commandGetTask);
		FindTaskInstanceForTokenAndTaskCommand command = new FindTaskInstanceForTokenAndTaskCommand(tokenId, ti.getTask().getName());
		JbpmTask resultat = new JbpmTask((TaskInstance)commandService.execute(command));
		adminService.mesuraCalcular("jBPM findEquivalentTaskInstance", "jbpmDao");
		return resultat;
	}

	public boolean isProcessStateNodeJoinOrFork(long processInstanceId, String nodeName) {
		adminService.mesuraIniciar("jBPM isProcessStateNodeJoinOrFork", "jbpmDao");
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(processInstanceId);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		Node node = pi.getProcessDefinition().getNode(nodeName);
		String nodeClassName = node.toString();
		NodeType nodeType = node.getNodeType();
		adminService.mesuraCalcular("jBPM isProcessStateNodeJoinOrFork", "jbpmDao");
		return (nodeClassName.startsWith("ProcessState") || nodeType == NodeType.Fork || nodeType == NodeType.Join);
	}

	public boolean isJoinNode(long processInstanceId, String nodeName) {
		adminService.mesuraIniciar("jBPM isJoinNode", "jbpmDao");
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(processInstanceId);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		NodeType nodeType = pi.getProcessDefinition().getNode(nodeName).getNodeType();
		adminService.mesuraCalcular("jBPM isJoinNode", "jbpmDao");
		return nodeType == NodeType.Join;
	}
	
	public ProcessLog getProcessLogById(Long id){
		adminService.mesuraIniciar("jBPM getProcessLogById", "jbpmDao");
		GetProcessLogByIdCommand command = new GetProcessLogByIdCommand(id.longValue());
		ProcessLog log = (ProcessLog)commandService.execute(command);
		adminService.mesuraCalcular("jBPM getProcessLogById", "jbpmDao");
		return log;
	}

	public Node getNodeByName(long processInstanceId, String nodeName) {
		adminService.mesuraIniciar("jBPM getNodeByName", "jbpmDao");
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(processInstanceId);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		Node node = pi.getProcessDefinition().getNode(nodeName);
		adminService.mesuraCalcular("jBPM getNodeByName", "jbpmDao");
		return node;
	}
	
	public boolean hasStartBetweenLogs(long begin, long end, long taskInstanceId) {
		adminService.mesuraIniciar("jBPM hasStartBetweenLogs", "jbpmDao");
		HasStartBetweenLogsCommand command = new HasStartBetweenLogsCommand(begin, end, taskInstanceId);
		Boolean hasStart = (Boolean)commandService.execute(command);
		adminService.mesuraCalcular("jBPM hasStartBetweenLogs", "jbpmDao");
		return hasStart.booleanValue();
	}
	

	@Autowired
	public void setCommandService(CommandService commandService) {
		this.commandService = commandService;
	}
	
	public LlistatIds findListPersonalTasks(
			String usuariBo, 
			String tasca, 
			List<Long> idsExpedients, 
			Date dataCreacioInici, 
			Date dataCreacioFi, 
			Integer prioritat, 
			Date dataLimitInici, 
			Date dataLimitFi, 
			int firstRow, 
			int maxResults, 
			String sort, 
			boolean asc) {
		adminService.mesuraIniciar("jBPM findListPersonalTasks", "jbpmDao");
		GetRootProcessInstancesForActiveTasksCommand command = new GetRootProcessInstancesForActiveTasksCommand(usuariBo, tasca, idsExpedients, dataCreacioInici, dataCreacioFi, prioritat, dataLimitInici, dataLimitFi, sort, asc,false);
		command.setFirstRow(firstRow);
		command.setMaxResults(maxResults);
		LlistatIds llistat = (LlistatIds)commandService.execute(command);
		adminService.mesuraCalcular("jBPM findListPersonalTasks", "jbpmDao");
		return llistat;
	}

	public LlistatIds findListGroupTasks(
			String usuariBo, 
			String tasca, 
			List<Long> idsExpedients, 
			Date dataCreacioInici, 
			Date dataCreacioFi, 
			Integer prioritat, 
			Date dataLimitInici, 
			Date dataLimitFi, 
			int firstRow, 
			int maxResults, 
			String sort, 
			boolean asc) {
		adminService.mesuraIniciar("jBPM findListGroupTasks", "jbpmDao");
		GetRootProcessInstancesForActiveTasksCommand command = new GetRootProcessInstancesForActiveTasksCommand(usuariBo, tasca, idsExpedients, dataCreacioInici, dataCreacioFi, prioritat, dataLimitInici, dataLimitFi, sort, asc,true);
		command.setFirstRow(firstRow);
		command.setMaxResults(maxResults);
		LlistatIds llistat = (LlistatIds)commandService.execute(command);
		adminService.mesuraCalcular("jBPM findListGroupTasks", "jbpmDao");
		return llistat;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmTask> findPersonalTasks(List<Long> ids, String usuariBo) {
		adminService.mesuraIniciar("jBPM findPersonalTasks ids", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetPersonalTaskListCommand command = new GetPersonalTaskListCommand(usuariBo, ids);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		adminService.mesuraCalcular("jBPM findPersonalTasks ids", "jbpmDao");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmTask> findGroupTasks(List<Long> ids, String usuariBo) {
		adminService.mesuraIniciar("jBPM findGroupTasks ids", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetGroupTaskListCommand command = new GetGroupTaskListCommand(usuariBo, ids);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		adminService.mesuraCalcular("jBPM findGroupTasks ids", "jbpmDao");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmTask> findPersonalTasks(String usuariBo) {
		adminService.mesuraIniciar("jBPM findPersonalTasks", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetPersonalTaskListCommand command = new GetPersonalTaskListCommand(usuariBo);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		adminService.mesuraCalcular("jBPM findPersonalTasks", "jbpmDao");
		return resultat;
	}

	public LlistatIds findListIdsPersonalTasks(String actorId,List<Long> idsExpedients) {
		adminService.mesuraIniciar("jBPM findListIdsPersonalTasks", "jbpmDao");
		GetRootProcessInstancesForActiveTasksCommand command = new GetRootProcessInstancesForActiveTasksCommand(actorId, idsExpedients, false);
		LlistatIds resultado = (LlistatIds)commandService.execute(command);
		adminService.mesuraCalcular("jBPM findListIdsPersonalTasks", "jbpmDao");
		return resultado;
	}
	
	public LlistatIds findListIdsGroupTasks(String actorId,List<Long> idsExpedients) {
		adminService.mesuraIniciar("jBPM findListIdsGroupTasks", "jbpmDao");
		GetRootProcessInstancesForActiveTasksCommand command = new GetRootProcessInstancesForActiveTasksCommand(actorId, idsExpedients, true);
		LlistatIds resultado = (LlistatIds)commandService.execute(command);
		adminService.mesuraCalcular("jBPM findListIdsGroupTasks", "jbpmDao");
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<JbpmTask> findGroupTasks(String actorId) {
		adminService.mesuraIniciar("jBPM findGroupTasks", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		
		GetGroupTaskListCommand command = new GetGroupTaskListCommand(actorId);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		adminService.mesuraCalcular("jBPM findGroupTasks", "jbpmDao");
		return resultat;
	}
	
	public List<Long> findRootProcessInstanceIdsWithActiveTasksForActorId(String actorId,List<Long> idsExpedients) {
		adminService.mesuraIniciar("jBPM findRootProcessInstanceIdsWithActiveTasksForActorId", "jbpmDao");
		List<Long> resultat = new ArrayList<Long>();
		GetRootProcessInstancesForActiveTasksCommand commandPersonal = new GetRootProcessInstancesForActiveTasksCommand(actorId, idsExpedients, false);
		LlistatIds resultadoPersonal = (LlistatIds)commandService.execute(commandPersonal);
		resultat.addAll(resultadoPersonal.getIds());
		GetRootProcessInstancesForActiveTasksCommand commandGroup = new GetRootProcessInstancesForActiveTasksCommand(actorId, idsExpedients, true);
		LlistatIds resultadoGroup = (LlistatIds)commandService.execute(commandGroup);
		resultat.addAll(resultadoGroup.getIds());
		adminService.mesuraCalcular("jBPM findRootProcessInstanceIdsWithActiveTasksForActorId", "jbpmDao");
		return resultat;
	}
	
	public List<Long> findRootProcessInstancesForExpedientsWithActiveTasksCommand(String actorId,List<Long> idsExpedients) {
		adminService.mesuraIniciar("jBPM findRootProcessInstancesForExpedientsWithActiveTasksCommand", "jbpmDao");
		List<Long> resultat = new ArrayList<Long>();
		GetRootProcessInstancesForExpedientsWithActiveTasksCommand commandPersonal = new GetRootProcessInstancesForExpedientsWithActiveTasksCommand(actorId, idsExpedients, false);
		resultat.addAll((List<Long>)commandService.execute(commandPersonal));
		GetRootProcessInstancesForExpedientsWithActiveTasksCommand commandGroup = new GetRootProcessInstancesForExpedientsWithActiveTasksCommand(actorId, idsExpedients, true);
		resultat.addAll((List<Long>)commandService.execute(commandGroup));
		adminService.mesuraCalcular("jBPM findRootProcessInstancesForExpedientsWithActiveTasksCommand", "jbpmDao");
		return resultat;
	}
}
