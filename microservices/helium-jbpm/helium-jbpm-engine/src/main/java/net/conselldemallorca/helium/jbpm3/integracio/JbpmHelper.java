/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import net.conselldemallorca.helium.api.dto.ExpedientDto;
import net.conselldemallorca.helium.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.api.dto.LlistatIds;
import net.conselldemallorca.helium.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.api.dto.PaginacioParamsDto.OrdreDto;
import net.conselldemallorca.helium.api.dto.ResultatConsultaPaginada;
import net.conselldemallorca.helium.api.exception.HeliumJbpmException;
import net.conselldemallorca.helium.api.service.WDeployment;
import net.conselldemallorca.helium.api.service.WProcessDefinition;
import net.conselldemallorca.helium.api.service.WProcessInstance;
import net.conselldemallorca.helium.api.service.WTaskInstance;
import net.conselldemallorca.helium.api.service.WToken;
import net.conselldemallorca.helium.api.service.WorkflowEngineApi;
import net.conselldemallorca.helium.jbpm3.helper.CommandHelper;
import net.conselldemallorca.helium.jbpm3.command.*;
import org.jbpm.command.*;
import org.jbpm.command.CancelProcessInstanceCommand;
import org.jbpm.command.StartProcessInstanceCommand;
import org.jbpm.file.def.FileDefinition;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.ProcessInstanceExpedient;
import org.jbpm.graph.exe.ProcessInstanceExpedientTipus;
import org.jbpm.graph.exe.Token;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.ExpressionEvaluator;
import org.jbpm.jpdl.el.VariableResolver;
import org.jbpm.jpdl.el.impl.ExpressionEvaluatorImpl;
import org.jbpm.logging.log.ProcessLog;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Dao per a l'accés a la funcionalitat de jBPM3
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@Transactional
public class JbpmHelper implements WorkflowEngineApi {

	private CommandService commandService;
	private CommandHelper commandHelper;

	@Autowired
	public void setCommandService(CommandService commandService) {
		this.commandService = commandService;
	}
	@Autowired
	public void setCommandHelper(CommandHelper commandHelper) {
		this.commandHelper = commandHelper;
	}

	// DEFINICIÓ DE PROCÉS
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////


	// Desplegaments
	////////////////////////////////////////////////////////////////////////////////

	public WDeployment desplegar(
			String nomArxiu,
			byte[] contingut) {
		//adminService.mesuraIniciar("jBPM desplegar", "jbpmDao");
		JbpmDeployment resposta = null;
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
		resposta = new JbpmDeployment(processDefinition);
		//adminService.mesuraCalcular("jBPM desplegar", "jbpmDao");
		return resposta;
	}

