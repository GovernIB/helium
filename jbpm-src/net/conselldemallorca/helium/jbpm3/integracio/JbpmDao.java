/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.conselldemallorca.helium.model.exception.DeploymentException;

import org.jbpm.command.ChangeProcessInstanceVersionCommand;
import org.jbpm.command.CommandService;
import org.jbpm.command.DeleteProcessDefinitionCommand;
import org.jbpm.command.DeployProcessCommand;
import org.jbpm.command.GetProcessInstanceCommand;
import org.jbpm.command.GetProcessInstancesCommand;
import org.jbpm.command.GetTaskInstanceCommand;
import org.jbpm.command.ResumeProcessInstanceCommand;
import org.jbpm.command.SignalCommand;
import org.jbpm.command.StartProcessInstanceCommand;
import org.jbpm.command.SuspendProcessInstanceCommand;
import org.jbpm.command.TaskInstanceEndCommand;
import org.jbpm.file.def.FileDefinition;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Dao per a l'accés a la funcionalitat de jBPM3
 * 
 * @author Josep Gayà <josepg@limit.es>
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
		long pdid = new Long(jbpmId).longValue();
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		resposta = new JbpmProcessDefinition((ProcessDefinition)commandService.execute(command));
		return resposta;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmProcessDefinition> getSubProcessDefinitions(String jbpmId) {
		List<JbpmProcessDefinition> resposta = null;
		long pdid = new Long(jbpmId).longValue();
		GetSubProcessDefinitionsCommand command = new GetSubProcessDefinitionsCommand(pdid);
		resposta = new ArrayList<JbpmProcessDefinition>();
		for (ProcessDefinition pd: (List<ProcessDefinition>)commandService.execute(command)) {
			resposta.add(new JbpmProcessDefinition(pd));
		}
		return resposta;
	}

	public JbpmProcessInstance getProcessInstance(String processInstanceId) {
		JbpmProcessInstance resposta = null;
		long piid = new Long(processInstanceId).longValue();
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(piid);
		resposta = new JbpmProcessInstance((ProcessInstance)commandService.execute(command));
		return resposta;
	}

	public String getStartTaskName(String jbpmId) {
		String resposta = null;
		long pdid = new Long(jbpmId).longValue();
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		org.jbpm.taskmgmt.def.Task startTask = processDefinition.getTaskMgmtDefinition().getStartTask();
		if (startTask != null)
			resposta = startTask.getName();
		return resposta;
	}

	@SuppressWarnings("unchecked")
	public List<String> TaskNamesFromDeployedProcessDefinition(JbpmProcessDefinition dpd) {
		List<String> taskNames = new ArrayList<String>();
		ProcessDefinition pd = dpd.getProcessDefinition();
		Map<String,Object> tasks = pd.getTaskMgmtDefinition().getTasks();
		if (tasks != null) {
			for (String taskName: tasks.keySet()) {
				taskNames.add(taskName);
			}
		}
		return taskNames;
	}

	public void esborrarDesplegament(String jbpmId) {
		DeleteProcessDefinitionCommand command = new DeleteProcessDefinitionCommand();
		command.setId(new Long(jbpmId).longValue());
		commandService.execute(command);
	}

	@SuppressWarnings("unchecked")
	public Set<String> getResourceNames(String jbpmId) {
		Set<String> resources = null;
		long pdid = new Long(jbpmId).longValue();
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		FileDefinition fd = processDefinition.getFileDefinition();
		if (fd != null)
			resources = fd.getBytesMap().keySet();
		else
			resources = new HashSet<String>();
		return resources;
	}

	public byte[] getResourceBytes(String jbpmId, String resourceName) {
		byte[] bytes = null;
		long pdid = new Long(jbpmId).longValue();
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		bytes = processDefinition.getFileDefinition().getBytes(resourceName);
		return bytes;
	}

	public JbpmProcessInstance startProcessInstanceByKey(
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
	}
	public JbpmProcessInstance startProcessInstanceById(
			String actorId,
			String processDefinitionId,
			Map<String, Object> variables,
			String transitionName) {
		JbpmProcessInstance resultat = null;
		StartProcessInstanceCommand command = new StartProcessInstanceCommand();
		command.setProcessDefinitionId(new Long(processDefinitionId).longValue());
		command.setActorId(actorId);
		if (variables != null)
			command.setVariables(variables);
		if (transitionName != null)
			command.setStartTransitionName(transitionName);
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		resultat = new JbpmProcessInstance(processInstance);
		return resultat;
	}
	public JbpmProcessInstance getRootProcessInstance(
			String processInstanceId) {
		JbpmProcessInstance resultat = null;
		long id = new Long(processInstanceId).longValue();
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(id);
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		while (processInstance.getSuperProcessToken() != null) {
			processInstance = processInstance.getSuperProcessToken().getProcessInstance();
		}
		resultat = new JbpmProcessInstance(processInstance);
		return resultat;
	}
	@SuppressWarnings("unchecked")
	public List<JbpmProcessInstance> getProcessInstanceTree(
			String rootProcessInstanceId) {
		List<JbpmProcessInstance> resposta = null;
		long id = new Long(rootProcessInstanceId).longValue();
		GetProcessInstancesTreeCommand command = new GetProcessInstancesTreeCommand(id);
		List<ProcessInstance> processInstancesTree = (List<ProcessInstance>)commandService.execute(command);
		resposta = new ArrayList<JbpmProcessInstance>();
		for (ProcessInstance pi: processInstancesTree) {
			resposta.add(new JbpmProcessInstance(pi));
		}
		return resposta;
	}

	public void deleteProcessInstance(
			String processInstanceId) {
		long id = new Long(processInstanceId).longValue();
		DeleteProcessInstanceCommand command = new DeleteProcessInstanceCommand(id);
		commandService.execute(command);
	}

	public void suspendProcessInstance(
			String processInstanceId) {
		long id = new Long(processInstanceId).longValue();
		SuspendProcessInstanceCommand command = new SuspendProcessInstanceCommand();
		command.setProcessInstanceId(id);
		commandService.execute(command);
	}
	public void suspendProcessInstances(
			String[] processInstanceIds) {
		long[] ids = new long[processInstanceIds.length];
		for (int i = 0; i < processInstanceIds.length; i++)
			ids[i] = new Long(processInstanceIds[i]).longValue();
		SuspendProcessInstanceCommand command = new SuspendProcessInstanceCommand();
		command.setProcessInstanceIds(ids);
		commandService.execute(command);
	}
	public void resumeProcessInstance(
			String processInstanceId) {
		long id = new Long(processInstanceId).longValue();
		ResumeProcessInstanceCommand command = new ResumeProcessInstanceCommand();
		command.setProcessInstanceId(id);
		commandService.execute(command);
	}
	public void resumeProcessInstances(
			String[] processInstanceIds) {
		long[] ids = new long[processInstanceIds.length];
		for (int i = 0; i < processInstanceIds.length; i++)
			ids[i] = new Long(processInstanceIds[i]).longValue();
		ResumeProcessInstanceCommand command = new ResumeProcessInstanceCommand();
		command.setProcessInstanceIds(ids);
		commandService.execute(command);
	}
	public void describeProcessInstance(
			String processInstanceId,
			String description) {
		long id = new Long(processInstanceId).longValue();
		DescribeProcessInstanceCommand command = new DescribeProcessInstanceCommand(id);
		command.setDescription(description);
		commandService.execute(command);
	}

	@SuppressWarnings("unchecked")
	public List<JbpmProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId) {
		List<JbpmProcessInstance> resultat = new ArrayList<JbpmProcessInstance>();
		long id = new Long(processDefinitionId).longValue();
		GetProcessInstancesCommand command = new GetProcessInstancesCommand();
		command.setProcessInstanceId(id); // Això està bé, el command agafa setProcessInstanceId com si fos setProcessDefinitionId
		for (ProcessInstance pi : (List<ProcessInstance>)commandService.execute(command))
			resultat.add(new JbpmProcessInstance(pi));
		return resultat;
	}

	public JbpmProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) {
		JbpmProcessDefinition resultat = null;
		long id = new Long(processInstanceId).longValue();
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		if (pi != null)
			resultat = new JbpmProcessDefinition(pi.getProcessDefinition());
		return resultat;
	}

	public JbpmTask getTaskById(String taskId) {
		JbpmTask resposta = null;
		long id = new Long(taskId).longValue();
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		resposta = new JbpmTask((TaskInstance)commandService.execute(command));
		return resposta;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmTask> findPersonalTasks(String actorId) {
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetPersonalTaskListCommand command = new GetPersonalTaskListCommand(actorId);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		return resultat;
	}
	@SuppressWarnings("unchecked")
	public List<JbpmTask> findGroupTasks(String actorId) {
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetGroupTaskListCommand command = new GetGroupTaskListCommand(actorId);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		return resultat;
	}

	public List<String> findTaskOutcomes(String jbpmId, String taskName) {
		List<String> resultat = new ArrayList<String>();
		long pdid = new Long(jbpmId).longValue();
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		org.jbpm.taskmgmt.def.Task task = processDefinition.getTaskMgmtDefinition().getTask(taskName);
		if (task != null) {
			for (Transition transition: (List<Transition>)task.getStartState().getLeavingTransitions())
				resultat.add(transition.getName());
		}
		return resultat;
	}
	@SuppressWarnings("unchecked")
	public List<String> findTaskInstanceOutcomes(String taskInstanceId) {
		List<String> resultat = new ArrayList<String>();
		long id = new Long(taskInstanceId).longValue();
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		List<Transition> outcomes = (List<Transition>)taskInstance.getAvailableTransitions();
		if (outcomes != null) {
			for (Transition transition: outcomes)
				resultat.add(transition.getName());
		}
		return resultat;
	}
	public void takeTaskInstance(String taskId, String actorId) {
		long id = new Long(taskId).longValue();
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		taskInstance.setActorId(actorId);
	}
	public JbpmTask cloneTaskInstance(String taskId, String actorId, Map<String, Object> variables) {
		JbpmTask resposta = null;
		long id = new Long(taskId).longValue();
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
		long id = new Long(taskId).longValue();
		StartTaskInstanceCommand command = new StartTaskInstanceCommand(id);
		resposta = new JbpmTask((TaskInstance)commandService.execute(command));
		return resposta;
	}
	public JbpmTask cancelTaskInstance(String taskId) {
		JbpmTask resposta = null;
		long id = new Long(taskId).longValue();
		CancelTaskInstanceCommand command = new CancelTaskInstanceCommand(id);
		resposta = new JbpmTask((TaskInstance)commandService.execute(command));
		return resposta;
	}
	public JbpmTask suspendTaskInstance(String taskId) {
		JbpmTask resposta = null;
		long id = new Long(taskId).longValue();
		SuspendTaskInstanceCommand command = new SuspendTaskInstanceCommand(id);
		resposta = new JbpmTask((TaskInstance)commandService.execute(command));
		return resposta;
	}
	public JbpmTask resumeTaskInstance(String taskId) {
		JbpmTask resposta = null;
		long id = new Long(taskId).longValue();
		ResumeTaskInstanceCommand command = new ResumeTaskInstanceCommand(id);
		resposta = new JbpmTask((TaskInstance)commandService.execute(command));
		return resposta;
	}
	public JbpmTask reassignTaskInstance(String taskId, String expression) {
		JbpmTask resposta = null;
		long id = new Long(taskId).longValue();
		ReassignTaskInstanceCommand command = new ReassignTaskInstanceCommand(id, expression);
		resposta = new JbpmTask((TaskInstance)commandService.execute(command));
		return resposta;
	}
	public void setTaskInstanceVariables(String taskId, Map<String, Object> variables) {
		long id = new Long(taskId).longValue();
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		for (String key: variables.keySet())
			taskInstance.setVariableLocally(
					key,
					variables.get(key));
	}
	public void setTaskInstanceVariable(String taskId, String codi, Object valor) {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put(codi, valor);
		setTaskInstanceVariables(taskId, vars);
	}
	public void deleteTaskInstanceVariable(String taskId, String varName) {
		long id = new Long(taskId).longValue();
		DeleteTaskInstanceVariablesCommand command = new DeleteTaskInstanceVariablesCommand(
				id,
				new String[] {varName});
		command.setLocally(true);
		commandService.execute(command);
	}
	public Object getTaskInstanceVariable(String taskId, String varName) {
		Object resultat = null;
		long id = new Long(taskId).longValue();
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		resultat = taskInstance.getVariableLocally(varName);
		return resultat;
	}
	@SuppressWarnings("unchecked")
	public Map<String, Object> getTaskInstanceVariables(String taskId) {
		Map<String, Object> resultat = null;
		long id = new Long(taskId).longValue();
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		resultat = (Map<String, Object>)taskInstance.getVariablesLocally();
		return resultat;
	}
	@SuppressWarnings("unchecked")
	public void deleteTaskInstanceVariablesAll(String taskId) {
		long id = new Long(taskId).longValue();
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		Map<String, Object> vars = taskInstance.getVariablesLocally();
		for (String codi: vars.keySet())
			taskInstance.deleteVariable(codi);
	}
	public void completeTaskInstance(String taskId, String outcome) {
		long id = new Long(taskId).longValue();
		TaskInstanceEndCommand command = new TaskInstanceEndCommand(id, outcome);
		commandService.execute(command);
	}
	public List<JbpmTask> findTaskInstancesForProcessInstance(String processInstanceId) {
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		long id = new Long(processInstanceId).longValue();
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
		long id = new Long(processInstanceId).longValue();
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		resultat = pi.getContextInstance().getVariables();
		return resultat;
	}
	public Object getProcessInstanceVariable(String processInstanceId, String varName) {
		Object resultat = null;
		long id = new Long(processInstanceId).longValue();
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		resultat = pi.getContextInstance().getVariable(varName);
		return resultat;
	}
	public void setProcessInstanceVariable(String processInstanceId, String varName, Object value) {
		long id = new Long(processInstanceId).longValue();
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(id);
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		processInstance.getContextInstance().setVariable(varName, value);
	}
	public void deleteProcessInstanceVariable(String processInstanceId, String varName) {
		long id = new Long(processInstanceId).longValue();
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(id);
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		processInstance.getContextInstance().deleteVariable(varName);
	}

	public JbpmToken getTokenById(String tokenId) {
		long id = new Long(tokenId).longValue();
		GetTokenByIdCommand command = new GetTokenByIdCommand(id);
		return new JbpmToken((Token)commandService.execute(command));
	}
	@SuppressWarnings("unchecked")
	public Map<String, JbpmToken> getActiveTokens(String processInstanceId) {
		Map<String, JbpmToken> resposta = new HashMap<String, JbpmToken>();
		long id = new Long(processInstanceId).longValue();
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(id);
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		Token root = processInstance.getRootToken();
		if (!root.hasEnded())
			resposta.put(root.getName(), new JbpmToken(root));
		Map<String, Token> activeTokens = processInstance.getRootToken().getActiveChildren();
		for (String tokenName: activeTokens.keySet()) {
			resposta.put(tokenName, new JbpmToken(activeTokens.get(tokenName)));
		}
		return resposta;
	}
	public Map<String, JbpmToken> getAllTokens(String processInstanceId) {
		Map<String, JbpmToken> resposta = new HashMap<String, JbpmToken>();
		long id = new Long(processInstanceId).longValue();
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
		long id = new Long(tokenId).longValue();
		FindArrivingNodeNamesCommand command = new FindArrivingNodeNamesCommand(id);
		return (List<String>)commandService.execute(command);
	}

	public void tokenRedirect(String tokenId, String nodeName, boolean cancelTasks) {
		long id = new Long(tokenId).longValue();
		TokenRedirectCommand command = new TokenRedirectCommand(id, nodeName);
		command.setCancelTasks(cancelTasks);
		commandService.execute(command);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> evaluateScript(
			String processInstanceId,
			String script,
			Set<String> outputNames) {
		Map<String,Object> resultat = null;
		long id = new Long(processInstanceId).longValue();
		EvaluateScriptCommand command = new EvaluateScriptCommand(
				id,
				script,
				outputNames);
		resultat = (Map<String,Object>)commandService.execute(command);
		return resultat;
	}

	public Object evaluateExpression(
			String processInstanceId,
			String expression) {
		long id = new Long(processInstanceId).longValue();
		EvaluateExpressionCommand command = new EvaluateExpressionCommand(
				id,
				expression);
		return commandService.execute(command);
	}

	public void executeAction(
			String processInstanceId,
			String actionName) {
		long id = new Long(processInstanceId).longValue();
		ExecuteActionCommand command = new ExecuteActionCommand(
				id,
				actionName);
		commandService.execute(command);
	}

	public void changeProcessInstanceVersion(
			String processInstanceId,
			int newVersion) {
		long id = new Long(processInstanceId).longValue();
		ChangeProcessInstanceVersionCommand command = new ChangeProcessInstanceVersionCommand(
				id,
				newVersion);
		commandService.execute(command);
	}

	public void signalToken(
			long tokenId,
			String transitionName) {
		SignalCommand command = new SignalCommand(tokenId, transitionName);
		commandService.execute(command);
	}



	@Autowired
	public void setCommandService(CommandService commandService) {
		this.commandService = commandService;
	}

}
