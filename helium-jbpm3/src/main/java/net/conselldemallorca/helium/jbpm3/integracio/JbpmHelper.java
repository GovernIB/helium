/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jbpm.JbpmException;
import org.jbpm.command.CancelTokenCommand;
import org.jbpm.command.ChangeProcessInstanceVersionCommand;
import org.jbpm.command.Command;
import org.jbpm.command.CommandService;
import org.jbpm.command.DeleteProcessDefinitionCommand;
import org.jbpm.command.DeployProcessCommand;
import org.jbpm.command.GetProcessInstanceCommand;
import org.jbpm.command.GetProcessInstancesCommand;
import org.jbpm.command.GetTaskInstanceCommand;
import org.jbpm.command.SignalCommand;
import org.jbpm.command.TaskInstanceEndCommand;
import org.jbpm.file.def.FileDefinition;
import org.jbpm.graph.def.DelegationException;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.ProcessInstanceExpedient;
import org.jbpm.graph.exe.Token;
import org.jbpm.job.Timer;
import org.jbpm.logging.log.ProcessLog;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.jbpm3.command.AddProcessInstanceMessageLogCommand;
import net.conselldemallorca.helium.jbpm3.command.AddTaskInstanceMessageLogCommand;
import net.conselldemallorca.helium.jbpm3.command.AddToAutoSaveCommand;
import net.conselldemallorca.helium.jbpm3.command.CancelProcessInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.CancelTaskInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.CloneTaskInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.DeleteProcessInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.DeleteProcessInstanceLogsCommand;
import net.conselldemallorca.helium.jbpm3.command.DeleteProcessInstanceVariablesCommand;
import net.conselldemallorca.helium.jbpm3.command.DeleteTaskInstanceVariablesCommand;
import net.conselldemallorca.helium.jbpm3.command.DescribeProcessInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.DescribeTaskInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.EvaluateExpressionCommand;
import net.conselldemallorca.helium.jbpm3.command.EvaluateScriptCommand;
import net.conselldemallorca.helium.jbpm3.command.ExecuteActionCommand;
import net.conselldemallorca.helium.jbpm3.command.FindArrivingNodeNamesCommand;
import net.conselldemallorca.helium.jbpm3.command.FindExpedientIdsFiltreCommand;
import net.conselldemallorca.helium.jbpm3.command.FindJbpmTasksFiltreCommand;
import net.conselldemallorca.helium.jbpm3.command.FindProcessInstanceLogsCommand;
import net.conselldemallorca.helium.jbpm3.command.FindProcessInstanceTimersCommand;
import net.conselldemallorca.helium.jbpm3.command.FindTaskInstanceForTokenAndTaskCommand;
import net.conselldemallorca.helium.jbpm3.command.FindTaskInstanceIdForTokenIdCommand;
import net.conselldemallorca.helium.jbpm3.command.GetExpedientsAfectatsListCommand;
import net.conselldemallorca.helium.jbpm3.command.GetGroupTaskListCommand;
import net.conselldemallorca.helium.jbpm3.command.GetPersonalTaskListCommand;
import net.conselldemallorca.helium.jbpm3.command.GetProcesDefinitionEntornNotUsedListCommand;
import net.conselldemallorca.helium.jbpm3.command.GetProcesDefinitionNotUsedListCommand;
import net.conselldemallorca.helium.jbpm3.command.GetProcessDefinitionByIdCommand;
import net.conselldemallorca.helium.jbpm3.command.GetProcessInstancesEntornCommand;
import net.conselldemallorca.helium.jbpm3.command.GetProcessInstancesTreeCommand;
import net.conselldemallorca.helium.jbpm3.command.GetProcessLogByIdCommand;
import net.conselldemallorca.helium.jbpm3.command.GetRootProcessInstancesForActiveTasksCommand;
import net.conselldemallorca.helium.jbpm3.command.GetRootProcessInstancesForExpedientsWithActiveTasksCommand;
import net.conselldemallorca.helium.jbpm3.command.GetSubProcessDefinitionsCommand;
import net.conselldemallorca.helium.jbpm3.command.GetTaskIdFromVariableLogCommand;
import net.conselldemallorca.helium.jbpm3.command.GetTaskListCommand;
import net.conselldemallorca.helium.jbpm3.command.GetTasquesSegonPlaPendentsIdsCommand;
import net.conselldemallorca.helium.jbpm3.command.GetTokenByIdCommand;
import net.conselldemallorca.helium.jbpm3.command.GetVariableIdFromVariableLogCommand;
import net.conselldemallorca.helium.jbpm3.command.GuardarErrorFinalitzacioCommand;
import net.conselldemallorca.helium.jbpm3.command.HasStartBetweenLogsCommand;
import net.conselldemallorca.helium.jbpm3.command.ListActionsCommand;
import net.conselldemallorca.helium.jbpm3.command.MarcarFinalitzarCommand;
import net.conselldemallorca.helium.jbpm3.command.MarcarIniciFinalitzacioSegonPlaCommand;
import net.conselldemallorca.helium.jbpm3.command.ProcessInstanceEndCommand;
import net.conselldemallorca.helium.jbpm3.command.ReassignTaskInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.ReleaseTaskInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.ResumeProcessInstanceTimerCommand;
import net.conselldemallorca.helium.jbpm3.command.ResumeProcessInstancesCommand;
import net.conselldemallorca.helium.jbpm3.command.ResumeTaskInstanceCommand;
import net.conselldemallorca.helium.jbpm3.command.RetryJobCommand;
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
import net.conselldemallorca.helium.jbpm3.command.TokenActivarCommand;
import net.conselldemallorca.helium.jbpm3.command.TokenRedirectCommand;
import net.conselldemallorca.helium.jbpm3.command.UpdateHandlersCommand;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDto;