	@Override
	public WDeployment getDesplegament(String processDefinitionId) {
		ProcessDefinition processDefinition = getDefinicioProces(processDefinitionId);
		if (processDefinition != null)
			return new JbpmDeployment(processDefinition);
		return null;
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

	/** Actualitza els recursos .class de la definició de procés indicada amb els recursos
	 * continguts en el jbpmProcessDefinition.
	 * @param jbpmId Identifica la definició de procés a actualtizar.
	 * @param deploymentContent Contingut del fitxer de la definició de procés.
	 */
	@Override
	public void updateDeploymentActions (
			Long jbpmId,
			byte[] deploymentContent) throws Exception {
		ProcessDefinition processDefinition = null;
		try {
			processDefinition = ProcessDefinition.parseParZipInputStream(
					new ZipInputStream(new ByteArrayInputStream(deploymentContent)));
		} catch (Exception ex) {
			throw new HeliumJbpmException("Error parsejant fitxer");
		}

		Map<String, byte[]> handlers = new HashMap<String, byte[]>();
		Map<String, byte[]> bytesMap = processDefinition.getFileDefinition().getBytesMap();
		for (String nom : bytesMap.keySet()) {
			if (nom.endsWith(".class")) {
				handlers.put(nom, bytesMap.get(nom));
			}
		}
		// Omple que command que substitueix els handlers existents
		UpdateHandlersCommand command = new UpdateHandlersCommand(
				jbpmId,
				handlers);
		commandService.execute(command);
	}

	@Override
	public void propagateDeploymentActions(String deploymentOrigenId, String deploymentDestiId) {

		ProcessDefinition processDefinitionOrigen = getDefinicioProces(deploymentOrigenId);
		ProcessDefinition processDefinitionDesti = getDefinicioProces(deploymentDestiId);

		Map<String, byte[]> handlers = new HashMap<String, byte[]>();
		Map<String, byte[]> bytesMap = processDefinitionOrigen.getFileDefinition().getBytesMap();
		for (String nom : bytesMap.keySet()) {
			if (nom.endsWith(".class")) {
				handlers.put(nom, bytesMap.get(nom));
			}
		}

		UpdateHandlersCommand command = new UpdateHandlersCommand(
				processDefinitionDesti.getId(),
				handlers);
		commandService.execute(command);
	}


	// Consulta de Definicions de Procés
	////////////////////////////////////////////////////////////////////////////////

	public WProcessDefinition getProcessDefinition(String processDefinitionId) {
		//adminService.mesuraIniciar("jBPM getProcessDefinition", "jbpmDao");
		ProcessDefinition processDefinition = getDefinicioProces(processDefinitionId);
		if (processDefinition != null)
			return new JbpmProcessDefinition(processDefinition);
		//adminService.mesuraCalcular("jBPM getProcessDefinition", "jbpmDao");
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<WProcessDefinition> getSubProcessDefinitions(String processDefinitionId) {
		//adminService.mesuraIniciar("jBPM getSubProcessDefinitions", "jbpmDao");
		List<WProcessDefinition> resposta = new ArrayList<WProcessDefinition>();
		final long pdid = Long.parseLong(processDefinitionId);
		GetSubProcessDefinitionsCommand command = new GetSubProcessDefinitionsCommand(pdid);
		for (ProcessDefinition pd: (List<ProcessDefinition>)commandService.execute(command)) {
			resposta.add(new JbpmProcessDefinition(pd));
		}
		//adminService.mesuraCalcular("jBPM getSubProcessDefinitions", "jbpmDao");
		return resposta;
	}


	@SuppressWarnings("unchecked")
	public List<String> getTaskNamesFromDeployedProcessDefinition(String deploymentId, String processDefinitionId) {
		//adminService.mesuraIniciar("jBPM getTaskNamesFromDeployedProcessDefinition", "jbpmDao");
		ProcessDefinition pd = getDefinicioProces(processDefinitionId);
		List<String> taskNames = new ArrayList<String>();
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

	// Definicions de procés

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

	/** Cerca els nodes de tipus ProcessState del ProcessDefinitions dp1  i assegura que s'apuntin correctament cap al pd2
	 * si coincideix el nom del subprocés.
	 *
	 * @param pd1
	 * @param pd2
	 */
	@Override
	public void updateSubprocessDefinition(String pd1, String pd2) {

		GetProcessDefinitionByIdCommand command1 = new GetProcessDefinitionByIdCommand(Long.parseLong(pd1));
		ProcessDefinition processDefinition1 = (ProcessDefinition)commandService.execute(command1);
		GetProcessDefinitionByIdCommand command2 = new GetProcessDefinitionByIdCommand(Long.parseLong(pd2));
		ProcessDefinition processDefinition2 = (ProcessDefinition)commandService.execute(command2);

		UpdateSubprocessDefinitionCommand updateCommand = new UpdateSubprocessDefinitionCommand(
				processDefinition1,
				processDefinition2);
		commandService.execute(updateCommand);
	}


	// DEFINICIÓ DE TASQUES
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////


	// INSTÀNCIA DE PROCÉS
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////

	@Override
	@SuppressWarnings("unchecked")
	public List<WProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId) {
		//adminService.mesuraIniciar("jBPM findProcessInstancesWithProcessDefinitionId", "jbpmDao");
		List<WProcessInstance> resultat = new ArrayList<WProcessInstance>();
		final long id = Long.parseLong(processDefinitionId);
		GetProcessInstancesCommand command = new GetProcessInstancesCommand();
		command.setProcessInstanceId(id); // Això està bé, el command agafa setProcessInstanceId com si fos setProcessDefinitionId
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resultat.add(new JbpmProcessInstance(pd));
		}
		//adminService.mesuraCalcular("jBPM findProcessInstancesWithProcessDefinitionId", "jbpmDao");
		return resultat;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WProcessInstance> findProcessInstancesWithProcessDefinitionName(String processName) {
		//adminService.mesuraIniciar("jBPM findProcessInstancesWithProcessDefinitionName", "jbpmDao");
		List<WProcessInstance> resultat = new ArrayList<WProcessInstance>();
		GetProcessInstancesCommand command = new GetProcessInstancesCommand();
		command.setProcessDefinitionName(processName);
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resultat.add(new JbpmProcessInstance(pd));
		}
		//adminService.mesuraCalcular("jBPM findProcessInstancesWithProcessDefinitionName", "jbpmDao");
		return resultat;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(String processName, String entornId) {
		//adminService.mesuraIniciar("jBPM findProcessInstancesWithProcessDefinitionNameAndEntorn", "jbpmDao");
		List<WProcessInstance> resultat = new ArrayList<WProcessInstance>();
		GetProcessInstancesEntornCommand command = new GetProcessInstancesEntornCommand();
		command.setProcessDefinitionName(processName);
		command.setEntornId(Long.parseLong(entornId));
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resultat.add(new JbpmProcessInstance(pd));
		}
		//adminService.mesuraCalcular("jBPM findProcessInstancesWithProcessDefinitionNameAndEntorn", "jbpmDao");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<WProcessInstance> getProcessInstanceTree(
			String rootProcessInstanceId) {

		//adminService.mesuraIniciar("jBPM getProcessInstanceTree", "jbpmDao");
		List<WProcessInstance> resposta = new ArrayList<WProcessInstance>();
		final long id = Long.parseLong(rootProcessInstanceId);
		GetProcessInstancesTreeCommand command = new GetProcessInstancesTreeCommand(id);
		for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
			resposta.add(new JbpmProcessInstance(pd));
		}
		//adminService.mesuraCalcular("jBPM getProcessInstanceTree", "jbpmDao");
		return resposta;
	}

	@Override
	public WProcessInstance getProcessInstance(String processInstanceId) {
		//adminService.mesuraIniciar("jBPM getProcessInstance", "jbpmDao");
		WProcessInstance resposta = null;
		final long piid = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(piid);
		resposta = new JbpmProcessInstance((ProcessInstance)commandService.execute(command));
		//adminService.mesuraCalcular("jBPM getProcessInstance", "jbpmDao");
		return resposta;
	}

