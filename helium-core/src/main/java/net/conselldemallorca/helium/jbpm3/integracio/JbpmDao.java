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

import net.conselldemallorca.helium.core.extern.formulari.LlistatIds;
import net.conselldemallorca.helium.core.model.exception.DeploymentException;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;

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

	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;


	public JbpmProcessDefinition desplegar(
			String nomArxiu,
			byte[] contingut) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM desplegar", "jbpmDao");
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
				mesuresTemporalsHelper.mesuraCalcular("jBPM desplegar", "jbpmDao");
				throw new DeploymentException("No s'ha pogut deplegar l'arxiu", ex);
			}
		} else if (nomArxiu.endsWith("ar")) {
			command = new DeployProcessCommand(contingut);
		} else {
			mesuresTemporalsHelper.mesuraCalcular("jBPM desplegar", "jbpmDao");
			throw new DeploymentException("Arxiu amb extensió no suportada " + nomArxiu + ". Només es suporten les extensions .xml i .*ar");
		}
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		resposta = new JbpmProcessDefinition(processDefinition);
		mesuresTemporalsHelper.mesuraCalcular("jBPM desplegar", "jbpmDao");
		return resposta;
	}

	public JbpmProcessDefinition getProcessDefinition(String jbpmId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getProcessDefinition", "jbpmDao");
		JbpmProcessDefinition resposta = null;
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		if (processDefinition != null)
			resposta = new JbpmProcessDefinition((ProcessDefinition)commandService.execute(command));
		mesuresTemporalsHelper.mesuraCalcular("jBPM getProcessDefinition", "jbpmDao");
		return resposta;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmProcessDefinition> getSubProcessDefinitions(String jbpmId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getSubProcessDefinitions", "jbpmDao");
		List<JbpmProcessDefinition> resposta = new ArrayList<JbpmProcessDefinition>();
		final long pdid = Long.parseLong(jbpmId);
		GetSubProcessDefinitionsCommand command = new GetSubProcessDefinitionsCommand(pdid);
		for (ProcessDefinition pd: (List<ProcessDefinition>)commandService.execute(command)) {
			resposta.add(new JbpmProcessDefinition(pd));
		}
		mesuresTemporalsHelper.mesuraCalcular("jBPM getSubProcessDefinitions", "jbpmDao");
		return resposta;
	}

	public JbpmProcessInstance getProcessInstance(String processInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getProcessInstance", "jbpmDao");
		JbpmProcessInstance resposta = null;
		final long piid = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(piid);
		resposta = new JbpmProcessInstance((ProcessInstance)commandService.execute(command));
		mesuresTemporalsHelper.mesuraCalcular("jBPM getProcessInstance", "jbpmDao");
		return resposta;
	}

	public String getStartTaskName(String jbpmId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getStartTaskName", "jbpmDao");
		String resposta = null;
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		if (processDefinition != null) {
			org.jbpm.taskmgmt.def.Task startTask = processDefinition.getTaskMgmtDefinition().getStartTask();
			if (startTask != null)
				resposta = startTask.getName();
		}
		mesuresTemporalsHelper.mesuraCalcular("jBPM getStartTaskName", "jbpmDao");
		return resposta;
	}

	@SuppressWarnings("unchecked")
	public List<String> getTaskNamesFromDeployedProcessDefinition(JbpmProcessDefinition dpd) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getTaskNamesFromDeployedProcessDefinition", "jbpmDao");
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
		mesuresTemporalsHelper.mesuraCalcular("jBPM getTaskNamesFromDeployedProcessDefinition", "jbpmDao");
		return taskNames;
	}

	public void esborrarDesplegament(String jbpmId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM esborrarDesplegament", "jbpmDao");
		DeleteProcessDefinitionCommand command = new DeleteProcessDefinitionCommand();
		command.setId(Long.parseLong(jbpmId));
		commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM esborrarDesplegament", "jbpmDao");
	}

	@SuppressWarnings("unchecked")
	public Set<String> getResourceNames(String jbpmId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getResourceNames", "jbpmDao");
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
		mesuresTemporalsHelper.mesuraCalcular("jBPM getResourceNames", "jbpmDao");
		return resources;
	}

	public byte[] getResourceBytes(String jbpmId, String resourceName) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getResourceBytes", "jbpmDao");
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		byte[] bytes = processDefinition.getFileDefinition().getBytes(resourceName);
		mesuresTemporalsHelper.mesuraCalcular("jBPM getResourceBytes", "jbpmDao");
		return bytes;
	}

	public JbpmProcessInstance startProcessInstanceById(
			String actorId,
			String processDefinitionId,
			Map<String, Object> variables) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM startProcessInstanceById", "jbpmDao");
		StartProcessInstanceCommand command = new StartProcessInstanceCommand();
		command.setProcessDefinitionId(Long.parseLong(processDefinitionId));
		command.setActorId(actorId);
		if (variables != null)
			command.setVariables(variables);
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		JbpmProcessInstance resultat = new JbpmProcessInstance(processInstance);
		mesuresTemporalsHelper.mesuraCalcular("jBPM startProcessInstanceById", "jbpmDao");
		return resultat;
	}
	
	public void signalProcessInstance(
			String processInstanceId,
			String transitionName) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM signalProcessInstance", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		SignalProcessInstanceCommand command = new SignalProcessInstanceCommand(id);
		if (transitionName != null)
			command.setStartTransitionName(transitionName);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM signalProcessInstance", "jbpmDao");
	}
	
	public JbpmProcessInstance getRootProcessInstance(
			String processInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getRootProcessInstance", "jbpmDao");
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(Long.parseLong(processInstanceId));
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		while (processInstance.getSuperProcessToken() != null) {
			final long id = processInstance.getSuperProcessToken().getProcessInstance().getId();
			command.setProcessInstanceId(id);
			processInstance = (ProcessInstance)commandService.execute(command);
		}
		JbpmProcessInstance resultat = new JbpmProcessInstance(processInstance);
		mesuresTemporalsHelper.mesuraCalcular("jBPM getRootProcessInstance", "jbpmDao");
		return resultat;
	}
	
	@SuppressWarnings("unchecked")
	public List<JbpmProcessInstance> getProcessInstanceTree(
			String rootProcessInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getProcessInstanceTree", "jbpmDao");
		List<JbpmProcessInstance> resposta = new ArrayList<JbpmProcessInstance>();
		final long id = Long.parseLong(rootProcessInstanceId);
		GetProcessInstancesTreeCommand command = new GetProcessInstancesTreeCommand(id);
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resposta.add(new JbpmProcessInstance(pd));
		}
		mesuresTemporalsHelper.mesuraCalcular("jBPM getProcessInstanceTree", "jbpmDao");
		return resposta;
	}

	public void deleteProcessInstance(
			String processInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM deleteProcessInstance", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		DeleteProcessInstanceCommand command = new DeleteProcessInstanceCommand(id);
		commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM deleteProcessInstance", "jbpmDao");
	}

	public void suspendProcessInstance(
			String processInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM suspendProcessInstance", "jbpmDao");
		suspendProcessInstances(new String[] {processInstanceId});
		mesuresTemporalsHelper.mesuraCalcular("jBPM suspendProcessInstance", "jbpmDao");
	}
	
	public void suspendProcessInstances(
			String[] processInstanceIds) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM suspendProcessInstances", "jbpmDao");
		long[] ids = new long[processInstanceIds.length];
		for (int i = 0; i < processInstanceIds.length; i++)
			ids[i] = Long.parseLong(processInstanceIds[i]);
		
		SuspendProcessInstancesCommand command = new SuspendProcessInstancesCommand(ids);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				ids,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM suspendProcessInstances", "jbpmDao");
	}
	
	public void resumeProcessInstance(
			String processInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM resumeProcessInstance", "jbpmDao");
		resumeProcessInstances(new String[] {processInstanceId});
		mesuresTemporalsHelper.mesuraCalcular("jBPM resumeProcessInstance", "jbpmDao");
	}
	
	public void resumeProcessInstances(
			String[] processInstanceIds) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM resumeProcessInstances", "jbpmDao");
		long[] ids = new long[processInstanceIds.length];
		for (int i = 0; i < processInstanceIds.length; i++)
			ids[i] = Long.parseLong(processInstanceIds[i]);
		ResumeProcessInstancesCommand command = new ResumeProcessInstancesCommand(ids);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				ids,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM resumeProcessInstances", "jbpmDao");
	}
	
	public void describeProcessInstance(
			String processInstanceId,
			String description) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM describeProcessInstance", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		DescribeProcessInstanceCommand command = new DescribeProcessInstanceCommand(id, description);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM describeProcessInstance", "jbpmDao");
	}

	@SuppressWarnings("unchecked")
	public List<JbpmProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findProcessInstancesWithProcessDefinitionId", "jbpmDao");
		List<JbpmProcessInstance> resultat = new ArrayList<JbpmProcessInstance>();
		final long id = Long.parseLong(processDefinitionId);
		GetProcessInstancesCommand command = new GetProcessInstancesCommand();
		command.setProcessInstanceId(id); // Això està bé, el command agafa setProcessInstanceId com si fos setProcessDefinitionId
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resultat.add(new JbpmProcessInstance(pd));
		}
		mesuresTemporalsHelper.mesuraCalcular("jBPM findProcessInstancesWithProcessDefinitionId", "jbpmDao");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmProcessInstance> findProcessInstancesWithProcessDefinitionName(String processName) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findProcessInstancesWithProcessDefinitionName", "jbpmDao");
		List<JbpmProcessInstance> resultat = new ArrayList<JbpmProcessInstance>();
		GetProcessInstancesCommand command = new GetProcessInstancesCommand();
		command.setProcessDefinitionName(processName);
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resultat.add(new JbpmProcessInstance(pd));
		}
		mesuresTemporalsHelper.mesuraCalcular("jBPM findProcessInstancesWithProcessDefinitionName", "jbpmDao");
		return resultat;
	}

	public JbpmProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findProcessDefinitionWithProcessInstanceId", "jbpmDao");
		JbpmProcessDefinition resultat = null;
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		if (pi != null)
			resultat = new JbpmProcessDefinition(pi.getProcessDefinition());
		mesuresTemporalsHelper.mesuraCalcular("jBPM findProcessDefinitionWithProcessInstanceId", "jbpmDao");
		return resultat;
	}

	public JbpmTask getTaskById(String taskId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getTaskById", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance ti = (TaskInstance)commandService.execute(command);
		if (ti != null)
			resposta = new JbpmTask(ti);
		mesuresTemporalsHelper.mesuraCalcular("jBPM getTaskById", "jbpmDao");
		return resposta;
	}

	public LlistatIds findListTasks(
			String usuariBo, 
			String tasca, 
			String titol,
			List<Long> idsExpedients, 
			Date dataCreacioInici, 
			Date dataCreacioFi, 
			Integer prioritat, 
			Date dataLimitInici, 
			Date dataLimitFi, 
			int firstRow, 
			int maxResults, 
			String sort, 
			boolean asc,
			boolean tasquesGrup) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findListTasks", "jbpmDao");
		GetProcessInstancesForActiveTasksCommand command = new GetProcessInstancesForActiveTasksCommand(usuariBo, tasca, idsExpedients, dataCreacioInici, dataCreacioFi, prioritat, dataLimitInici, dataLimitFi, sort, asc, (tasquesGrup ? null : false));
		command.setFirstRow(firstRow);
		command.setMaxResults(maxResults);
		command.setTitol(titol);
		LlistatIds llistat = (LlistatIds)commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM findListTasks", "jbpmDao");
		return llistat;
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
		mesuresTemporalsHelper.mesuraIniciar("jBPM findListPersonalTasks", "jbpmDao");
		GetProcessInstancesForActiveTasksCommand command = new GetProcessInstancesForActiveTasksCommand(usuariBo, tasca, idsExpedients, dataCreacioInici, dataCreacioFi, prioritat, dataLimitInici, dataLimitFi, sort, asc, false);
		command.setFirstRow(firstRow);
		command.setMaxResults(maxResults);
		LlistatIds llistat = (LlistatIds)commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM findListPersonalTasks", "jbpmDao");
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
		mesuresTemporalsHelper.mesuraIniciar("jBPM findListGroupTasks", "jbpmDao");
		GetProcessInstancesForActiveTasksCommand command = new GetProcessInstancesForActiveTasksCommand(usuariBo, tasca, idsExpedients, dataCreacioInici, dataCreacioFi, prioritat, dataLimitInici, dataLimitFi, sort, asc, true);
		command.setFirstRow(firstRow);
		command.setMaxResults(maxResults);
		LlistatIds llistat = (LlistatIds)commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM findListGroupTasks", "jbpmDao");
		return llistat;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmTask> findTasks(List<Long> ids) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findTasks ids", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetTaskListCommand command = new GetTaskListCommand(ids);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		mesuresTemporalsHelper.mesuraCalcular("jBPM findTasks ids", "jbpmDao");
		return resultat;
	}
	
	@SuppressWarnings("unchecked")
	public List<JbpmTask> findPersonalTasks(List<Long> ids, String usuariBo) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findPersonalTasks ids", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetPersonalTaskListCommand command = new GetPersonalTaskListCommand(usuariBo, ids);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		mesuresTemporalsHelper.mesuraCalcular("jBPM findPersonalTasks ids", "jbpmDao");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<Long> findIdsRootProcessInstanceGroupTasks(List<Long> ids, String usuariBo) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findIdsRootProcessInstanceGroupTasks ids", "jbpmDao");
		GetGroupTaskListCommand command = new GetGroupTaskListCommand(usuariBo, ids, true);
		List<Long> res = (List<Long>) commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM findIdsRootProcessInstanceGroupTasks ids", "jbpmDao");
		return res;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmTask> findGroupTasks(List<Long> ids, String usuariBo) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findGroupTasks ids", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetGroupTaskListCommand command = new GetGroupTaskListCommand(usuariBo, ids);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		mesuresTemporalsHelper.mesuraCalcular("jBPM findGroupTasks ids", "jbpmDao");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<JbpmTask> findPersonalTasks(String usuariBo) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findPersonalTasks", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetPersonalTaskListCommand command = new GetPersonalTaskListCommand(usuariBo);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		mesuresTemporalsHelper.mesuraCalcular("jBPM findPersonalTasks", "jbpmDao");
		return resultat;
	}

	public LlistatIds findListIdsPersonalTasks(String actorId,List<Long> idsExpedients) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findListIdsPersonalTasks", "jbpmDao");
		GetProcessInstancesForActiveTasksCommand command = new GetProcessInstancesForActiveTasksCommand(actorId, idsExpedients, false);
		LlistatIds resultado = (LlistatIds)commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM findListIdsPersonalTasks", "jbpmDao");
		return resultado;
	}
	
	public LlistatIds findListIdsGroupTasks(String actorId,List<Long> idsExpedients) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findListIdsGroupTasks", "jbpmDao");
		GetProcessInstancesForActiveTasksCommand command = new GetProcessInstancesForActiveTasksCommand(actorId, idsExpedients, true);
		LlistatIds resultado = (LlistatIds)commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM findListIdsGroupTasks", "jbpmDao");
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<JbpmTask> findGroupTasks(String actorId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findGroupTasks", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		
		GetGroupTaskListCommand command = new GetGroupTaskListCommand(actorId);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		mesuresTemporalsHelper.mesuraCalcular("jBPM findGroupTasks", "jbpmDao");
		return resultat;
	}

	public List<String> findStartTaskOutcomes(String jbpmId, String taskName) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findStartTaskOutcomes", "jbpmDao");
		List<String> resultat = new ArrayList<String>();
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		org.jbpm.taskmgmt.def.Task task = processDefinition.getTaskMgmtDefinition().getTask(taskName);
		if (task != null) {
			for (Transition transition: (List<Transition>)task.getStartState().getLeavingTransitions())
				resultat.add(transition.getName());
		}
		mesuresTemporalsHelper.mesuraCalcular("jBPM findStartTaskOutcomes", "jbpmDao");
		return resultat;
	}
	public List<String> findTaskInstanceOutcomes(String taskInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findTaskInstanceOutcomes", "jbpmDao");
		List<String> resultat = new ArrayList<String>();
		final long id = Long.parseLong(taskInstanceId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		List<Transition> outcomes = (List<Transition>)taskInstance.getTask().getTaskNode().getLeavingTransitions();
		if (outcomes != null) {
			for (Transition transition: outcomes)
				resultat.add(transition.getName());
		}
		mesuresTemporalsHelper.mesuraCalcular("jBPM findTaskInstanceOutcomes", "jbpmDao");
		return resultat;
	}
	
	public void takeTaskInstance(String taskId, String actorId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM takeTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		TakeTaskInstanceCommand command = new TakeTaskInstanceCommand(id, actorId);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM takeTaskInstance", "jbpmDao");
	}
	
	public void releaseTaskInstance(String taskId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM releaseTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		ReleaseTaskInstanceCommand command = new ReleaseTaskInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM releaseTaskInstance", "jbpmDao");
	}
	
	public JbpmTask cloneTaskInstance(String taskId, String actorId, Map<String, Object> variables) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM cloneTaskInstance", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		CloneTaskInstanceCommand command = new CloneTaskInstanceCommand(
				id,
				actorId,
				false);
		command.setVariables(variables);
		resposta = new JbpmTask((TaskInstance)commandService.execute(command));
		mesuresTemporalsHelper.mesuraCalcular("jBPM cloneTaskInstance", "jbpmDao");
		return resposta;
	}
	
	public JbpmTask startTaskInstance(String taskId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM startTaskInstance", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		StartTaskInstanceCommand command = new StartTaskInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		resposta = new JbpmTask((TaskInstance)commandService.execute(autoSaveCommand));
		mesuresTemporalsHelper.mesuraCalcular("jBPM startTaskInstance", "jbpmDao");
		return resposta;
	}
	
	public JbpmTask cancelTaskInstance(String taskId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM cancelTaskInstance", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		CancelTaskInstanceCommand command = new CancelTaskInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		resposta = new JbpmTask((TaskInstance)commandService.execute(autoSaveCommand));
		mesuresTemporalsHelper.mesuraCalcular("jBPM cancelTaskInstance", "jbpmDao");
		return resposta;
	}
	
	public JbpmTask suspendTaskInstance(String taskId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM suspendTaskInstance", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		SuspendTaskInstanceCommand command = new SuspendTaskInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		resposta = new JbpmTask((TaskInstance)commandService.execute(autoSaveCommand));
		mesuresTemporalsHelper.mesuraCalcular("jBPM suspendTaskInstance", "jbpmDao");
		return resposta;
	}
	
	public JbpmTask resumeTaskInstance(String taskId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM resumeTaskInstance", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		ResumeTaskInstanceCommand command = new ResumeTaskInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		resposta = new JbpmTask((TaskInstance)commandService.execute(autoSaveCommand));
		mesuresTemporalsHelper.mesuraCalcular("jBPM resumeTaskInstance", "jbpmDao");
		return resposta;
	}
	
	public JbpmTask reassignTaskInstance(String taskId, String expression) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM reassignTaskInstance", "jbpmDao");
		JbpmTask resposta = reassignTaskInstance(taskId, expression, null);
		mesuresTemporalsHelper.mesuraCalcular("jBPM reassignTaskInstance", "jbpmDao");
		return resposta;
	}
	
	public JbpmTask reassignTaskInstance(String taskId, String expression, Long entornId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM reassignTaskInstance entorn", "jbpmDao");
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
		mesuresTemporalsHelper.mesuraCalcular("jBPM reassignTaskInstance entorn", "jbpmDao");
		return resposta;
	}
	
	public void setTaskInstanceActorId(String taskInstanceId, String actorId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM setTaskInstanceActorId", "jbpmDao");
		final long id = Long.parseLong(taskInstanceId);
		ReassignTaskInstanceCommand command = new ReassignTaskInstanceCommand(id);
		command.setActorId(actorId);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM setTaskInstanceActorId", "jbpmDao");
	}
	
	public void setTaskInstancePooledActors(String taskInstanceId, String[] pooledActors) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM setTaskInstancePooledActors", "jbpmDao");
		final long id = Long.parseLong(taskInstanceId);
		ReassignTaskInstanceCommand command = new ReassignTaskInstanceCommand(id);
		command.setPooledActors(pooledActors);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM setTaskInstancePooledActors", "jbpmDao");
	}
	
	public void setTaskInstanceVariable(String taskId, String codi, Object valor) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM setTaskInstanceVariable", "jbpmDao");
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put(codi, valor);
		setTaskInstanceVariables(taskId, vars, false);
		mesuresTemporalsHelper.mesuraCalcular("jBPM setTaskInstanceVariable", "jbpmDao");
	}
	
	public void setTaskInstanceVariables(
			String taskId,
			Map<String, Object> variables,
			boolean deleteFirst) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM setTaskInstanceVariables", "jbpmDao");
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
		mesuresTemporalsHelper.mesuraCalcular("jBPM setTaskInstanceVariables", "jbpmDao");
	}
	
	public Object getTaskInstanceVariable(String taskId, String varName) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getTaskInstanceVariable", "jbpmDao");
		Object resultat = null;
		final long id = Long.parseLong(taskId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		resultat = taskInstance.getVariableLocally(varName);
		mesuresTemporalsHelper.mesuraCalcular("jBPM getTaskInstanceVariable", "jbpmDao");
		return resultat;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getTaskInstanceVariables(String taskId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getTaskInstanceVariables", "jbpmDao");
		Map<String, Object> resultat = null;
		final long id = Long.parseLong(taskId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		resultat = (Map<String, Object>)taskInstance.getVariablesLocally();
		mesuresTemporalsHelper.mesuraCalcular("jBPM getTaskInstanceVariables", "jbpmDao");
		return resultat;
	}
	
	public void deleteTaskInstanceVariable(String taskId, String varName) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM deleteTaskInstanceVariable", "jbpmDao");
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
		mesuresTemporalsHelper.mesuraCalcular("jBPM deleteTaskInstanceVariable", "jbpmDao");
	}
	
	public void endTaskInstance(String taskId, String outcome) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM endTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		TaskInstanceEndCommand command = new TaskInstanceEndCommand(id, outcome);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM endTaskInstance", "jbpmDao");
	}
	
	public void describeTaskInstance(String taskId, String description) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM describeTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		DescribeTaskInstanceCommand command = new DescribeTaskInstanceCommand(id, description);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM describeTaskInstance", "jbpmDao");
	}
	
	public List<JbpmTask> findTaskInstancesForProcessInstance(String processInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findTaskInstancesForProcessInstance", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		for (TaskInstance ti : pi.getTaskMgmtInstance().getTaskInstances())
			resultat.add(new JbpmTask(ti));
		mesuresTemporalsHelper.mesuraCalcular("jBPM findTaskInstancesForProcessInstance", "jbpmDao");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getProcessInstanceVariables(String processInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getProcessInstanceVariables", "jbpmDao");
		Map<String, Object> resultat = null;
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		resultat = pi.getContextInstance().getVariables();
		mesuresTemporalsHelper.mesuraCalcular("jBPM getProcessInstanceVariables", "jbpmDao");
		return resultat;
	}
	
	public Object getProcessInstanceVariable(String processInstanceId, String varName) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getProcessInstanceVariable", "jbpmDao");
		Object resultat = null;
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		resultat = pi.getContextInstance().getVariable(varName);
		mesuresTemporalsHelper.mesuraCalcular("jBPM getProcessInstanceVariable", "jbpmDao");
		return resultat;
	}
	
	public void setProcessInstanceVariable(
			String processInstanceId,
			String varName,
			Object value) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM setProcessInstanceVariable", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put(varName, value);
		SaveProcessInstanceVariablesCommand command = new SaveProcessInstanceVariablesCommand(id, vars);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM setProcessInstanceVariable", "jbpmDao");
	}
	
	public void deleteProcessInstanceVariable(String processInstanceId, String varName) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM deleteProcessInstanceVariable", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		DeleteProcessInstanceVariablesCommand command = new DeleteProcessInstanceVariablesCommand(id, new String[] {varName});
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM deleteProcessInstanceVariable", "jbpmDao");
	}

	public JbpmToken getTokenById(String tokenId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getTokenById", "jbpmDao");
		final long id = Long.parseLong(tokenId);
		GetTokenByIdCommand command = new GetTokenByIdCommand(id);
		JbpmToken resultat = new JbpmToken((Token)commandService.execute(command));
		mesuresTemporalsHelper.mesuraCalcular("jBPM getTokenById", "jbpmDao");
		return resultat;
	}
	
	public Map<String, JbpmToken> getActiveTokens(String processInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getActiveTokens PI", "jbpmDao");
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
		mesuresTemporalsHelper.mesuraCalcular("jBPM getActiveTokens PI", "jbpmDao");
		return resposta;
	}
	
	@SuppressWarnings("unchecked")
	private  Map<String, Token> getActiveTokens(Token token){
		mesuresTemporalsHelper.mesuraIniciar("jBPM getActiveTokens", "jbpmDao");
		Map<String, Token> activeTokens = new HashMap<String, Token>();
		if (token.hasActiveChildren()) {
			activeTokens = token.getActiveChildren();
			for (Token t: activeTokens.values()){
				activeTokens.putAll(getActiveTokens(t));
			}
		}
		mesuresTemporalsHelper.mesuraCalcular("jBPM getActiveTokens", "jbpmDao");
		return activeTokens;
	}
	
	public Map<String, JbpmToken> getAllTokens(String processInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getAllTokens", "jbpmDao");
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
		mesuresTemporalsHelper.mesuraCalcular("jBPM getAllTokens", "jbpmDao");
		return resposta;
	}

	@SuppressWarnings("unchecked")
	public List<String> findArrivingNodeNames(String tokenId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findArrivingNodeNames", "jbpmDao");
		final long id = Long.parseLong(tokenId);
		FindArrivingNodeNamesCommand command = new FindArrivingNodeNamesCommand(id);
		List<String> resultat = (List<String>)commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM findArrivingNodeNames", "jbpmDao");
		return resultat;
	}

	public void tokenRedirect(
			long tokenId,
			String nodeName,
			boolean cancelTasks,
			boolean enterNodeIfTask,
			boolean executeNode) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM tokenRedirect", "jbpmDao");
		TokenRedirectCommand command = new TokenRedirectCommand(tokenId, nodeName);
		command.setCancelTasks(cancelTasks);
		command.setEnterNodeIfTask(enterNodeIfTask);
		command.setExecuteNode(executeNode);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				tokenId,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM tokenRedirect", "jbpmDao");
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> evaluateScript(
			String processInstanceId,
			String script,
			Set<String> outputNames) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM evaluateScript", "jbpmDao");
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
		mesuresTemporalsHelper.mesuraCalcular("jBPM evaluateScript", "jbpmDao");
		return resultat;
	}

	public Object evaluateExpression(
			String taskInstanceInstanceId,
			String processInstanceId,
			String expression,
			Map<String, Object> valors) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM evaluateExpression", "jbpmDao");
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
		mesuresTemporalsHelper.mesuraCalcular("jBPM evaluateExpression", "jbpmDao");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<String> listActions(String jbpmId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM listActions", "jbpmDao");
		final long id = Long.parseLong(jbpmId);
		List<String> llista = (List<String>)commandService.execute(
				new ListActionsCommand(id));
		mesuresTemporalsHelper.mesuraCalcular("jBPM listActions", "jbpmDao");
		return llista;
	}
	
	public void executeActionInstanciaProces(
			String processInstanceId,
			String actionName) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM executeActionInstanciaProces", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		ExecuteActionCommand command = new ExecuteActionCommand(
				id,
				actionName);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM executeActionInstanciaProces", "jbpmDao");
	}
	
	public void executeActionInstanciaTasca(
			String taskInstanceId,
			String actionName) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM executeActionInstanciaTasca", "jbpmDao");
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
		mesuresTemporalsHelper.mesuraCalcular("jBPM executeActionInstanciaTasca", "jbpmDao");
	}
	
	public void retrocedirAccio(
			String processInstanceId,
			String actionName,
			List<String> params) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM retrocedirAccio", "jbpmDao");
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
		mesuresTemporalsHelper.mesuraCalcular("jBPM retrocedirAccio", "jbpmDao");
	}

	public void changeProcessInstanceVersion(
			String processInstanceId,
			int newVersion) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM changeProcessInstanceVersion", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		ChangeProcessInstanceVersionCommand command = new ChangeProcessInstanceVersionCommand(
				id,
				newVersion);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM changeProcessInstanceVersion", "jbpmDao");
	}

	public void signalToken(
			long tokenId,
			String transitionName) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM signalToken", "jbpmDao");
		SignalCommand command = new SignalCommand(tokenId, transitionName);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				tokenId,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM signalToken", "jbpmDao");
	}

	@SuppressWarnings("unchecked")
	public List<Timer> findTimersWithProcessInstanceId(
			String processInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findTimersWithProcessInstanceId", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		FindProcessInstanceTimersCommand command = new FindProcessInstanceTimersCommand(id);
		List<Timer> llista = (List<Timer>)commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM findTimersWithProcessInstanceId", "jbpmDao");
		return llista;
	}

	public void suspendTimer(
			long timerId,
			Date dueDate) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM suspendTimer", "jbpmDao");
		SuspendProcessInstanceTimerCommand command = new SuspendProcessInstanceTimerCommand(timerId);
		command.setDueDate(dueDate);
		commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM suspendTimer", "jbpmDao");
	}
	public void resumeTimer(
			long timerId,
			Date dueDate) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM resumeTimer", "jbpmDao");
		ResumeProcessInstanceTimerCommand command = new ResumeProcessInstanceTimerCommand(timerId);
		command.setDueDate(dueDate);
		commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM resumeTimer", "jbpmDao");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Token, List<ProcessLog>> getProcessInstanceLogs(String processInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getProcessInstanceLogs", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		FindProcessInstanceLogsCommand command = new FindProcessInstanceLogsCommand(id);
		Map resultat = (Map)commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM getProcessInstanceLogs", "jbpmDao");
		return resultat;
	}

	public Long addProcessInstanceMessageLog(String processInstanceId, String message) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM addProcessInstanceMessageLog", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		AddProcessInstanceMessageLogCommand command = new AddProcessInstanceMessageLogCommand(id, message);
		long resultat = ((Long)commandService.execute(command)).longValue();
		mesuresTemporalsHelper.mesuraCalcular("jBPM addProcessInstanceMessageLog", "jbpmDao");
		return resultat;
	}
	public Long addTaskInstanceMessageLog(String taskInstanceId, String message) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM addTaskInstanceMessageLog", "jbpmDao");
		final long id = Long.parseLong(taskInstanceId);
		AddTaskInstanceMessageLogCommand command = new AddTaskInstanceMessageLogCommand(id, message);
		long resultat = ((Long)commandService.execute(command)).longValue();
		mesuresTemporalsHelper.mesuraCalcular("jBPM addTaskInstanceMessageLog", "jbpmDao");
		return resultat;
	}

	public Long getVariableIdFromVariableLog(long variableLogId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getVariableIdFromVariableLog", "jbpmDao");
		GetVariableIdFromVariableLogCommand command = new GetVariableIdFromVariableLogCommand(variableLogId);
		Long resultat = (Long)commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM getVariableIdFromVariableLog", "jbpmDao");
		return resultat;
	}
	public Long getTaskIdFromVariableLog(long variableLogId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getTaskIdFromVariableLog", "jbpmDao");
		GetTaskIdFromVariableLogCommand command = new GetTaskIdFromVariableLogCommand(variableLogId);
		Long resultat = (Long)commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM getTaskIdFromVariableLog", "jbpmDao");
		return resultat;
	}

	public void cancelProcessInstance(long id) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM cancelProcessInstance", "jbpmDao");
		CancelProcessInstanceCommand command = new CancelProcessInstanceCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM cancelProcessInstance", "jbpmDao");
	}
	public void revertProcessInstanceEnd(long id) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM revertProcessInstanceEnd", "jbpmDao");
		RevertProcessInstanceEndCommand command = new RevertProcessInstanceEndCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM revertProcessInstanceEnd", "jbpmDao");
	}

	public void cancelToken(long id) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM cancelToken", "jbpmDao");
		CancelTokenCommand command = new CancelTokenCommand(id);
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		commandService.execute(autoSaveCommand);
		mesuresTemporalsHelper.mesuraCalcular("jBPM cancelToken", "jbpmDao");
	}
	
	public void revertTokenEnd(long id) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM revertTokenEnd", "jbpmDao");
		JbpmToken jtoken = getTokenById(String.valueOf(id));
		RevertTokenEndCommand command = new RevertTokenEndCommand(jtoken.getToken().getId());
		AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		commandService.execute(autoSaveCommand);
		this.sessionFactory.getCurrentSession().refresh(jtoken.getToken());
		jtoken.getToken().setAbleToReactivateParent(true);
		mesuresTemporalsHelper.mesuraCalcular("jBPM revertTokenEnd", "jbpmDao");
	}

	public JbpmTask findEquivalentTaskInstance(long tokenId, long taskInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM findEquivalentTaskInstance", "jbpmDao");
		GetTaskInstanceCommand commandGetTask = new GetTaskInstanceCommand(taskInstanceId);
		TaskInstance ti = (TaskInstance)commandService.execute(commandGetTask);
		FindTaskInstanceForTokenAndTaskCommand command = new FindTaskInstanceForTokenAndTaskCommand(tokenId, ti.getTask().getName());
		JbpmTask resultat = new JbpmTask((TaskInstance)commandService.execute(command));
		mesuresTemporalsHelper.mesuraCalcular("jBPM findEquivalentTaskInstance", "jbpmDao");
		return resultat;
	}

	public boolean isProcessStateNodeJoinOrFork(long processInstanceId, String nodeName) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM isProcessStateNodeJoinOrFork", "jbpmDao");
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(processInstanceId);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		Node node = pi.getProcessDefinition().getNode(nodeName);
		String nodeClassName = node.toString();
		NodeType nodeType = node.getNodeType();
		mesuresTemporalsHelper.mesuraCalcular("jBPM isProcessStateNodeJoinOrFork", "jbpmDao");
		return (nodeClassName.startsWith("ProcessState") || nodeType == NodeType.Fork || nodeType == NodeType.Join);
	}

	public boolean isJoinNode(long processInstanceId, String nodeName) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM isJoinNode", "jbpmDao");
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(processInstanceId);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		NodeType nodeType = pi.getProcessDefinition().getNode(nodeName).getNodeType();
		mesuresTemporalsHelper.mesuraCalcular("jBPM isJoinNode", "jbpmDao");
		return nodeType == NodeType.Join;
	}
	
	public ProcessLog getProcessLogById(Long id){
		mesuresTemporalsHelper.mesuraIniciar("jBPM getProcessLogById", "jbpmDao");
		GetProcessLogByIdCommand command = new GetProcessLogByIdCommand(id.longValue());
		ProcessLog log = (ProcessLog)commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM getProcessLogById", "jbpmDao");
		return log;
	}

	public Node getNodeByName(long processInstanceId, String nodeName) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM getNodeByName", "jbpmDao");
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(processInstanceId);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		Node node = pi.getProcessDefinition().getNode(nodeName);
		mesuresTemporalsHelper.mesuraCalcular("jBPM getNodeByName", "jbpmDao");
		return node;
	}
	
	public boolean hasStartBetweenLogs(long begin, long end, long taskInstanceId) {
		mesuresTemporalsHelper.mesuraIniciar("jBPM hasStartBetweenLogs", "jbpmDao");
		HasStartBetweenLogsCommand command = new HasStartBetweenLogsCommand(begin, end, taskInstanceId);
		Boolean hasStart = (Boolean)commandService.execute(command);
		mesuresTemporalsHelper.mesuraCalcular("jBPM hasStartBetweenLogs", "jbpmDao");
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