/**
 * Dao per a l'accés a la funcionalitat de jBPM3
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class JbpmHelper {

	private CommandService commandService;

	public enum MostrarTasquesDto {
		MOSTRAR_TASQUES_TOTS,
		MOSTRAR_TASQUES_NOMES_GROUPS,
		MOSTRAR_TASQUES_NOMES_PERSONALS
	}

	public ResultatConsultaPaginadaJbpm<JbpmTask> tascaFindByFiltrePaginat(
			Long entornId,
			String actorId,
			String taskName,
			String titol,
			Long expedientId,
			String expedientTitol,
			String expedientNumero,
			Long expedientTipusId,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			boolean mostrarAssignadesUsuari,
			boolean mostrarAssignadesGrup,
			boolean nomesPendents,
			PaginacioParamsDto paginacioParams) {
		return tascaFindByFiltrePaginat(
				entornId,
				actorId,
				taskName,
				titol,
				expedientId,
				expedientTitol,
				expedientNumero,
				expedientTipusId,
				dataCreacioInici,
				dataCreacioFi,
				prioritat,
				dataLimitInici,
				dataLimitFi,
				mostrarAssignadesUsuari,
				mostrarAssignadesGrup,
				nomesPendents,
				paginacioParams,
				false);
	}
	@SuppressWarnings("unchecked")
	public ResultatConsultaPaginadaJbpm<JbpmTask> tascaFindByFiltrePaginat(
			Long entornId,
			String actorId,
			String taskName,
			String titol,
			Long expedientId,
			String expedientTitol,
			String expedientNumero,
			Long expedientTipusId,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			boolean mostrarAssignadesUsuari,
			boolean mostrarAssignadesGrup,
			boolean nomesPendents,
			PaginacioParamsDto paginacioParams,
			boolean nomesCount) {
		String ordre = null;
		boolean asc = true;
		if (paginacioParams.getOrdres() != null && !paginacioParams.getOrdres().isEmpty()) {
			OrdreDto ordreDto = paginacioParams.getOrdres().get(0);
			if ("expedientIdentificador".equals(ordreDto.getCamp())) {
				ordre = "expedientTitol";
			} else if ("expedientTipusNom".equals(ordreDto.getCamp())) {
				ordre = "expedientTipusNom";
			} else if ("createTime".equals(ordreDto.getCamp())) {
				ordre = "dataCreacio";
			} else if ("dueDate".equals(ordreDto.getCamp())) {
				ordre = "dataLimit";
			} else if ("prioritat".equals(ordreDto.getCamp())) {
				ordre = "prioritat";
			}
			asc = !OrdreDireccioDto.DESCENDENT.equals(paginacioParams.getOrdres().get(0).getDireccio());
		}
		FindJbpmTasksFiltreCommand command = new FindJbpmTasksFiltreCommand(
				entornId,
				actorId,
				taskName,
				titol,
				expedientId,
				expedientTitol,
				expedientNumero,
				expedientTipusId,
				dataCreacioInici,
				dataCreacioFi,
				prioritat,
				dataLimitInici,
				dataLimitFi,
				mostrarAssignadesUsuari,
				mostrarAssignadesGrup,
				nomesPendents,
				paginacioParams.getPaginaNum() * paginacioParams.getPaginaTamany(),
				paginacioParams.getPaginaTamany(),
				ordre,
				asc,
				nomesCount);
		return (ResultatConsultaPaginadaJbpm<JbpmTask>)commandService.execute(command);
	}

	@SuppressWarnings("unchecked")
	public ResultatConsultaPaginadaJbpm<Long> expedientFindByFiltre(
			Long entornId,
			String actorId,
			Collection<Long> tipusIdPermesos,
			String titol,
			String numero,
			Long tipusId,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesIniciats,
			boolean nomesFinalitzats,
			boolean mostrarAnulats,
			boolean mostrarNomesAnulats,
			boolean nomesAlertes,
			boolean nomesErrors,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesTasquesMeves,
			PaginacioParamsDto paginacioParams,
			boolean nomesCount) {
		String ordre = null;
		boolean asc = true;
		if (paginacioParams.getOrdres() != null && !paginacioParams.getOrdres().isEmpty()) {
			OrdreDto ordreDto = paginacioParams.getOrdres().get(0);
			if ("identificador".equals(ordreDto.getCamp())) {
				ordre = "identificador";
			} else if ("tipus.nom".equals(ordreDto.getCamp())) {
				ordre = "tipus";
			} else if ("dataInici".equals(ordreDto.getCamp())) {
				ordre = "dataInici";
			} else if ("dataFi".equals(ordreDto.getCamp())) {
				ordre = "dataFi";
			} else if ("estat.nom".equals(ordreDto.getCamp())) {
				ordre = "estat";
			}
			asc = !OrdreDireccioDto.DESCENDENT.equals(paginacioParams.getOrdres().get(0).getDireccio());
		}
		FindExpedientIdsFiltreCommand command = new FindExpedientIdsFiltreCommand(
				entornId,
				actorId,
				tipusIdPermesos,
				titol,
				numero,
				tipusId,
				dataCreacioInici,
				dataCreacioFi,
				estatId,
				geoPosX,
				geoPosY,
				geoReferencia,
				nomesIniciats,
				nomesFinalitzats,
				mostrarAnulats,
				mostrarNomesAnulats,
				nomesAlertes,
				nomesErrors,
				nomesTasquesPersonals,
				nomesTasquesGrup,
				nomesTasquesMeves,
				paginacioParams.getPaginaNum() * paginacioParams.getPaginaTamany(),
				paginacioParams.getPaginaTamany(),
				ordre,
				asc,
				nomesCount);
		return (ResultatConsultaPaginadaJbpm<Long>)commandService.execute(command);
	}

	public ProcessInstanceExpedient expedientFindByProcessInstanceId(
			String processInstanceId) {
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(
				Long.parseLong(processInstanceId));
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		return processInstance.getExpedient();
	}



	public JbpmProcessDefinition desplegar(
			String nomArxiu,
			byte[] contingut) {
		//adminService.mesuraIniciar("jBPM desplegar", "jbpmDao");
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
				//adminService.mesuraCalcular("jBPM desplegar", "jbpmDao");
				throw new RuntimeException("No s'ha pogut deplegar l'arxiu", ex);
			}
		} else if (nomArxiu.endsWith("ar")) {
			command = new DeployProcessCommand(contingut);
		} else {
			//adminService.mesuraCalcular("jBPM desplegar", "jbpmDao");
			throw new RuntimeException("Arxiu amb extensió no suportada " + nomArxiu + ". Només es suporten les extensions .xml i .*ar");
		}
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		resposta = new JbpmProcessDefinition(processDefinition);
		//adminService.mesuraCalcular("jBPM desplegar", "jbpmDao");
		return resposta;
	}

	
	public JbpmProcessDefinition getProcessDefinition(String jbpmId) {
		//adminService.mesuraIniciar("jBPM getProcessDefinition", "jbpmDao");
		JbpmProcessDefinition resposta = null;
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		if (processDefinition != null)
			resposta = new JbpmProcessDefinition((ProcessDefinition)commandService.execute(command));
		//adminService.mesuraCalcular("jBPM getProcessDefinition", "jbpmDao");
		return resposta;
	}

	
	@SuppressWarnings("unchecked")
	public List<JbpmProcessDefinition> getSubProcessDefinitions(String jbpmId) {
		//adminService.mesuraIniciar("jBPM getSubProcessDefinitions", "jbpmDao");
		List<JbpmProcessDefinition> resposta = new ArrayList<JbpmProcessDefinition>();
		final long pdid = Long.parseLong(jbpmId);
		GetSubProcessDefinitionsCommand command = new GetSubProcessDefinitionsCommand(pdid);
		for (ProcessDefinition pd: (List<ProcessDefinition>)commandService.execute(command)) {
			resposta.add(new JbpmProcessDefinition(pd));
		}
		//adminService.mesuraCalcular("jBPM getSubProcessDefinitions", "jbpmDao");
		return resposta;
	}

	
	public JbpmProcessInstance getProcessInstance(String processInstanceId) {
		//adminService.mesuraIniciar("jBPM getProcessInstance", "jbpmDao");
		JbpmProcessInstance resposta = null;
		final long piid = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(piid);
		resposta = new JbpmProcessInstance((ProcessInstance)commandService.execute(command));
		//adminService.mesuraCalcular("jBPM getProcessInstance", "jbpmDao");
		return resposta;
	}

	
	public String getStartTaskName(String jbpmId) {
		//adminService.mesuraIniciar("jBPM getStartTaskName", "jbpmDao");
		String resposta = null;
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		if (processDefinition != null) {
			org.jbpm.taskmgmt.def.Task startTask = processDefinition.getTaskMgmtDefinition().getStartTask();
			if (startTask != null)
				resposta = startTask.getName();
		}
		//adminService.mesuraCalcular("jBPM getStartTaskName", "jbpmDao");
		return resposta;
	}

	
	@SuppressWarnings("unchecked")
	public List<String> getTaskNamesFromDeployedProcessDefinition(JbpmProcessDefinition dpd) {
		//adminService.mesuraIniciar("jBPM getTaskNamesFromDeployedProcessDefinition", "jbpmDao");
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
		//adminService.mesuraCalcular("jBPM getTaskNamesFromDeployedProcessDefinition", "jbpmDao");
		return taskNames;
	}

	
	public void esborrarDesplegament(String jbpmId) {
		//adminService.mesuraIniciar("jBPM esborrarDesplegament", "jbpmDao");
		DeleteProcessDefinitionCommand command = new DeleteProcessDefinitionCommand();
		command.setId(Long.parseLong(jbpmId));
		commandService.execute(command);
		//adminService.mesuraCalcular("jBPM esborrarDesplegament", "jbpmDao");
	}

	
	@SuppressWarnings("unchecked")
	public Set<String> getResourceNames(String jbpmId) {
		//adminService.mesuraIniciar("jBPM getResourceNames", "jbpmDao");
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
		//adminService.mesuraCalcular("jBPM getResourceNames", "jbpmDao");
		return resources;
	}

	
	public byte[] getResourceBytes(String jbpmId, String resourceName) {
		//adminService.mesuraIniciar("jBPM getResourceBytes", "jbpmDao");
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		byte[] bytes = processDefinition.getFileDefinition().getBytes(resourceName);
		//adminService.mesuraCalcular("jBPM getResourceBytes", "jbpmDao");
		return bytes;
	}

	
	public JbpmProcessInstance startProcessInstanceById(
			String actorId,
			String processDefinitionId,
			Map<String, Object> variables) {
//			boolean ambRetroaccio) {
		//adminService.mesuraIniciar("jBPM startProcessInstanceById", "jbpmDao");
		StartProcessInstanceCommand command = new StartProcessInstanceCommand(); //ambRetroaccio);
		command.setProcessDefinitionId(Long.parseLong(processDefinitionId));
		command.setActorId(actorId);
		if (variables != null)
			command.setVariables(variables);
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		JbpmProcessInstance resultat = new JbpmProcessInstance(processInstance);
		//adminService.mesuraCalcular("jBPM startProcessInstanceById", "jbpmDao");
		return resultat;
	}
	
	
	public void signalProcessInstance(
			String processInstanceId,
			String transitionName) {
		//adminService.mesuraIniciar("jBPM signalProcessInstance", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		SignalProcessInstanceCommand command = new SignalProcessInstanceCommand(id);
		if (transitionName != null)
			command.setStartTransitionName(transitionName);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM signalProcessInstance", "jbpmDao");
	}
	
	
	public JbpmProcessInstance getRootProcessInstance(
			String processInstanceId) {
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(
				Long.parseLong(processInstanceId));
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		if (processInstance != null) {
			if (processInstance.getExpedient() != null) {
				if (!processInstance.getExpedient().getProcessInstanceId().equals(processInstanceId)) {
					command.setProcessInstanceId(
							Long.parseLong(
									processInstance.getExpedient().getProcessInstanceId()));
					processInstance = (ProcessInstance)commandService.execute(command);
				}
				return new JbpmProcessInstance(processInstance);
			} else {
				// L'expedient del processInstance pot ser null si estam
				// iniciant l'expedient
				while (processInstance.getSuperProcessToken() != null) {
					final long id = processInstance.getSuperProcessToken().getProcessInstance().getId();
					command.setProcessInstanceId(id);
					processInstance = (ProcessInstance)commandService.execute(command);
				}
				return new JbpmProcessInstance(processInstance);
			}
		} else {
			return null;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<JbpmProcessInstance> getProcessInstanceTree(
			String rootProcessInstanceId) {
		//adminService.mesuraIniciar("jBPM getProcessInstanceTree", "jbpmDao");
		List<JbpmProcessInstance> resposta = new ArrayList<JbpmProcessInstance>();
		final long id = Long.parseLong(rootProcessInstanceId);
		GetProcessInstancesTreeCommand command = new GetProcessInstancesTreeCommand(id);
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resposta.add(new JbpmProcessInstance(pd));
		}
		//adminService.mesuraCalcular("jBPM getProcessInstanceTree", "jbpmDao");
		return resposta;
	}
	
	public void deleteProcessInstance(
			String processInstanceId) {
		//adminService.mesuraIniciar("jBPM deleteProcessInstance", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		DeleteProcessInstanceCommand command = new DeleteProcessInstanceCommand(id);
		commandService.execute(command);
		//adminService.mesuraCalcular("jBPM deleteProcessInstance", "jbpmDao");
		/*AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		commandService.execute(autoSaveCommand);*/
	}

	
	public void suspendProcessInstance(
			String processInstanceId) {
		//adminService.mesuraIniciar("jBPM suspendProcessInstance", "jbpmDao");
		suspendProcessInstances(new String[] {processInstanceId});
		//adminService.mesuraCalcular("jBPM suspendProcessInstance", "jbpmDao");
	}
	
	
	public void suspendProcessInstances(
			String[] processInstanceIds) {
		//adminService.mesuraIniciar("jBPM suspendProcessInstances", "jbpmDao");
		long[] ids = new long[processInstanceIds.length];
		for (int i = 0; i < processInstanceIds.length; i++)
			ids[i] = Long.parseLong(processInstanceIds[i]);
		SuspendProcessInstancesCommand command = new SuspendProcessInstancesCommand(ids);
		executeCommandWithAutoSave(
				command,
				ids,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM suspendProcessInstances", "jbpmDao");
	}
	
	
	public void resumeProcessInstance(
			String processInstanceId) {
		//adminService.mesuraIniciar("jBPM resumeProcessInstance", "jbpmDao");
		resumeProcessInstances(new String[] {processInstanceId});
		//adminService.mesuraCalcular("jBPM resumeProcessInstance", "jbpmDao");
	}
	
	
	public void resumeProcessInstances(
			String[] processInstanceIds) {
		//adminService.mesuraIniciar("jBPM resumeProcessInstances", "jbpmDao");
		long[] ids = new long[processInstanceIds.length];
		for (int i = 0; i < processInstanceIds.length; i++)
			ids[i] = Long.parseLong(processInstanceIds[i]);
		ResumeProcessInstancesCommand command = new ResumeProcessInstancesCommand(ids);
		executeCommandWithAutoSave(
				command,
				ids,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM resumeProcessInstances", "jbpmDao");
	}
	
	
	public void describeProcessInstance(
			String processInstanceId,
			String description) {
		//adminService.mesuraIniciar("jBPM describeProcessInstance", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		DescribeProcessInstanceCommand command = new DescribeProcessInstanceCommand(id, description);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM describeProcessInstance", "jbpmDao");
	}
	
	//Marcar tasca pendent de finalitzar en segón pla
	public void marcarFinalitzar(String taskId, Date marcadaFinalitzar, String outcome, String rols) {
		final long id = Long.parseLong(taskId);
		MarcarFinalitzarCommand command = new MarcarFinalitzarCommand(id, marcadaFinalitzar, outcome, rols);
		commandService.execute(command);
	}

	//Marcar tasca marcada en segon pla com "en execució"
	public void marcarIniciFinalitzacioSegonPla(String taskId, Date iniciFinalitzacio) {
		final long id = Long.parseLong(taskId);
		MarcarIniciFinalitzacioSegonPlaCommand command = new MarcarIniciFinalitzacioSegonPlaCommand(id, iniciFinalitzacio);
		commandService.execute(command);
	}
	
	//Guardar l'error de finalitzacio a BBDD
	public void guardarErrorFinalitzacio(String taskId, String errorFinalitzacio) {
		final long id = Long.parseLong(taskId);
		String errorTractat;
		if (errorFinalitzacio.length() > 1000)
			errorTractat = errorFinalitzacio.substring(0, 1000);
		else
			errorTractat = errorFinalitzacio;
		GuardarErrorFinalitzacioCommand command = new GuardarErrorFinalitzacioCommand(id, errorTractat);
		commandService.execute(command);
	}
	
	@SuppressWarnings("unchecked")
	public List<JbpmProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId) {
		//adminService.mesuraIniciar("jBPM findProcessInstancesWithProcessDefinitionId", "jbpmDao");
		List<JbpmProcessInstance> resultat = new ArrayList<JbpmProcessInstance>();
		final long id = Long.parseLong(processDefinitionId);
		GetProcessInstancesCommand command = new GetProcessInstancesCommand();
		command.setProcessInstanceId(id); // Això està bé, el command agafa setProcessInstanceId com si fos setProcessDefinitionId
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resultat.add(new JbpmProcessInstance(pd));
		}
		//adminService.mesuraCalcular("jBPM findProcessInstancesWithProcessDefinitionId", "jbpmDao");
		return resultat;
	}

	
	@SuppressWarnings("unchecked")
	public List<JbpmProcessInstance> findProcessInstancesWithProcessDefinitionName(String processName) {
		//adminService.mesuraIniciar("jBPM findProcessInstancesWithProcessDefinitionName", "jbpmDao");
		List<JbpmProcessInstance> resultat = new ArrayList<JbpmProcessInstance>();
		GetProcessInstancesCommand command = new GetProcessInstancesCommand();
		command.setProcessDefinitionName(processName);
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resultat.add(new JbpmProcessInstance(pd));
		}
		//adminService.mesuraCalcular("jBPM findProcessInstancesWithProcessDefinitionName", "jbpmDao");
		return resultat;
	}
	
	@SuppressWarnings("unchecked")
	public List<JbpmProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(String processName, Long entornId) {
		//adminService.mesuraIniciar("jBPM findProcessInstancesWithProcessDefinitionNameAndEntorn", "jbpmDao");
		List<JbpmProcessInstance> resultat = new ArrayList<JbpmProcessInstance>();
		GetProcessInstancesEntornCommand command = new GetProcessInstancesEntornCommand();
		command.setProcessDefinitionName(processName);
		command.setEntornId(entornId);
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resultat.add(new JbpmProcessInstance(pd));
		}
		//adminService.mesuraCalcular("jBPM findProcessInstancesWithProcessDefinitionNameAndEntorn", "jbpmDao");
		return resultat;
	}

	
	public JbpmProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) {
		//adminService.mesuraIniciar("jBPM findProcessDefinitionWithProcessInstanceId", "jbpmDao");
		JbpmProcessDefinition resultat = null;
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		if (pi != null)
			resultat = new JbpmProcessDefinition(pi.getProcessDefinition());
		//adminService.mesuraCalcular("jBPM findProcessDefinitionWithProcessInstanceId", "jbpmDao");
		return resultat;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getTasquesSegonPlaPendents() {
		GetTasquesSegonPlaPendentsIdsCommand command = new GetTasquesSegonPlaPendentsIdsCommand();
		return (List<Object[]>)commandService.execute(command);
	}

	
	public JbpmTask getTaskById(String taskId) {
		//adminService.mesuraIniciar("jBPM getTaskById", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance ti = (TaskInstance)commandService.execute(command);
		if (ti != null)
			resposta = new JbpmTask(ti);
		//adminService.mesuraCalcular("jBPM getTaskById", "jbpmDao");
		return resposta;
	}

	
	public List<String> findStartTaskOutcomes(String jbpmId, String taskName) {
		//adminService.mesuraIniciar("jBPM findStartTaskOutcomes", "jbpmDao");
		List<String> resultat = new ArrayList<String>();
		final long pdid = Long.parseLong(jbpmId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		org.jbpm.taskmgmt.def.Task task = processDefinition.getTaskMgmtDefinition().getTask(taskName);
		if (task != null) {
			for (Transition transition: (List<Transition>)task.getStartState().getLeavingTransitions())
				resultat.add(transition.getName());
		}
		//adminService.mesuraCalcular("jBPM findStartTaskOutcomes", "jbpmDao");
		return resultat;
	}
	
	
	public List<String> findTaskInstanceOutcomes(String taskInstanceId) {
		//adminService.mesuraIniciar("jBPM findTaskInstanceOutcomes", "jbpmDao");
		List<String> resultat = new ArrayList<String>();
		final long id = Long.parseLong(taskInstanceId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		List<Transition> outcomes = (List<Transition>)taskInstance.getTask().getTaskNode().getLeavingTransitions();
		if (outcomes != null) {
			for (Transition transition: outcomes)
				resultat.add(transition.getName());
		}
		//adminService.mesuraCalcular("jBPM findTaskInstanceOutcomes", "jbpmDao");
		return resultat;
	}
	
	
	public void takeTaskInstance(String taskId, String actorId) {
		//adminService.mesuraIniciar("jBPM takeTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		TakeTaskInstanceCommand command = new TakeTaskInstanceCommand(id, actorId);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM takeTaskInstance", "jbpmDao");
	}
	
	
	public void releaseTaskInstance(String taskId) {
		//adminService.mesuraIniciar("jBPM releaseTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		ReleaseTaskInstanceCommand command = new ReleaseTaskInstanceCommand(id);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM releaseTaskInstance", "jbpmDao");
	}
	
	
	public JbpmTask cloneTaskInstance(String taskId, String actorId, Map<String, Object> variables) {
		//adminService.mesuraIniciar("jBPM cloneTaskInstance", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		CloneTaskInstanceCommand command = new CloneTaskInstanceCommand(
				id,
				actorId,
				false);
		command.setVariables(variables);
		resposta = new JbpmTask((TaskInstance)commandService.execute(command));
		//adminService.mesuraCalcular("jBPM cloneTaskInstance", "jbpmDao");
		return resposta;
	}
	
	
	public JbpmTask startTaskInstance(String taskId) {
		//adminService.mesuraIniciar("jBPM startTaskInstance", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		StartTaskInstanceCommand command = new StartTaskInstanceCommand(id);
		resposta = new JbpmTask((TaskInstance)executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA));
		//adminService.mesuraCalcular("jBPM startTaskInstance", "jbpmDao");
		return resposta;
	}
	
	
	public JbpmTask cancelTaskInstance(String taskId) {
		//adminService.mesuraIniciar("jBPM cancelTaskInstance", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		CancelTaskInstanceCommand command = new CancelTaskInstanceCommand(id);
		resposta = new JbpmTask((TaskInstance)executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA));
		//adminService.mesuraCalcular("jBPM cancelTaskInstance", "jbpmDao");
		return resposta;
	}
	
	
	public JbpmTask suspendTaskInstance(String taskId) {
		//adminService.mesuraIniciar("jBPM suspendTaskInstance", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		SuspendTaskInstanceCommand command = new SuspendTaskInstanceCommand(id);
		resposta = new JbpmTask((TaskInstance)executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA));
		//adminService.mesuraCalcular("jBPM suspendTaskInstance", "jbpmDao");
		return resposta;
	}
	
	
	public JbpmTask resumeTaskInstance(String taskId) {
		//adminService.mesuraIniciar("jBPM resumeTaskInstance", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		ResumeTaskInstanceCommand command = new ResumeTaskInstanceCommand(id);
		resposta = new JbpmTask((TaskInstance)executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA));
		//adminService.mesuraCalcular("jBPM resumeTaskInstance", "jbpmDao");
		return resposta;
	}
	
	
	public JbpmTask reassignTaskInstance(String taskId, String expression) {
		//adminService.mesuraIniciar("jBPM reassignTaskInstance", "jbpmDao");
		JbpmTask resposta = reassignTaskInstance(taskId, expression, null);
		//adminService.mesuraCalcular("jBPM reassignTaskInstance", "jbpmDao");
		return resposta;
	}
	
	
	public JbpmTask reassignTaskInstance(String taskId, String expression, Long entornId) {
		//adminService.mesuraIniciar("jBPM reassignTaskInstance entorn", "jbpmDao");
		JbpmTask resposta = null;
		final long id = Long.parseLong(taskId);
		ReassignTaskInstanceCommand command = new ReassignTaskInstanceCommand(id);
		command.setExpression(expression);
		command.setEntornId(entornId);
		resposta = new JbpmTask((TaskInstance)executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA));
		//adminService.mesuraCalcular("jBPM reassignTaskInstance entorn", "jbpmDao");
		return resposta;
	}
	
	
	public void setTaskInstanceActorId(String taskInstanceId, String actorId) {
		//adminService.mesuraIniciar("jBPM setTaskInstanceActorId", "jbpmDao");
		final long id = Long.parseLong(taskInstanceId);
		ReassignTaskInstanceCommand command = new ReassignTaskInstanceCommand(id);
		command.setActorId(actorId);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM setTaskInstanceActorId", "jbpmDao");
	}
	
	
	public void setTaskInstancePooledActors(String taskInstanceId, String[] pooledActors) {
		//adminService.mesuraIniciar("jBPM setTaskInstancePooledActors", "jbpmDao");
		final long id = Long.parseLong(taskInstanceId);
		ReassignTaskInstanceCommand command = new ReassignTaskInstanceCommand(id);
		command.setPooledActors(pooledActors);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM setTaskInstancePooledActors", "jbpmDao");
	}
	
	
	public void setTaskInstanceVariable(String taskId, String codi, Object valor) {
		//adminService.mesuraIniciar("jBPM setTaskInstanceVariable", "jbpmDao");
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put(codi, valor);
		setTaskInstanceVariables(taskId, vars, false);
		//adminService.mesuraCalcular("jBPM setTaskInstanceVariable", "jbpmDao");
	}
	
	
	public void setTaskInstanceVariables(
			String taskId,
			Map<String, Object> variables,
			boolean deleteFirst) {
		//adminService.mesuraIniciar("jBPM setTaskInstanceVariables", "jbpmDao");
		final long id = Long.parseLong(taskId);
		SaveTaskInstanceVariablesCommand command = new SaveTaskInstanceVariablesCommand(
				id,
				variables);
		command.setLocally(true);
		command.setDeleteFirst(deleteFirst);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM setTaskInstanceVariables", "jbpmDao");
	}
	
	
	public Object getTaskInstanceVariable(String taskId, String varName) {
		//adminService.mesuraIniciar("jBPM getTaskInstanceVariable", "jbpmDao");
		Object resultat = null;
		final long id = Long.parseLong(taskId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		resultat = taskInstance.getVariableLocally(varName);
		//adminService.mesuraCalcular("jBPM getTaskInstanceVariable", "jbpmDao");
		return resultat;
	}
	
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getTaskInstanceVariables(String taskId) {
		//adminService.mesuraIniciar("jBPM getTaskInstanceVariables", "jbpmDao");
		Map<String, Object> resultat = null;
		final long id = Long.parseLong(taskId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance taskInstance = (TaskInstance)commandService.execute(command);
		resultat = (Map<String, Object>)taskInstance.getVariablesLocally();
		//adminService.mesuraCalcular("jBPM getTaskInstanceVariables", "jbpmDao");
		return resultat;
	}
	
	
	public void deleteTaskInstanceVariable(String taskId, String varName) {
		//adminService.mesuraIniciar("jBPM deleteTaskInstanceVariable", "jbpmDao");
		//setTaskInstanceVariable(taskId, varName, null);
		final long id = Long.parseLong(taskId);
		DeleteTaskInstanceVariablesCommand command = new DeleteTaskInstanceVariablesCommand(
				id,
				new String[] {varName});
		command.setLocally(true);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM deleteTaskInstanceVariable", "jbpmDao");
	}
	
	
	public void endTaskInstance(String taskId, String outcome) {
		//adminService.mesuraIniciar("jBPM endTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		TaskInstanceEndCommand command = new TaskInstanceEndCommand(id, outcome);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM endTaskInstance", "jbpmDao");
	}
	
	
	public void describeTaskInstance(String taskId, String titol, String description) {
		//adminService.mesuraIniciar("jBPM describeTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		DescribeTaskInstanceCommand command = new DescribeTaskInstanceCommand(
				id,
				titol,
				description);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM describeTaskInstance", "jbpmDao");
	}
	
	
	public List<JbpmTask> findTaskInstancesForProcessInstance(String processInstanceId) {
		//adminService.mesuraIniciar("jBPM findTaskInstancesForProcessInstance", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		for (TaskInstance ti : pi.getTaskMgmtInstance().getTaskInstances())
			resultat.add(new JbpmTask(ti));
		//adminService.mesuraCalcular("jBPM findTaskInstancesForProcessInstance", "jbpmDao");
		return resultat;
	}
	
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getProcessInstanceVariables(String processInstanceId) {
		//adminService.mesuraIniciar("jBPM getProcessInstanceVariables", "jbpmDao");
		Map<String, Object> resultat = null;
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM getProcessInstanceVariables", "jbpmDao");
		resultat = pi.getContextInstance().getVariables();
		return resultat;
	}
	
	
	public Object getProcessInstanceVariable(String processInstanceId, String varName) {
		//adminService.mesuraIniciar("jBPM getProcessInstanceVariable", "jbpmDao");
		Object resultat = null;
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		resultat = pi.getContextInstance().getVariable(varName);
		//adminService.mesuraCalcular("jBPM getProcessInstanceVariable", "jbpmDao");
		return resultat;
	}
	
	
	public void setProcessInstanceVariable(
			String processInstanceId,
			String varName,
			Object value) {
		//adminService.mesuraIniciar("jBPM setProcessInstanceVariable", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put(varName, value);
		SaveProcessInstanceVariablesCommand command = new SaveProcessInstanceVariablesCommand(id, vars);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM setProcessInstanceVariable", "jbpmDao");
	}
	
	
	public void deleteProcessInstanceVariable(String processInstanceId, String varName) {
		//adminService.mesuraIniciar("jBPM deleteProcessInstanceVariable", "jbpmDao");
		//setProcessInstanceVariable(processInstanceId, varName, null);
		final long id = Long.parseLong(processInstanceId);
		DeleteProcessInstanceVariablesCommand command = new DeleteProcessInstanceVariablesCommand(id, new String[] {varName});
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM deleteProcessInstanceVariable", "jbpmDao");
	}
	
	
	public JbpmToken getTokenById(String tokenId) {
		//adminService.mesuraIniciar("jBPM getTokenById", "jbpmDao");
		final long id = Long.parseLong(tokenId);
		GetTokenByIdCommand command = new GetTokenByIdCommand(id);
		JbpmToken resultat = new JbpmToken((Token)commandService.execute(command));
		//adminService.mesuraCalcular("jBPM getTokenById", "jbpmDao");
		return resultat;
	}
	
	
	public Map<String, JbpmToken> getActiveTokens(String processInstanceId) {
		//adminService.mesuraIniciar("jBPM getActiveTokens", "jbpmDao");
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
		//adminService.mesuraCalcular("jBPM getActiveTokens", "jbpmDao");
		return resposta;
	}
	
	@SuppressWarnings("unchecked")
	private  Map<String, Token> getActiveTokens(Token token){
		//adminService.mesuraIniciar("jBPM getActiveTokens", "jbpmDao");
		Map<String, Token> activeTokens = new HashMap<String, Token>();
		if (token.hasActiveChildren()) {
			activeTokens = token.getActiveChildren();
			Map<String, Token> tokensPerAfegir = new HashMap<String, Token>();
			for (Token t: activeTokens.values()) {
				tokensPerAfegir.putAll(getActiveTokens(t));
			}
			activeTokens.putAll(tokensPerAfegir);
		}
		//adminService.mesuraCalcular("jBPM getActiveTokens", "jbpmDao");
		return activeTokens;
	}
	
	public Map<String, JbpmToken> getAllTokens(String processInstanceId) {
		//adminService.mesuraIniciar("jBPM getAllTokens", "jbpmDao");
		Map<String, JbpmToken> resposta = new HashMap<String, JbpmToken>();
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(id);
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		Token root = processInstance.getRootToken();
		
		getTokenAndChildren(root, resposta);
		
		//adminService.mesuraCalcular("jBPM getAllTokens", "jbpmDao");
		return resposta;
	}
	
	private void getTokenAndChildren(Token root, Map<String,JbpmToken> resposta){
		Map<String, Token> childTokens = root.getChildren();
		for (String tokenName: childTokens.keySet()) {
			JbpmToken child = new JbpmToken(childTokens.get(tokenName));
			resposta.put(tokenName,child);
			if(child.getToken().getChildren() != null){
				getTokenAndChildren(child.getToken(),resposta);
			}
		}
		resposta.put(root.getName(),new JbpmToken(root));
	}
	
//	private HashMap<String,JbpmToken> sortTokensMapById(Map<String,JbpmToken> hMap) { 
//		TreeMap<String, JbpmToken> treeMap = new TreeMap<String, JbpmToken>(new TokenComparator());
//		treeMap.putAll(hMap);
//	       
//		return null;
//	}
//	
//	private class TokenComparator implements Comparator<JbpmToken> {
//	    @Override
//	    public int compare(JbpmToken o1, JbpmToken o2) {
//	        return o1.getId().compareTo(o2.getId());
//	    }
//	}
	
	
	@SuppressWarnings("unchecked")
	public List<String> findArrivingNodeNames(String tokenId) {
		//adminService.mesuraIniciar("jBPM findArrivingNodeNames", "jbpmDao");
		final long id = Long.parseLong(tokenId);
		FindArrivingNodeNamesCommand command = new FindArrivingNodeNamesCommand(id);
		List<String> resultat = (List<String>)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM findArrivingNodeNames", "jbpmDao");
		return resultat;
	}
	
	
	public void tokenRedirect(
			long tokenId,
			String nodeName,
			boolean cancelTasks,
			boolean enterNodeIfTask,
			boolean executeNode) {
		//adminService.mesuraIniciar("jBPM tokenRedirect", "jbpmDao");
		TokenRedirectCommand command = new TokenRedirectCommand(tokenId, nodeName);
		command.setCancelTasks(cancelTasks);
		command.setEnterNodeIfTask(enterNodeIfTask);
		command.setExecuteNode(executeNode);
		executeCommandWithAutoSave(
				command,
				tokenId,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		//adminService.mesuraCalcular("jBPM tokenRedirect", "jbpmDao");
	}


	public boolean tokenActivar(long tokenId, boolean activar) {
		//adminService.mesuraIniciar("jBPM tokenActivar", "jbpmDao");		
		try {
			TokenActivarCommand command = new TokenActivarCommand(tokenId, activar);
			executeCommandWithAutoSave(
					command,
					tokenId,
					AddToAutoSaveCommand.TIPUS_TOKEN);
			return true;
		} catch (Exception ex) {
			return false;
		} finally {
			//adminService.mesuraCalcular("jBPM tokenActivar", "jbpmDao");
		}
	}

	
	@SuppressWarnings("unchecked")
	public Map<String, Object> evaluateScript(
			String processInstanceId,
			String script,
			Set<String> outputNames) {
		//adminService.mesuraIniciar("jBPM evaluateScript", "jbpmDao");
		Map<String,Object> resultat = null;
		final long id = Long.parseLong(processInstanceId);
		if (outputNames == null)
			outputNames = new HashSet<String>();
		EvaluateScriptCommand command = new EvaluateScriptCommand(
				id,
				script,
				outputNames);
		resultat = (Map<String,Object>)executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM evaluateScript", "jbpmDao");
		return resultat;
	}
	
	
	public Object evaluateExpression(
			String taskInstanceInstanceId,
			String processInstanceId,
			String expression,
			Map<String, Object> valors) {
		//adminService.mesuraIniciar("jBPM evaluateExpression", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		EvaluateExpressionCommand command = new EvaluateExpressionCommand(
				id,
				expression);
		if (taskInstanceInstanceId != null)
			command.setTid(Long.parseLong(taskInstanceInstanceId));
		if (valors != null)
			command.setValors(valors);
		Object resultat = executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM evaluateExpression", "jbpmDao");
		return resultat;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<String> listActions(String jbpmId) {
		//adminService.mesuraIniciar("jBPM listActions", "jbpmDao");
		final long id = Long.parseLong(jbpmId);
		List<String> llista = (List<String>)commandService.execute(
				new ListActionsCommand(id));
		//adminService.mesuraCalcular("jBPM listActions", "jbpmDao");
		return llista;
	}
	
	
	public void executeActionInstanciaProces(
			String processInstanceId,
			String actionName,
			String processDefinitionPareId) {
		//adminService.mesuraIniciar("jBPM executeActionInstanciaProces", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		ExecuteActionCommand command = new ExecuteActionCommand(
				id,
				actionName,
				processDefinitionPareId);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM executeActionInstanciaProces", "jbpmDao");
	}
	
	
	public void executeActionInstanciaTasca(
			String taskInstanceId,
			String actionName,
			String processDefinitionPareId) {
		//adminService.mesuraIniciar("jBPM executeActionInstanciaTasca", "jbpmDao");
		final long id = Long.parseLong(taskInstanceId);
		ExecuteActionCommand command = new ExecuteActionCommand(
				id,
				actionName,
				processDefinitionPareId);
		command.setTaskInstance(true);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM executeActionInstanciaTasca", "jbpmDao");
	}
	
	
	public void retrocedirAccio(
			String processInstanceId,
			String actionName,
			List<String> params,
			String processDefinitionPareId) {
		//adminService.mesuraIniciar("jBPM retrocedirAccio", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		ExecuteActionCommand command = new ExecuteActionCommand(
				id,
				actionName,
				processDefinitionPareId);
		command.setGoBack(true);
		command.setParams(params);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM retrocedirAccio", "jbpmDao");
	}
	
	
	public void changeProcessInstanceVersion(
			String processInstanceId,
			int newVersion) {
		//adminService.mesuraIniciar("jBPM changeProcessInstanceVersion", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		ChangeProcessInstanceVersionCommand command = new ChangeProcessInstanceVersionCommand(
				id,
				newVersion);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM changeProcessInstanceVersion", "jbpmDao");
	}
	
	
	public void signalToken(
			long tokenId,
			String transitionName) {
		//adminService.mesuraIniciar("jBPM signalToken", "jbpmDao");
		SignalCommand command = new SignalCommand(tokenId, transitionName);
		executeCommandWithAutoSave(
				command,
				tokenId,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		//adminService.mesuraCalcular("jBPM signalToken", "jbpmDao");
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Timer> findTimersWithProcessInstanceId(
			String processInstanceId) {
		//adminService.mesuraIniciar("jBPM findTimersWithProcessInstanceId", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		FindProcessInstanceTimersCommand command = new FindProcessInstanceTimersCommand(id);
		List<Timer> llista = (List<Timer>)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM findTimersWithProcessInstanceId", "jbpmDao");
		return llista;
	}
	
	
	public void suspendTimer(
			long timerId,
			Date dueDate) {
		//adminService.mesuraIniciar("jBPM suspendTimer", "jbpmDao");
		SuspendProcessInstanceTimerCommand command = new SuspendProcessInstanceTimerCommand(timerId);
		command.setDueDate(dueDate);
		commandService.execute(command);
		//adminService.mesuraCalcular("jBPM suspendTimer", "jbpmDao");
	}
	
	
	public void resumeTimer(
			long timerId,
			Date dueDate) {
		//adminService.mesuraIniciar("jBPM resumeTimer", "jbpmDao");
		ResumeProcessInstanceTimerCommand command = new ResumeProcessInstanceTimerCommand(timerId);
		command.setDueDate(dueDate);
		commandService.execute(command);
		//adminService.mesuraCalcular("jBPM resumeTimer", "jbpmDao");
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Token, List<ProcessLog>> getProcessInstanceLogs(String processInstanceId) {
		//adminService.mesuraIniciar("jBPM getProcessInstanceLogs", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		FindProcessInstanceLogsCommand command = new FindProcessInstanceLogsCommand(id);
		Map resultat = (Map)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM getProcessInstanceLogs", "jbpmDao");
		return resultat;
	}
	
	
	public long addProcessInstanceMessageLog(String processInstanceId, String message) {
		//adminService.mesuraIniciar("jBPM addProcessInstanceMessageLog", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		AddProcessInstanceMessageLogCommand command = new AddProcessInstanceMessageLogCommand(id, message);
		long resultat = ((Long)commandService.execute(command)).longValue();
		//adminService.mesuraCalcular("jBPM addProcessInstanceMessageLog", "jbpmDao");
		return resultat;
	}
	
	
	public long addTaskInstanceMessageLog(String taskInstanceId, String message) {
		//adminService.mesuraIniciar("jBPM addTaskInstanceMessageLog", "jbpmDao");
		final long id = Long.parseLong(taskInstanceId);
		AddTaskInstanceMessageLogCommand command = new AddTaskInstanceMessageLogCommand(id, message);
		long resultat = ((Long)commandService.execute(command)).longValue();
		//adminService.mesuraCalcular("jBPM addTaskInstanceMessageLog", "jbpmDao");
		return resultat;
	}
	
	
	public Long getVariableIdFromVariableLog(long variableLogId) {
		//adminService.mesuraIniciar("jBPM getVariableIdFromVariableLog", "jbpmDao");
		GetVariableIdFromVariableLogCommand command = new GetVariableIdFromVariableLogCommand(variableLogId);
		Long resultat = (Long)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM getVariableIdFromVariableLog", "jbpmDao");
		return resultat;
	}
	
	
	public Long getTaskIdFromVariableLog(long variableLogId) {
		//adminService.mesuraIniciar("jBPM getTaskIdFromVariableLog", "jbpmDao");
		GetTaskIdFromVariableLogCommand command = new GetTaskIdFromVariableLogCommand(variableLogId);
		Long resultat = (Long)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM getTaskIdFromVariableLog", "jbpmDao");
		return resultat;
	}
	
	
	public void cancelProcessInstance(long id) {
		//adminService.mesuraIniciar("jBPM cancelProcessInstance", "jbpmDao");
		CancelProcessInstanceCommand command = new CancelProcessInstanceCommand(id);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM cancelProcessInstance", "jbpmDao");
	}
	
	
	public void revertProcessInstanceEnd(long id) {
		//adminService.mesuraIniciar("jBPM revertProcessInstanceEnd", "jbpmDao");
		RevertProcessInstanceEndCommand command = new RevertProcessInstanceEndCommand(id);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM revertProcessInstanceEnd", "jbpmDao");
	}
	
	
	public void cancelToken(long id) {
		//adminService.mesuraIniciar("jBPM cancelToken", "jbpmDao");
		CancelTokenCommand command = new CancelTokenCommand(id);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		//adminService.mesuraCalcular("jBPM cancelToken", "jbpmDao");
	}
	
	
	public void revertTokenEnd(long id) {
		//adminService.mesuraIniciar("jBPM revertTokenEnd", "jbpmDao");
		JbpmToken jtoken = getTokenById(String.valueOf(id));
		RevertTokenEndCommand command = new RevertTokenEndCommand(jtoken);
		executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_TOKEN);
//		this.sessionFactory.getCurrentSession().refresh(jtoken.getToken());
		jtoken.getToken().setAbleToReactivateParent(true);
		//adminService.mesuraCalcular("jBPM revertTokenEnd", "jbpmDao");
	}
	
	
	public JbpmTask findEquivalentTaskInstance(long tokenId, long taskInstanceId) {
		//adminService.mesuraIniciar("jBPM findEquivalentTaskInstance", "jbpmDao");
		GetTaskInstanceCommand commandGetTask = new GetTaskInstanceCommand(taskInstanceId);
		TaskInstance ti = (TaskInstance)commandService.execute(commandGetTask);
		FindTaskInstanceForTokenAndTaskCommand command = new FindTaskInstanceForTokenAndTaskCommand(tokenId, ti.getTask().getName());
		JbpmTask resultat = new JbpmTask((TaskInstance)commandService.execute(command));
		//adminService.mesuraCalcular("jBPM findEquivalentTaskInstance", "jbpmDao");
		return resultat;
	}
	
	
	public boolean isProcessStateNodeJoinOrFork(long processInstanceId, String nodeName) {
		//adminService.mesuraIniciar("jBPM isProcessStateNodeJoinOrFork", "jbpmDao");
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(processInstanceId);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		Node node = pi.getProcessDefinition().getNode(nodeName);
		String nodeClassName = node.toString();
		NodeType nodeType = node.getNodeType();
		//adminService.mesuraCalcular("jBPM isProcessStateNodeJoinOrFork", "jbpmDao");
		
		return (nodeClassName.startsWith("ProcessState") || nodeType == NodeType.Fork || nodeType == NodeType.Join);
	}
	
	
	public boolean isJoinNode(long processInstanceId, String nodeName) {
		//adminService.mesuraIniciar("jBPM isJoinNode", "jbpmDao");
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(processInstanceId);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		NodeType nodeType = null;
		try {
			nodeType = pi.getProcessDefinition().getNode(nodeName).getNodeType();
		} catch (Exception ex) {
			return false;
		} finally {
			//adminService.mesuraCalcular("jBPM isJoinNode", "jbpmDao");	
		}
		
		return nodeType == NodeType.Join;
	}
	
	
	public ProcessLog getProcessLogById(Long id){
		//adminService.mesuraIniciar("jBPM getProcessLogById", "jbpmDao");
		GetProcessLogByIdCommand command = new GetProcessLogByIdCommand(id.longValue());
		ProcessLog log = (ProcessLog)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM getProcessLogById", "jbpmDao");
		return log;
	}
	
	
	public Node getNodeByName(long processInstanceId, String nodeName) {
		//adminService.mesuraIniciar("jBPM getNodeByName", "jbpmDao");
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(processInstanceId);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		Node node = pi.getProcessDefinition().getNode(nodeName);
		//adminService.mesuraCalcular("jBPM getNodeByName", "jbpmDao");
		return node;
	}
	
	
	public boolean hasStartBetweenLogs(long begin, long end, long taskInstanceId) {
		//adminService.mesuraIniciar("jBPM hasStartBetweenLogs", "jbpmDao");
		HasStartBetweenLogsCommand command = new HasStartBetweenLogsCommand(begin, end, taskInstanceId);
		Boolean hasStart = (Boolean)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM hasStartBetweenLogs", "jbpmDao");
		return hasStart.booleanValue();
	}	

	@Autowired
	public void setCommandService(CommandService commandService) {
		this.commandService = commandService;
	}

	public LlistatIds findListTasks( // 3.0
			String responsable,
			String tasca,
			String tascaSel,
			List<Long> idsPIExpedients,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			PaginacioParamsDto paginacioParams,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAmbPendents) {
		//adminService.mesuraIniciar("jBPM findListTasks", "jbpmDao");
		GetRootProcessInstancesForActiveTasksCommand command = new GetRootProcessInstancesForActiveTasksCommand(
				responsable,
				tasca,
				tascaSel,
				idsPIExpedients,
				dataCreacioInici,
				dataCreacioFi,
				prioritat,
				dataLimitInici,
				dataLimitFi,
				paginacioParams.getOrdres(),
				nomesTasquesPersonals,
				nomesTasquesGrup);
		command.setFirstRow(
				paginacioParams.getPaginaNum() * paginacioParams.getPaginaTamany());
		command.setMaxResults(
				paginacioParams.getPaginaTamany());
		command.setNomesActives(nomesAmbPendents);
		LlistatIds llistat = (LlistatIds)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM findListTasks", "jbpmDao");
		return llistat;
	}
	
	public LlistatIds findListTasks( // 2.6
			String usuariBo, 
			String tascaSel,
			String titol,
			List<Long> idsPIExpedients, 
			Date dataCreacioInici, 
			Date dataCreacioFi, 
			Integer prioritat, 
			Date dataLimitInici, 
			Date dataLimitFi, 
			int firstRow, 
			int maxResults, 
			String sort, 
			boolean asc,
			MostrarTasquesDto mostrarTasques) {
		//adminService.mesuraIniciar("jBPM findListTasks", "jbpmDao");
		GetRootProcessInstancesForActiveTasksCommand command = new GetRootProcessInstancesForActiveTasksCommand(
				usuariBo, 
				null, 
				tascaSel, 
				idsPIExpedients, 
				dataCreacioInici, 
				dataCreacioFi, 
				prioritat, 
				dataLimitInici, 
				dataLimitFi, 
				sort, 
				asc, 
				mostrarTasques);
		command.setFirstRow(firstRow);
		command.setMaxResults(maxResults);
		command.setTitol(titol);
		LlistatIds llistat = (LlistatIds)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM findListTasks", "jbpmDao");
		return llistat;
	}	
	
	public LlistatIds findListPersonalTasks( // 2.6
			String usuariBo, 
			String tasca, 
			List<Long> idsPIExpedients, 
			Date dataCreacioInici, 
			Date dataCreacioFi, 
			Integer prioritat, 
			Date dataLimitInici, 
			Date dataLimitFi, 
			int firstRow, 
			int maxResults, 
			String sort, 
			boolean asc) {
		//adminService.mesuraIniciar("jBPM findListPersonalTasks", "jbpmDao");
		GetRootProcessInstancesForActiveTasksCommand command = new GetRootProcessInstancesForActiveTasksCommand(usuariBo, tasca, null, idsPIExpedients, dataCreacioInici, dataCreacioFi, prioritat, dataLimitInici, dataLimitFi, sort, asc, false);
		command.setFirstRow(firstRow);
		command.setMaxResults(maxResults);
		LlistatIds llistat = (LlistatIds)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM findListPersonalTasks", "jbpmDao");
		return llistat;
	}
	
	public LlistatIds findListGroupTasks( // 2.6
			String usuariBo, 
			String tasca, 
			List<Long> idsPIExpedients, 
			Date dataCreacioInici, 
			Date dataCreacioFi, 
			Integer prioritat, 
			Date dataLimitInici, 
			Date dataLimitFi, 
			int firstRow, 
			int maxResults, 
			String sort, 
			boolean asc) {
		//adminService.mesuraIniciar("jBPM findListGroupTasks", "jbpmDao");
		GetRootProcessInstancesForActiveTasksCommand command = new GetRootProcessInstancesForActiveTasksCommand(usuariBo, tasca, null, idsPIExpedients, dataCreacioInici, dataCreacioFi, prioritat, dataLimitInici, dataLimitFi, sort, asc, true);
		command.setFirstRow(firstRow);
		command.setMaxResults(maxResults);
		LlistatIds llistat = (LlistatIds)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM findListGroupTasks", "jbpmDao");
		return llistat;
	}
	
	public LlistatIds findListIdsTasks( // 2.6
			String actorId,
			List<Long> idsPIExpedients) {
		GetRootProcessInstancesForActiveTasksCommand command = new GetRootProcessInstancesForActiveTasksCommand(actorId, idsPIExpedients);
		LlistatIds resultado = (LlistatIds)commandService.execute(command);
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<JbpmTask> findTasks( // 3.0 i 2.6
			List<Long> ids) {
		//adminService.mesuraIniciar("jBPM findTasks ids", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetTaskListCommand command = new GetTaskListCommand(ids);
		for (TaskInstance ti: (List<TaskInstance>)commandService.execute(command)) {
			resultat.add(new JbpmTask(ti));
		}
		//adminService.mesuraCalcular("jBPM findTasks ids", "jbpmDao");
		return resultat;
	}
	
	public Long getTaskInstanceIdByTokenId(Long tokenId) {
		FindTaskInstanceIdForTokenIdCommand command = new FindTaskInstanceIdForTokenIdCommand(tokenId);
		return (Long)commandService.execute(command);
	}
	
	@SuppressWarnings("unchecked")
	public List<JbpmTask> findPersonalTasks( // 2.6
			List<Long> ids,
			String usuariBo) {
		//adminService.mesuraIniciar("jBPM findPersonalTasks ids", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetPersonalTaskListCommand command = new GetPersonalTaskListCommand(usuariBo, ids);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		//adminService.mesuraCalcular("jBPM findPersonalTasks ids", "jbpmDao");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<Long> findIdsRootProcessInstanceGroupTasks( // 2.6
			List<Long> ids,
			String usuariBo) {
		//adminService.mesuraIniciar("jBPM findIdsRootProcessInstanceGroupTasks ids", "jbpmDao");
		GetGroupTaskListCommand command = new GetGroupTaskListCommand(usuariBo, ids, true);
		List<Long> res = (List<Long>) commandService.execute(command);
		//adminService.mesuraCalcular("jBPM findIdsRootProcessInstanceGroupTasks ids", "jbpmDao");
		return res;
	}	
	
	@SuppressWarnings("unchecked")
	public List<JbpmTask> findGroupTasks( // 2.6
			List<Long> ids,
			String usuariBo) {
		//adminService.mesuraIniciar("jBPM findGroupTasks ids", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetGroupTaskListCommand command = new GetGroupTaskListCommand(usuariBo, ids);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		//adminService.mesuraCalcular("jBPM findGroupTasks ids", "jbpmDao");
		return resultat;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<JbpmTask> findPersonalTasks( // 2.6
			String usuariBo) {
		//adminService.mesuraIniciar("jBPM findPersonalTasks", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		GetPersonalTaskListCommand command = new GetPersonalTaskListCommand(usuariBo);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		//adminService.mesuraCalcular("jBPM findPersonalTasks", "jbpmDao");
		return resultat;
	}
	
	public LlistatIds findListIdsPersonalTasks( // 2.6
			String actorId,
			List<Long> idsPIExpedients) {
		//adminService.mesuraIniciar("jBPM findListIdsPersonalTasks", "jbpmDao");
		GetRootProcessInstancesForActiveTasksCommand command = new GetRootProcessInstancesForActiveTasksCommand(actorId, idsPIExpedients, false);
		LlistatIds resultado = (LlistatIds)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM findListIdsPersonalTasks", "jbpmDao");
		return resultado;
	}
	
	
	public LlistatIds findListIdsGroupTasks( // 2.6
			String actorId,
			List<Long> idsPIExpedients) {
		//adminService.mesuraIniciar("jBPM findListIdsGroupTasks", "jbpmDao");
		GetRootProcessInstancesForActiveTasksCommand command = new GetRootProcessInstancesForActiveTasksCommand(actorId, idsPIExpedients, true);
		LlistatIds resultado = (LlistatIds)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM findListIdsGroupTasks", "jbpmDao");
		return resultado;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<JbpmTask> findGroupTasks( // 2.6
			String actorId) {
		//adminService.mesuraIniciar("jBPM findGroupTasks", "jbpmDao");
		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
		
		GetGroupTaskListCommand command = new GetGroupTaskListCommand(actorId);
		for (TaskInstance ti : (List<TaskInstance>)commandService.execute(command))
			resultat.add(new JbpmTask(ti));
		//adminService.mesuraCalcular("jBPM findGroupTasks", "jbpmDao");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<String> findRootProcessInstancesWithTasksCommand( // 3.0
			String actorId,
			List<String> processInstanceIds,
			boolean nomesMeves,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		//adminService.mesuraIniciar("jBPM findRootProcessInstancesForExpedientsWithActiveTasksCommand", "jbpmDao");
		boolean nomesAmbPendents = true; // Mostrar sólo las pendientes
		boolean personals = nomesTasquesPersonals && !nomesTasquesGrup;
		boolean grup = !nomesTasquesPersonals && nomesTasquesGrup;
		boolean tots = !nomesTasquesPersonals && !nomesTasquesGrup;
		
		List<String> resultat = new ArrayList<String>();
		if (personals) {
			GetRootProcessInstancesForExpedientsWithActiveTasksCommand commandPersonal = new GetRootProcessInstancesForExpedientsWithActiveTasksCommand(actorId, processInstanceIds, false, nomesAmbPendents, nomesMeves);
			resultat.addAll((List<String>)commandService.execute(commandPersonal));
		} else if (grup && !nomesMeves) {
			GetRootProcessInstancesForExpedientsWithActiveTasksCommand commandGroup = new GetRootProcessInstancesForExpedientsWithActiveTasksCommand(actorId, processInstanceIds, true, nomesAmbPendents, nomesMeves);
			resultat.addAll((List<String>)commandService.execute(commandGroup));
		} else if (tots) {
			GetRootProcessInstancesForExpedientsWithActiveTasksCommand commandPersonal = new GetRootProcessInstancesForExpedientsWithActiveTasksCommand(actorId, processInstanceIds, false, nomesAmbPendents, nomesMeves);
			resultat.addAll((List<String>)commandService.execute(commandPersonal));
			if (!nomesMeves) {
				GetRootProcessInstancesForExpedientsWithActiveTasksCommand commandGroup = new GetRootProcessInstancesForExpedientsWithActiveTasksCommand(actorId, processInstanceIds, true, nomesAmbPendents, nomesMeves);
				resultat.addAll((List<String>)commandService.execute(commandGroup));
			}
		}

		List<String> idsDiferents = new ArrayList<String>();
		for (String id: resultat) 
			idsDiferents.add(id);
		
		//adminService.mesuraCalcular("jBPM findRootProcessInstancesForExpedientsWithActiveTasksCommand", "jbpmDao");
		return idsDiferents;
	}

	@SuppressWarnings("unchecked")
	public void deleteProcessInstanceTreeLogs(String rootProcessInstanceId) {
		//adminService.mesuraIniciar("jBPM deleteProcessInstanceTreeLogs", "jbpmDao");
		final long id = Long.parseLong(rootProcessInstanceId);
		GetProcessInstancesTreeCommand command = new GetProcessInstancesTreeCommand(id);
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			DeleteProcessInstanceLogsCommand deleteCommand = new DeleteProcessInstanceLogsCommand(pd);
			commandService.execute(deleteCommand);
		}
		//adminService.mesuraCalcular("jBPM deleteProcessInstanceTreeLogs", "jbpmDao");
	}
	
	public void retryJob(Long jobId) {
		RetryJobCommand command = new RetryJobCommand(jobId);
		commandService.execute(command);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findDefinicionsProcesIdNoUtilitzadesByEntorn(Long entornId) {
		List<String> resultat = new ArrayList<String>();
		
		GetProcesDefinitionEntornNotUsedListCommand command = new GetProcesDefinitionEntornNotUsedListCommand(entornId);
		for (ProcessDefinition pd : (List<ProcessDefinition>)commandService.execute(command))
			resultat.add(String.valueOf(pd.getId()));
		//adminService.mesuraCalcular("jBPM findGroupTasks", "jbpmDao");
		return resultat;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(Long expedientTipusId) {
		List<String> resultat = new ArrayList<String>();
		
		GetProcesDefinitionNotUsedListCommand command = new GetProcesDefinitionNotUsedListCommand(expedientTipusId);
		for (ProcessDefinition pd : (List<ProcessDefinition>)commandService.execute(command))
			resultat.add(String.valueOf(pd.getId()));
		//adminService.mesuraCalcular("jBPM findGroupTasks", "jbpmDao");
		return resultat;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcessInstanceExpedient> findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
			Long expedientTipusId,
			Long processDefinitionId) {
		GetExpedientsAfectatsListCommand command = new GetExpedientsAfectatsListCommand(expedientTipusId, processDefinitionId);
		return (List<ProcessInstanceExpedient>)commandService.execute(command);
	}

	public void desfinalitzarExpedient(String processInstanceId){
		//adminService.mesuraIniciar("jBPM reprendreExpedient", "jbpmDao");
		
		// Recuperamos el token EndState más reciente
		JbpmProcessInstance rootProcessInstance = getRootProcessInstance(processInstanceId);
		List<JbpmProcessInstance> lista = getProcessInstanceTree(String.valueOf(rootProcessInstance.getProcessInstance().getId()));
		Token token = null;
		for (JbpmProcessInstance pi : lista) {
			Map<String, JbpmToken> tokens = getAllTokens(String.valueOf(pi.getProcessInstance().getId()));
			for (String tokenName: tokens.keySet()) {
				Token tokenTmp = tokens.get(tokenName).getToken();
				if (!NodeType.EndState.equals(tokenTmp.getNode().getNodeType())) {
					if(tokenTmp.hasParent()) {
						tokenTmp = tokenTmp.getParent();
						if (NodeType.EndState.equals(tokenTmp.getNode().getNodeType()) && tokenTmp.hasEnded()) {
							if (token == null)
								token = tokenTmp;
							else if (tokenTmp.getEnd().after(token.getEnd()))
								token = tokenTmp;
						}
					}
				} else if (tokenTmp.hasEnded()) {
					if (token == null)
						token = tokenTmp;
					else if (tokenTmp.getEnd().after(token.getEnd()))
						token = tokenTmp;
				}
			}
		}
				
		// Activamos recursivamente
		while (token != null) {
			JbpmToken jtoken = getTokenById(String.valueOf(token.getId()));
			RevertTokenEndCommand command = new RevertTokenEndCommand(jtoken);
			executeCommandWithAutoSave(
					command,
					token.getId(),
					AddToAutoSaveCommand.TIPUS_TOKEN);
			token = token.getParent();
		}
		
		// Activamos la instancia de proceso
		revertProcessInstanceEnd(rootProcessInstance.getProcessInstance().getId());
		
		//adminService.mesuraCalcular("jBPM reprendreExpedient", "jbpmDao");
	}
	
	public void finalitzarExpedient(String[] processInstanceIds, Date dataFinalitzacio){
		long[] ids = new long[processInstanceIds.length];
		for (int i = 0; i < processInstanceIds.length; i++)
			ids[i] = Long.parseLong(processInstanceIds[i]);
		ProcessInstanceEndCommand command = new ProcessInstanceEndCommand(ids, dataFinalitzacio);
		executeCommandWithAutoSave(
				command,
				ids,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
	}

	/** Actualitza els recursos .class de la definició de procés indicada amb els recursos
	 * continguts en el jbpmProcessDefinition.
	 * @param jbpmId Identifica la definició de procés a actualtizar.
	 * @param jbpmProcessDefinition Conté la informació amb què actualitzar els handlers.
	 */
	public void updateHandlers (
			Long jbpmId, 
			Map<String, byte[]> handlers) {
		// Omple que command que substitueix els handlers existents
		UpdateHandlersCommand command = new UpdateHandlersCommand(
				jbpmId,
				handlers);
		commandService.execute(command);
	}
	
	private Object executeCommandWithAutoSave(
			Command command,
			long id,
			int autoSaveTipus) throws RuntimeException {
		try {
			AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
					command,
					id,
					autoSaveTipus);
			return commandService.execute(autoSaveCommand);
		} catch (JbpmException ex) {
			//Inserim l'error a la tasca si aquesta s'ha executat en segon pla
			Jbpm3HeliumBridge.getInstanceService().setErrorTascaSegonPla(id, ex);
			//
			throw tractarExceptionJbpm(
					ex,
					id,
					autoSaveTipus);
		}
	}
	private Object executeCommandWithAutoSave(
			Command command,
			long[] ids,
			int autoSaveTipus) throws RuntimeException {
		try {
			AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
					command,
					ids,
					autoSaveTipus);
			return commandService.execute(autoSaveCommand);
		} catch (JbpmException ex) {
			throw tractarExceptionJbpm(
					ex,
					(ids!= null && ids.length > 0) ? ids[0] : null,
					autoSaveTipus);
		}
	}

	private RuntimeException tractarExceptionJbpm(
			JbpmException ex,
			long id,
			int autoSaveTipus) {
		Long taskInstanceId = null;
		Long tokenId = null;
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		switch (autoSaveTipus) {
		case AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES:
			command.setProcessInstanceId(id);
			break;
		case AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA:
			taskInstanceId = new Long(id);
			command.setTaskInstanceId(id);
			break;
		case AddToAutoSaveCommand.TIPUS_TOKEN:
			tokenId = new Long(id);
			command.setTokenId(id);
			break;
		}
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		Long processInstanceId = new Long(processInstance.getId());
		ProcessInstanceExpedient expedient = processInstance.getExpedient();
		if (ex.getCause() != null && ex.getCause() instanceof DelegationException && ex.getCause().getCause() != null) {
			for (StackTraceElement element: ex.getCause().getCause().getStackTrace()) {
				if (element.getMethodName().equals("execute")) {
					return new ExecucioHandlerException(
							(expedient != null) ? expedient.getId() : null,
							(expedient != null) ? expedient.getTipus().getId() : null,
							processInstanceId,
							taskInstanceId,
							tokenId,
							element.getClassName(),
							element.getMethodName(),
							element.getFileName(),
							element.getLineNumber(),
							ex.getCause().getCause().getMessage(),
							ex.getCause().getCause());
				}
			}
		}
		return ex;
	}
}