	@Override
	public WProcessInstance getRootProcessInstance(
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

	@Override
	@SuppressWarnings("unchecked")
	public List<String> findRootProcessInstances( String actorId,
												  List<String> processInstanceIds,
												  boolean nomesMeves,
												  boolean nomesTasquesPersonals,
												  boolean nomesTasquesGrup) {

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

	// Tramitació
	////////////////////////////////////////////////////////////////////////////////

	@Override
	public WProcessInstance startProcessInstanceById(
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
		WProcessInstance resultat = new JbpmProcessInstance(processInstance);
		//adminService.mesuraCalcular("jBPM startProcessInstanceById", "jbpmDao");
		return resultat;
	}

	@Override
	public void signalProcessInstance(
			String processInstanceId,
			String transitionName) {
		//adminService.mesuraIniciar("jBPM signalProcessInstance", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		SignalProcessInstanceCommand command = new SignalProcessInstanceCommand(id);
		if (transitionName != null)
			command.setStartTransitionName(transitionName);
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM signalProcessInstance", "jbpmDao");
	}

	@Override
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

	@Override
	public void suspendProcessInstances(
			String[] processInstanceIds) {
		//adminService.mesuraIniciar("jBPM suspendProcessInstances", "jbpmDao");
		long[] ids = new long[processInstanceIds.length];
		for (int i = 0; i < processInstanceIds.length; i++)
			ids[i] = Long.parseLong(processInstanceIds[i]);
		SuspendProcessInstancesCommand command = new SuspendProcessInstancesCommand(ids);
		commandHelper.executeCommandWithAutoSave(
				command,
				ids,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM suspendProcessInstances", "jbpmDao");
	}

	@Override
	public void resumeProcessInstances(
			String[] processInstanceIds) {
		//adminService.mesuraIniciar("jBPM resumeProcessInstances", "jbpmDao");
		long[] ids = new long[processInstanceIds.length];
		for (int i = 0; i < processInstanceIds.length; i++)
			ids[i] = Long.parseLong(processInstanceIds[i]);
		ResumeProcessInstancesCommand command = new ResumeProcessInstancesCommand(ids);
		commandHelper.executeCommandWithAutoSave(
				command,
				ids,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM resumeProcessInstances", "jbpmDao");
	}

	@Override
	public void changeProcessInstanceVersion(
			String processInstanceId,
			int newVersion) {
		//adminService.mesuraIniciar("jBPM changeProcessInstanceVersion", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		ChangeProcessInstanceVersionCommand command = new ChangeProcessInstanceVersionCommand(
				id,
				newVersion);
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM changeProcessInstanceVersion", "jbpmDao");
	}


	// VARIABLES DE PROCÉS
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////

	// Consulta de variables
	////////////////////////////////////////////////////////////////////////////////

	@Override
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

	@Override
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

	// Actualització de variables
	////////////////////////////////////////////////////////////////////////////////

	@Override
	public void setProcessInstanceVariable(
			String processInstanceId,
			String varName,
			Object value) {
		//adminService.mesuraIniciar("jBPM setProcessInstanceVariable", "jbpmDao");
		final long id = Long.parseLong(processInstanceId);
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put(varName, value);
		SaveProcessInstanceVariablesCommand command = new SaveProcessInstanceVariablesCommand(id, vars);
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM setProcessInstanceVariable", "jbpmDao");
	}

	@Override
	public void deleteProcessInstanceVariable(String processInstanceId, String varName) {
		//adminService.mesuraIniciar("jBPM deleteProcessInstanceVariable", "jbpmDao");
		//setProcessInstanceVariable(processInstanceId, varName, null);
		final long id = Long.parseLong(processInstanceId);
		DeleteProcessInstanceVariablesCommand command = new DeleteProcessInstanceVariablesCommand(id, new String[] {varName});
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM deleteProcessInstanceVariable", "jbpmDao");
	}


	// INSTÀNCIA DE TASQUES
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////

	@Override
	public WTaskInstance getTaskById(String taskId) {
		//adminService.mesuraIniciar("jBPM getTaskById", "jbpmDao");
		WTaskInstance resposta = null;
		final long id = Long.parseLong(taskId);
		GetTaskInstanceCommand command = new GetTaskInstanceCommand(id);
		TaskInstance ti = (TaskInstance)commandService.execute(command);
		if (ti != null)
			resposta = new JbpmTask(ti);
		//adminService.mesuraCalcular("jBPM getTaskById", "jbpmDao");
		return resposta;
	}

	@Override
	public List<WTaskInstance> findTaskInstancesByProcessInstanceId(String processInstanceId) {
		//adminService.mesuraIniciar("jBPM findTaskInstancesForProcessInstance", "jbpmDao");
		List<WTaskInstance> resultat = new ArrayList<WTaskInstance>();
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		ProcessInstance pi = (ProcessInstance)commandService.execute(command);
		for (TaskInstance ti : pi.getTaskMgmtInstance().getTaskInstances())
			resultat.add(new JbpmTask(ti));
		//adminService.mesuraCalcular("jBPM findTaskInstancesForProcessInstance", "jbpmDao");
		return resultat;
	}

	@Override
	public String getTaskInstanceIdByExecutionTokenId(String tokenId) {
		FindTaskInstanceIdForTokenIdCommand command = new FindTaskInstanceIdForTokenIdCommand(Long.parseLong(tokenId));
		return commandService.execute(command).toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public ResultatConsultaPaginada<WTaskInstance> tascaFindByFiltrePaginat(
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
		return (ResultatConsultaPaginada<WTaskInstance>)commandService.execute(command);
	}

	@Override
	public LlistatIds tascaIdFindByFiltrePaginat(
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

	// Tramitació de tasques
	////////////////////////////////////////////////////////////////////////////////

	@Override
	public void takeTaskInstance(String taskId, String actorId) {
		//adminService.mesuraIniciar("jBPM takeTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		TakeTaskInstanceCommand command = new TakeTaskInstanceCommand(id, actorId);
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM takeTaskInstance", "jbpmDao");
	}

	@Override
	public void releaseTaskInstance(String taskId) {
		//adminService.mesuraIniciar("jBPM releaseTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		ReleaseTaskInstanceCommand command = new ReleaseTaskInstanceCommand(id);
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM releaseTaskInstance", "jbpmDao");
	}

	@Override
	public WTaskInstance startTaskInstance(String taskId) {
		//adminService.mesuraIniciar("jBPM startTaskInstance", "jbpmDao");
		WTaskInstance resposta = null;
		final long id = Long.parseLong(taskId);
		StartTaskInstanceCommand command = new StartTaskInstanceCommand(id);
		resposta = new JbpmTask((TaskInstance)commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA));
		//adminService.mesuraCalcular("jBPM startTaskInstance", "jbpmDao");
		return resposta;
	}

	@Override
	public void endTaskInstance(String taskId, String outcome) {
		//adminService.mesuraIniciar("jBPM endTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		TaskInstanceEndCommand command = new TaskInstanceEndCommand(id, outcome);
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM endTaskInstance", "jbpmDao");
	}

//	@Override
//	public ResultatCompleteTask completeTaskInstance(
//			WTaskInstance task,
//			String outcome) {
//		ResultatCompleteTask resposta = new ResultatCompleteTask();
//		resposta.setCompletat(false);
//		resposta.setSupervisat(false);
//
//		startTaskInstance(task.getId());
//		endTaskInstance(task.getId(), outcome);
//		resposta.setCompletat(true);
//		// Accions per a una tasca delegada
//		DelegationInfo delegationInfo = getDelegationTaskInstanceInfo(task.getId(), false);
//		if (delegationInfo != null) {
//			if (!task.getId().equals(delegationInfo.getSourceTaskId())) {
//				// Copia les variables de la tasca delegada a la original
//				setTaskInstanceVariables(
//						delegationInfo.getSourceTaskId(),
//						getTaskInstanceVariables(task.getId()),
//						false);
//				WTaskInstance taskOriginal = getTaskById(delegationInfo.getSourceTaskId());
//				if (!delegationInfo.isSupervised()) {
//					// Si no es supervisada també finalitza la tasca original
////					completar(entornId, taskOriginal.getId(), false, null, outcome);
//					resposta.setSupervisat(true);
//					resposta.setTascaDelegadaId(taskOriginal.getId());
//				}
//				deleteDelegationInfo(taskOriginal);
//			}
//		}
//		return resposta;
//	}

	@Override
	public WTaskInstance cancelTaskInstance(String taskId) {
		//adminService.mesuraIniciar("jBPM cancelTaskInstance", "jbpmDao");
		WTaskInstance resposta = null;
		final long id = Long.parseLong(taskId);
		CancelTaskInstanceCommand command = new CancelTaskInstanceCommand(id);
		resposta = new JbpmTask((TaskInstance)commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA));
		//adminService.mesuraCalcular("jBPM cancelTaskInstance", "jbpmDao");
		return resposta;
	}

	@Override
	public WTaskInstance suspendTaskInstance(String taskId) {
		//adminService.mesuraIniciar("jBPM suspendTaskInstance", "jbpmDao");
		WTaskInstance resposta = null;
		final long id = Long.parseLong(taskId);
		SuspendTaskInstanceCommand command = new SuspendTaskInstanceCommand(id);
		resposta = new JbpmTask((TaskInstance)commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA));
		//adminService.mesuraCalcular("jBPM suspendTaskInstance", "jbpmDao");
		return resposta;
	}

	@Override
	public WTaskInstance resumeTaskInstance(String taskId) {
		//adminService.mesuraIniciar("jBPM resumeTaskInstance", "jbpmDao");
		WTaskInstance resposta = null;
		final long id = Long.parseLong(taskId);
		ResumeTaskInstanceCommand command = new ResumeTaskInstanceCommand(id);
		resposta = new JbpmTask((TaskInstance)commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA));
		//adminService.mesuraCalcular("jBPM resumeTaskInstance", "jbpmDao");
		return resposta;
	}


//	public WTaskInstance reassignTaskInstance(String taskId, String expression) {
//		//adminService.mesuraIniciar("jBPM reassignTaskInstance", "jbpmDao");
//		WTaskInstance resposta = reassignTaskInstance(taskId, expression, null);
//		//adminService.mesuraCalcular("jBPM reassignTaskInstance", "jbpmDao");
//		return resposta;
//	}

	@Override
	public WTaskInstance reassignTaskInstance(String taskId, String expression, Long entornId) {
		//adminService.mesuraIniciar("jBPM reassignTaskInstance entorn", "jbpmDao");
		WTaskInstance resposta = null;
		final long id = Long.parseLong(taskId);
		ReassignTaskInstanceCommand command = new ReassignTaskInstanceCommand(id);
		command.setExpression(expression);
		command.setEntornId(entornId);
		resposta = new JbpmTask((TaskInstance)commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA));
		//adminService.mesuraCalcular("jBPM reassignTaskInstance entorn", "jbpmDao");
		return resposta;
	}

	@Override
	public void setTaskInstanceActorId(String taskInstanceId, String actorId) {
		//adminService.mesuraIniciar("jBPM setTaskInstanceActorId", "jbpmDao");
		final long id = Long.parseLong(taskInstanceId);
		ReassignTaskInstanceCommand command = new ReassignTaskInstanceCommand(id);
		command.setActorId(actorId);
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM setTaskInstanceActorId", "jbpmDao");
	}

	@Override
	public void setTaskInstancePooledActors(String taskInstanceId, String[] pooledActors) {
		//adminService.mesuraIniciar("jBPM setTaskInstancePooledActors", "jbpmDao");
		final long id = Long.parseLong(taskInstanceId);
		ReassignTaskInstanceCommand command = new ReassignTaskInstanceCommand(id);
		command.setPooledActors(pooledActors);
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM setTaskInstancePooledActors", "jbpmDao");
	}

	// Caché

	@Override
	public void updateTaskInstanceInfoCache(
			String taskId,
			String titol,
			String description) {
		//adminService.mesuraIniciar("jBPM describeTaskInstance", "jbpmDao");
		final long id = Long.parseLong(taskId);
		DescribeTaskInstanceCommand command = new DescribeTaskInstanceCommand(
				id,
				titol,
				description);
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM describeTaskInstance", "jbpmDao");
	}


	// VARIABLES DE TASQUES
	////////////////////////////////////////////////////////////////////////////////

	@Override
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

	@Override
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

	@Override
	public void setTaskInstanceVariable(String taskId, String codi, Object valor) {
		//adminService.mesuraIniciar("jBPM setTaskInstanceVariable", "jbpmDao");
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put(codi, valor);
		setTaskInstanceVariables(taskId, vars, false);
		//adminService.mesuraCalcular("jBPM setTaskInstanceVariable", "jbpmDao");
	}

	@Override
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
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM setTaskInstanceVariables", "jbpmDao");
	}

	@Override
	public void deleteTaskInstanceVariable(String taskId, String varName) {
		//adminService.mesuraIniciar("jBPM deleteTaskInstanceVariable", "jbpmDao");
		//setTaskInstanceVariable(taskId, varName, null);
		final long id = Long.parseLong(taskId);
		DeleteTaskInstanceVariablesCommand command = new DeleteTaskInstanceVariablesCommand(
				id,
				new String[] {varName});
		command.setLocally(true);
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM deleteTaskInstanceVariable", "jbpmDao");
	}


	// FILS D'EXECUCIÓ (Token / Execution path)
	////////////////////////////////////////////////////////////////////////////////

	@Override
	public WToken getTokenById(String tokenId) {
		//adminService.mesuraIniciar("jBPM getTokenById", "jbpmDao");
		final long id = Long.parseLong(tokenId);
		GetTokenByIdCommand command = new GetTokenByIdCommand(id);
		WToken resultat = new JbpmToken((Token)commandService.execute(command));
		//adminService.mesuraCalcular("jBPM getTokenById", "jbpmDao");
		return resultat;
	}

	@Override
	public Map<String, WToken> getActiveTokens(String processInstanceId) {
		//adminService.mesuraIniciar("jBPM getActiveTokens", "jbpmDao");
		Map<String, WToken> resposta = new HashMap<String, WToken>();
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

	public Map<String, WToken> getAllTokens(String processInstanceId) {
		//adminService.mesuraIniciar("jBPM getAllTokens", "jbpmDao");
		Map<String, WToken> resposta = new HashMap<String, WToken>();
		final long id = Long.parseLong(processInstanceId);
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(id);
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		Token root = processInstance.getRootToken();

		getTokenAndChildren(root, resposta);

		//adminService.mesuraCalcular("jBPM getAllTokens", "jbpmDao");
		return resposta;
	}

	private void getTokenAndChildren(Token root, Map<String,WToken> resposta){
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

	@Override
	public void tokenRedirect(
			String tokenId,
			String nodeName,
			boolean cancelTasks,
			boolean enterNodeIfTask,
			boolean executeNode) {
		//adminService.mesuraIniciar("jBPM tokenRedirect", "jbpmDao");
		TokenRedirectCommand command = new TokenRedirectCommand(Long.parseLong(tokenId), nodeName);
		command.setCancelTasks(cancelTasks);
		command.setEnterNodeIfTask(enterNodeIfTask);
		command.setExecuteNode(executeNode);
		commandHelper.executeCommandWithAutoSave(
				command,
				Long.parseLong(tokenId),
				AddToAutoSaveCommand.TIPUS_TOKEN);
		//adminService.mesuraCalcular("jBPM tokenRedirect", "jbpmDao");
	}

	@Override
	public boolean tokenActivar(String tokenId, boolean activar) {
		//adminService.mesuraIniciar("jBPM tokenActivar", "jbpmDao");
		try {
			TokenActivarCommand command = new TokenActivarCommand(Long.parseLong(tokenId), activar);
			commandHelper.executeCommandWithAutoSave(
					command,
					Long.parseLong(tokenId),
					AddToAutoSaveCommand.TIPUS_TOKEN);
			return true;
		} catch (Exception ex) {
			return false;
		} finally {
			//adminService.mesuraCalcular("jBPM tokenActivar", "jbpmDao");
		}
	}

	@Override
	public void signalToken(
			String tokenId,
			String transitionName) {
		//adminService.mesuraIniciar("jBPM signalToken", "jbpmDao");
		SignalCommand command = new SignalCommand(Long.parseLong(tokenId), transitionName);
		commandHelper.executeCommandWithAutoSave(
				command,
				Long.parseLong(tokenId),
				AddToAutoSaveCommand.TIPUS_TOKEN);
		//adminService.mesuraCalcular("jBPM signalToken", "jbpmDao");
	}

	// ACCIONS
	////////////////////////////////////////////////////////////////////////////////

	@Override
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
		resultat = (Map<String,Object>)commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM evaluateScript", "jbpmDao");
		return resultat;
	}

	@Override
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
		Object resultat = commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM evaluateExpression", "jbpmDao");
		return resultat;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object evaluateExpression(
			String expression,
			Class expectedClass,
			final Map<String, Object> context) {

		ExpressionEvaluator evaluator = new ExpressionEvaluatorImpl();
		Object resultat = evaluator.evaluate(
				expression,
				expectedClass,
				new VariableResolver() {
					public Object resolveVariable(String name)
							throws ELException {
						return context.get(name);
					}
				},
				null);

		return resultat;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> listActions(String jbpmId) {
		//adminService.mesuraIniciar("jBPM listActions", "jbpmDao");
		final long id = Long.parseLong(jbpmId);
		List<String> llista = (List<String>)commandService.execute(
				new ListActionsCommand(id));
		//adminService.mesuraCalcular("jBPM listActions", "jbpmDao");
		return llista;
	}

	@Override
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
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM executeActionInstanciaProces", "jbpmDao");
	}

	@Override
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
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA);
		//adminService.mesuraCalcular("jBPM executeActionInstanciaTasca", "jbpmDao");
	}


	// TIMERS
	////////////////////////////////////////////////////////////////////////////////

//	@SuppressWarnings("unchecked")
//	public List<Timer> findTimersWithProcessInstanceId(
//			String processInstanceId) {
//		//adminService.mesuraIniciar("jBPM findTimersWithProcessInstanceId", "jbpmDao");
//		final long id = Long.parseLong(processInstanceId);
//		FindProcessInstanceTimersCommand command = new FindProcessInstanceTimersCommand(id);
//		List<Timer> llista = (List<Timer>)commandService.execute(command);
//		//adminService.mesuraCalcular("jBPM findTimersWithProcessInstanceId", "jbpmDao");
//		return llista;
//	}

	@Override
	public void suspendTimer(
			String timerId,
			Date dueDate) {
		//adminService.mesuraIniciar("jBPM suspendTimer", "jbpmDao");
		SuspendProcessInstanceTimerCommand command = new SuspendProcessInstanceTimerCommand(Long.parseLong(timerId));
		command.setDueDate(dueDate);
		commandService.execute(command);
		//adminService.mesuraCalcular("jBPM suspendTimer", "jbpmDao");
	}

	@Override
	public void resumeTimer(
			String timerId,
			Date dueDate) {
		//adminService.mesuraIniciar("jBPM resumeTimer", "jbpmDao");
		ResumeProcessInstanceTimerCommand command = new ResumeProcessInstanceTimerCommand(Long.parseLong(timerId));
		command.setDueDate(dueDate);
		commandService.execute(command);
		//adminService.mesuraCalcular("jBPM resumeTimer", "jbpmDao");
	}

	// AREES I CARRECS
	////////////////////////////////////////////////////////////////////////////////
	public List<String> findAreesByFiltre(String filtre) {
		FindGrupCommand command = new FindGrupCommand(filtre);
		return (List<String>) commandService.execute(command);
	}

	public List<String> findAreesByPersona(String personaCodi) {
		FindAreesCommand command = new FindAreesCommand(personaCodi);
		return (List<String>) commandService.execute(command);
	}

	public List<String> findRolsByPersona(String personaCodi) {
		FindAreesCommand command = new FindAreesCommand(personaCodi, true);
		return (List<String>) commandService.execute(command);
	}

	public List<String[]> findCarrecsByFiltre(String filtre) {
		FindCarrecCommand command = new FindCarrecCommand(FindCarrecCommand.TipusConsulta.FILTRE, filtre);
		return (List<String[]>) commandService.execute(command);
	}

	public List<String> findPersonesByGrupAndCarrec(String grupCodi, String carrecCodi) {
		FindCarrecCommand command = new FindCarrecCommand(
				FindCarrecCommand.TipusConsulta.PERSONA_AMB_CARREC_I_GRUP,
				null,
				grupCodi,
				carrecCodi);
		return (List<String>) commandService.execute(command);
	}

	public List<String> findCarrecsByPersonaAndGrup(String personaCodi, String grupCodi) {
		FindCarrecCommand command = new FindCarrecCommand(
				FindCarrecCommand.TipusConsulta.CARREC_PER_PERSONA_I_GRUP,
				personaCodi,
				grupCodi,
				null);
		return (List<String>) commandService.execute(command);
	}

	public List<String> findPersonesByCarrec(String carrecCodi) {
		FindCarrecCommand command = new FindCarrecCommand(
				FindCarrecCommand.TipusConsulta.PERSONA_AMB_CARREC,
				null,
				null,
				carrecCodi);
		return (List<String>) commandService.execute(command);
	}

	public List<String> findPersonesByGrup(String grupCodi) {
		FindCarrecCommand command = new FindCarrecCommand(
				FindCarrecCommand.TipusConsulta.PERSONA_AMB_GRUP,
				null,
				grupCodi,
				null);
		return (List<String>) commandService.execute(command);
	}


	// TRANSITIONS
	////////////////////////////////////////////////////////////////////////////////

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
		List<Transition> outcomes = null;
		if (taskInstance.getTask().getTaskNode() != null) {
			outcomes = (List<Transition>)taskInstance.getTask().getTaskNode().getLeavingTransitions();
		}
		if (outcomes != null) {
			for (Transition transition: outcomes)
				resultat.add(transition.getName());
		}
		//adminService.mesuraCalcular("jBPM findTaskInstanceOutcomes", "jbpmDao");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public List<String> findArrivingNodeNames(String tokenId) {
		//adminService.mesuraIniciar("jBPM findArrivingNodeNames", "jbpmDao");
		final long id = Long.parseLong(tokenId);
		FindArrivingNodeNamesCommand command = new FindArrivingNodeNamesCommand(id);
		List<String> resultat = (List<String>)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM findArrivingNodeNames", "jbpmDao");
		return resultat;
	}


	// A MOURE FORA DE LA FUNCIONALITAT DEL MOTOR DE WORKFLOW
	////////////////////////////////////////////////////////////////////////////////

	// Expedients

	public ExpedientDto expedientFindByProcessInstanceId(
			String processInstanceId) {
		GetProcessInstanceCommand command = new GetProcessInstanceCommand(
				Long.parseLong(processInstanceId));
		ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
		ProcessInstanceExpedient pie = processInstance.getExpedient();

		ExpedientDto expedient =  null;
		if (processInstance != null) {
			expedient = processInstanceExpedientToDto(pie);
			expedient.setTipus(processInstanceExpedientTipusToDto(pie.getTipus()));
		}
		return expedient;
	}

	@SuppressWarnings("unchecked")
	public ResultatConsultaPaginada<Long> expedientFindByFiltre(
			Long entornId,
			String actorId,
			Collection<Long> tipusIdPermesos,
			String titol,
			String numero,
			Long tipusId,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Date dataFinalitzacioInici,
			Date dataFinalitzacioFi,
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
				dataFinalitzacioInici,
				dataFinalitzacioFi,
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
		return (ResultatConsultaPaginada<Long>)commandService.execute(command);
	}

	public void finalitzarExpedient(String[] processInstanceIds, Date dataFinalitzacio){
		long[] ids = new long[processInstanceIds.length];
		for (int i = 0; i < processInstanceIds.length; i++)
			ids[i] = Long.parseLong(processInstanceIds[i]);
		ProcessInstanceEndCommand command = new ProcessInstanceEndCommand(ids, dataFinalitzacio);
		commandHelper.executeCommandWithAutoSave(
				command,
				ids,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
	}

	public void desfinalitzarExpedient(String processInstanceId){
		//adminService.mesuraIniciar("jBPM reprendreExpedient", "jbpmDao");

		// Recuperamos el token EndState más reciente
		WProcessInstance rootProcessInstance = getRootProcessInstance(processInstanceId);
		List<WProcessInstance> lista = getProcessInstanceTree(rootProcessInstance.getId());
		Token token = null;
		for (WProcessInstance pi : lista) {
			Map<String, WToken> tokens = getAllTokens(pi.getId());
			for (String tokenName: tokens.keySet()) {
				Token tokenTmp = (Token) tokens.get(tokenName).getToken();
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
			JbpmToken jtoken = (JbpmToken) getTokenById(String.valueOf(token.getId()));
			RevertTokenEndCommand command = new RevertTokenEndCommand(jtoken);
			commandHelper.executeCommandWithAutoSave(
					command,
					token.getId(),
					AddToAutoSaveCommand.TIPUS_TOKEN);
			token = token.getParent();
		}

		// Activamos la instancia de proceso
		revertProcessInstanceEnd(Long.parseLong(rootProcessInstance.getId()));

		//adminService.mesuraCalcular("jBPM reprendreExpedient", "jbpmDao");
	}


	// Tasques en segón pla

	//Marcar tasca pendent de finalitzar en segón pla
	@Override
	public void marcarFinalitzar(String taskId, Date marcadaFinalitzar, String outcome, String rols) {
		final long id = Long.parseLong(taskId);
		MarcarFinalitzarCommand command = new MarcarFinalitzarCommand(id, marcadaFinalitzar, outcome, rols);
		commandService.execute(command);
	}

	//Marcar tasca marcada en segon pla com "en execució"
	@Override
	public void marcarIniciFinalitzacioSegonPla(String taskId, Date iniciFinalitzacio) {
		final long id = Long.parseLong(taskId);
		MarcarIniciFinalitzacioSegonPlaCommand command = new MarcarIniciFinalitzacioSegonPlaCommand(id, iniciFinalitzacio);
		commandService.execute(command);
	}

	//Guardar l'error de finalitzacio a BBDD
	@Override
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

	@Override
	@SuppressWarnings("unchecked")
	public List<Object[]> getTasquesSegonPlaPendents() {
		GetTasquesSegonPlaPendentsIdsCommand command = new GetTasquesSegonPlaPendentsIdsCommand();
		return (List<Object[]>)commandService.execute(command);
	}


	// Eliminació de definicions de procés

	@Override
	@SuppressWarnings("unchecked")
	public List<String> findDefinicionsProcesIdNoUtilitzadesByEntorn(Long entornId) {
		List<String> resultat = new ArrayList<String>();

		GetProcesDefinitionEntornNotUsedListCommand command = new GetProcesDefinitionEntornNotUsedListCommand(entornId);
		for (ProcessDefinition pd : (List<ProcessDefinition>)commandService.execute(command))
			resultat.add(String.valueOf(pd.getId()));
		//adminService.mesuraCalcular("jBPM findGroupTasks", "jbpmDao");
		return resultat;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(Long expedientTipusId) {
		List<String> resultat = new ArrayList<String>();

		GetProcesDefinitionNotUsedListCommand command = new GetProcesDefinitionNotUsedListCommand(expedientTipusId);
		for (ProcessDefinition pd : (List<ProcessDefinition>)commandService.execute(command))
			resultat.add(String.valueOf(pd.getId()));
		//adminService.mesuraCalcular("jBPM findGroupTasks", "jbpmDao");
		return resultat;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ExpedientDto> findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
			Long expedientTipusId,
			Long processDefinitionId) {
		GetExpedientsAfectatsListCommand command = new GetExpedientsAfectatsListCommand(expedientTipusId, processDefinitionId);
		List<ProcessInstanceExpedient> processInstanceExpedients = (List<ProcessInstanceExpedient>)commandService.execute(command);

		List<ExpedientDto> expedientsDto = new ArrayList<ExpedientDto>();
		for (ProcessInstanceExpedient pie : processInstanceExpedients) {
			expedientsDto.add(processInstanceExpedientToDto(pie));
		}
		return expedientsDto;
	}


	@Override
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
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM retrocedirAccio", "jbpmDao");
	}

	@Override
	public WProcessDefinition parse(ZipInputStream zipInputStream) throws Exception {
		ProcessDefinition processDefinition = ProcessDefinition.parseParZipInputStream(zipInputStream);
		return new JbpmProcessDefinition(processDefinition);
	}
















//	public JbpmTask cloneTaskInstance(String taskId, String actorId, Map<String, Object> variables) {
//		//adminService.mesuraIniciar("jBPM cloneTaskInstance", "jbpmDao");
//		JbpmTask resposta = null;
//		final long id = Long.parseLong(taskId);
//		CloneTaskInstanceCommand command = new CloneTaskInstanceCommand(
//				id,
//				actorId,
//				false);
//		command.setVariables(variables);
//		resposta = new JbpmTask((TaskInstance)commandService.execute(command));
//		//adminService.mesuraCalcular("jBPM cloneTaskInstance", "jbpmDao");
//		return resposta;
//	}
//
//	public void suspendProcessInstance(
//			String processInstanceId) {
//		//adminService.mesuraIniciar("jBPM suspendProcessInstance", "jbpmDao");
//		suspendProcessInstances(new String[] {processInstanceId});
//		//adminService.mesuraCalcular("jBPM suspendProcessInstance", "jbpmDao");
//	}
//
//	public void resumeProcessInstance(
//			String processInstanceId) {
//		//adminService.mesuraIniciar("jBPM resumeProcessInstance", "jbpmDao");
//		resumeProcessInstances(new String[] {processInstanceId});
//		//adminService.mesuraCalcular("jBPM resumeProcessInstance", "jbpmDao");
//	}



//	public void describeProcessInstance(
//			String processInstanceId,
//			String description) {
//		//adminService.mesuraIniciar("jBPM describeProcessInstance", "jbpmDao");
//		final long id = Long.parseLong(processInstanceId);
//		DescribeProcessInstanceCommand command = new DescribeProcessInstanceCommand(id, description);
//		commandHelper.executeCommandWithAutoSave(
//				command,
//				id,
//				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
//		//adminService.mesuraCalcular("jBPM describeProcessInstance", "jbpmDao");
//	}

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
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM cancelProcessInstance", "jbpmDao");
	}


	public void revertProcessInstanceEnd(long id) {
		//adminService.mesuraIniciar("jBPM revertProcessInstanceEnd", "jbpmDao");
		RevertProcessInstanceEndCommand command = new RevertProcessInstanceEndCommand(id);
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
		//adminService.mesuraCalcular("jBPM revertProcessInstanceEnd", "jbpmDao");
	}


	public void cancelToken(long id) {
		//adminService.mesuraIniciar("jBPM cancelToken", "jbpmDao");
		CancelTokenCommand command = new CancelTokenCommand(id);
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_TOKEN);
		//adminService.mesuraCalcular("jBPM cancelToken", "jbpmDao");
	}


	public void revertTokenEnd(long id) {
		//adminService.mesuraIniciar("jBPM revertTokenEnd", "jbpmDao");
		JbpmToken jtoken = (JbpmToken) getTokenById(String.valueOf(id));
		RevertTokenEndCommand command = new RevertTokenEndCommand(jtoken);
		commandHelper.executeCommandWithAutoSave(
				command,
				id,
				AddToAutoSaveCommand.TIPUS_TOKEN);
//		this.sessionFactory.getCurrentSession().refresh(jtoken.getToken());
		jtoken.getToken().setAbleToReactivateParent(true);
		//adminService.mesuraCalcular("jBPM revertTokenEnd", "jbpmDao");
	}


	public WTaskInstance findEquivalentTaskInstance(long tokenId, long taskInstanceId) {
		//adminService.mesuraIniciar("jBPM findEquivalentTaskInstance", "jbpmDao");
		GetTaskInstanceCommand commandGetTask = new GetTaskInstanceCommand(taskInstanceId);
		TaskInstance ti = (TaskInstance)commandService.execute(commandGetTask);
		FindTaskInstanceForTokenAndTaskCommand command = new FindTaskInstanceForTokenAndTaskCommand(tokenId, ti.getTask().getName());
		WTaskInstance resultat = new JbpmTask((TaskInstance)commandService.execute(command));
		if (resultat.getTaskInstance() == null)
			((JbpmTask)resultat).setTaskInstance(ti);
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

	public Action getActionById(long nodeId) {
		//adminService.mesuraIniciar("jBPM getNodeByName", "jbpmDao");
		GetActionByIdCommand command = new GetActionByIdCommand(nodeId);
		Action action = (Action)commandService.execute(command);
		return action;
	}

	public boolean hasStartBetweenLogs(long begin, long end, long taskInstanceId) {
		//adminService.mesuraIniciar("jBPM hasStartBetweenLogs", "jbpmDao");
		HasStartBetweenLogsCommand command = new HasStartBetweenLogsCommand(begin, end, taskInstanceId);
		Boolean hasStart = (Boolean)commandService.execute(command);
		//adminService.mesuraCalcular("jBPM hasStartBetweenLogs", "jbpmDao");
		return hasStart.booleanValue();
	}


//	@SuppressWarnings("unchecked")
//	public List<JbpmTask> findTasks( // 3.0 i 2.6
//			List<Long> ids) {
//		//adminService.mesuraIniciar("jBPM findTasks ids", "jbpmDao");
//		List<JbpmTask> resultat = new ArrayList<JbpmTask>();
//		GetTaskListCommand command = new GetTaskListCommand(ids);
//		for (TaskInstance ti: (List<TaskInstance>)commandService.execute(command)) {
//			resultat.add(new JbpmTask(ti));
//		}
//		//adminService.mesuraCalcular("jBPM findTasks ids", "jbpmDao");
//		return resultat;
//	}

	private ProcessDefinition getDefinicioProces(String processDefinitionId) {
		final long pdid = Long.parseLong(processDefinitionId);
		GetProcessDefinitionByIdCommand command = new GetProcessDefinitionByIdCommand(pdid);
		ProcessDefinition processDefinition = (ProcessDefinition)commandService.execute(command);
		return processDefinition;
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

	/* Mètode privat per transformar una instancia de procés d'expedient en un dto d'expedient.
	 *
	 * @param pie
	 * @return
	 */
	private ExpedientDto processInstanceExpedientToDto(ProcessInstanceExpedient pie) {
		ExpedientDto exp = new ExpedientDto();
		exp.setId(pie.getId());
		exp.setTitol(pie.getTitol());
		exp.setNumero(pie.getNumero());
		exp.setNumeroDefault(pie.getNumeroDefault());
		exp.setDataInici(pie.getDataInici());
		exp.setDataFi(pie.getDataFi());
		exp.setProcessInstanceId(pie.getProcessInstanceId());
		return exp;
	}

	/* Mètode privat per transformar una instancia de procés de tipus d'expedient a un dto de tipus d'expedient
	 *
	 * @param piet
	 * @return
	 */
	private ExpedientTipusDto processInstanceExpedientTipusToDto(ProcessInstanceExpedientTipus piet) {
		ExpedientTipusDto tipus = null;
		if (piet != null) {
			tipus = new ExpedientTipusDto();
			tipus.setId(piet.getId());
			tipus.setCodi(piet.getCodi());
			tipus.setNom(piet.getNom());
			tipus.setTeTitol(piet.isTeTitol());
			tipus.setTeNumero(piet.isTeNumero());
		}
		return tipus;
	}
}
