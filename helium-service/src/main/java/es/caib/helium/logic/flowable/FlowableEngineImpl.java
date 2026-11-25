package es.caib.helium.logic.flowable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import es.caib.helium.bpmn.engine.*;
import es.caib.helium.commons.dto.PersonaDto;
import es.caib.helium.commons.dto.TascaOutcomeDto;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.logic.helper.ExpedientTipusRecursHelper;
import es.caib.helium.logic.helper.PluginHelper;
import groovy.lang.GroovyShell;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.api.io.InputStreamProvider;
import org.flowable.common.engine.impl.context.Context;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.common.engine.impl.util.io.BytesStreamSource;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.TaskServiceImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.migration.ProcessInstanceMigrationValidationResult;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.TaskHelper;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;

/**
 * Implementació de l'API del WorkflowEngine pel motor BPMN 2.0. Flowable
 */
@Component
public class FlowableEngineImpl implements WorkflowEngineApi {

	/** Motiu de fi (deleteReason) amb què queden a l'historial les tasques cancel·lades des de Helium. */
	public static final String MOTIU_TASCA_CANCELADA = "helium:tascaCancelada";
	/** Motiu amb què s'aturen les instàncies de procés d'un expedient que s'esborra. */
	public static final String MOTIU_EXPEDIENT_ESBORRAT = "helium:expedientEsborrat";

	@Autowired
	private ProcessEngine processEngine;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ExpedientTipusRecursHelper expedientTipusRecursHelper;

	@Override
	public WProcessDefinition desplegar(String nomArxiu, byte[] contingut, boolean isJar) {
		Deployment deployment;
		if (isJar) {
			deployment = processEngine.
				getRepositoryService().
				createDeployment().
				addZipInputStream(new ZipInputStream(new ByteArrayInputStream(contingut))).
				deploy();
		} else {
			deployment = processEngine.
				getRepositoryService().
				createDeployment().
				addInputStream(nomArxiu, new ByteArrayInputStream(contingut)).
				deploy();
		}
		ProcessDefinition pd = processEngine.
			getRepositoryService().
			createProcessDefinitionQuery().
			deploymentId(deployment.getId()).
			singleResult();
		WProcessDefinition ret = toWProcessDefinition(pd);
		return ret;
	}

	public String getXml(String deploymentId) throws IOException {
		ProcessDefinition processDefinition = processEngine.getRepositoryService().
			getProcessDefinition(deploymentId);
		InputStream is = processEngine.getRepositoryService().getProcessModel(processDefinition.getId());
		return new String(is.readAllBytes(), StandardCharsets.UTF_8);
	}

	@Override
	public WDeployment getDesplegament(String deploymentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void esborrarDesplegament(String deploymentId) {
		ProcessDefinition wpd = processEngine.
			getRepositoryService().
			getProcessDefinition(deploymentId);
		processEngine.
			getRepositoryService().
			deleteDeployment(wpd.getDeploymentId());
	}

	@Override
	public Set<String> getResourceNames(String deploymentId) {
		List<String> names = processEngine.
			getRepositoryService().
			getDeploymentResourceNames(deploymentId);
		return new HashSet<>(names);
	}

	@Override
	public byte[] getResourceBytes(String deploymentId, String resourceName) throws IOException {
		try {
			InputStream is = processEngine
				.getRepositoryService()
				.getResourceAsStream(deploymentId, resourceName);
			return is.readAllBytes();
		} catch (FlowableObjectNotFoundException e) {
			ProcessDefinition wpd = processEngine.getRepositoryService()
				.createProcessDefinitionQuery()
				.deploymentId(deploymentId)
				.singleResult();
			return expedientTipusRecursHelper.loadResource(wpd.getId(), resourceName);
		}
	}

	@Override
	public void updateDeploymentActions(String deploymentId, String deploymentFileName, byte[] deploymentFileContent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateDeploymentActions(String deploymentOrigenId, String deploymentDestiId) {
		// TODO Auto-generated method stub

	}

	@Override
	public WProcessDefinition getProcessDefinition(String processDefinitionId) {
		ProcessDefinition pd =
				processEngine
				.getRepositoryService()
		            .createProcessDefinitionQuery()
		            .processDefinitionId(processDefinitionId)
		            .singleResult();
		return toWProcessDefinition(pd);
	}

	@Override
	public List<WProcessDefinition> getSubProcessDefinitions(String processDefinitionId) {
		List<WProcessDefinition> subprocessos = new ArrayList<>();
		BpmnModel model = processEngine
							.getRepositoryService()
								.getBpmnModel(processDefinitionId);
		Process process = model.getMainProcess();
		this.cercarSubprocessos(process.getFlowElements(), subprocessos);
		return subprocessos;
	}

	/** Funció recursiva per cercar subprocessos.
	 */
	private void cercarSubprocessos(Collection<FlowElement> elements, List<WProcessDefinition> subprocessos) {

		for (FlowElement element : elements) {
		    if (element instanceof CallActivity) {
		        CallActivity call = (CallActivity) element;
		        ProcessDefinition pd = processEngine
		        						.getRepositoryService()
						        	        .createProcessDefinitionQuery()
						        	        .processDefinitionKey(call.getCalledElement())
						        	        .latestVersion()
						        	        .singleResult();
		        subprocessos.add(toWProcessDefinition(pd));
		    } else if (element instanceof SubProcess) {
		    	cercarSubprocessos(((SubProcess) element).getFlowElements(), subprocessos);
            }
		}
	}



	@Override
	public List<WUserTask> getUserTasksFromDeployedProcessDefinition(String processKey, Integer version) {
		ProcessDefinition processDefinition =
			processEngine
				.getRepositoryService()
					.createProcessDefinitionQuery()
					.processDefinitionKey(processKey)
					.processDefinitionVersion(version)
					.singleResult();
	    BpmnModel model =
	    		processEngine
	    			.getRepositoryService()
	    				.getBpmnModel(processDefinition.getId());
		Process process = model.getMainProcess();

		List<WUserTask> userTasks = new ArrayList<>();
	    for (FlowElement element : process.getFlowElements()) {
			if (element instanceof SubProcess) {
				for (FlowElement subElement : ((SubProcess)element).getFlowElements()) {
					if (subElement instanceof UserTask) {
						UserTask userTask = (UserTask) subElement;
						WUserTask wUserTask = new WUserTask();
						wUserTask.setId(userTask.getId());
						wUserTask.setName(userTask.getName());
						userTasks.add(wUserTask);
					}
				}
			} else if (element instanceof UserTask) {
	            UserTask userTask = (UserTask) element;
	            WUserTask wUserTask = new WUserTask();
	            wUserTask.setId(userTask.getId());
	            wUserTask.setName(userTask.getName());
	            userTasks.add(wUserTask);
	        }
	    }
		return userTasks;
	}

	@Override
	public String getStartTaskName(String processDefinitionId) {
		BpmnModel bpmnModel = processEngine
								.getRepositoryService()
									.getBpmnModel(processDefinitionId);
		return this.getStartTaskName(bpmnModel);
	}

	/** Cerca en el model BPMN la primera tasca d'usuari després de l'inici. */
	private String getStartTaskName(BpmnModel bpmnModel) {
		String startTaskName = null;
		UserTask tascaInicial = null;
		for (StartEvent startEvent
				: bpmnModel.getMainProcess().findFlowElementsOfType(StartEvent.class)) {
	        for (SequenceFlow sequenceFlow : startEvent.getOutgoingFlows()) {
	            FlowElement target =
	                bpmnModel.getFlowElement(sequenceFlow.getTargetRef());
	            if (target instanceof UserTask) {
	                tascaInicial = (UserTask) target;
	                break;
	            }
	        }
	        if (tascaInicial != null) {
	        	startTaskName = tascaInicial.getId();
	        	break;
	        }
	    }
		return startTaskName;
	}

	@Override
	public WProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) {
		if (processInstanceId == null) {
			return null;
		}
		// getProcessInstance també troba les instàncies finalitzades (historial)
		WProcessInstance processInstance = getProcessInstance(processInstanceId);
		if (processInstance == null) {
			return null;
		}
		return getProcessDefinition(processInstance.getProcessDefinitionId());
	}

	@Override
	public void updateSubprocessDefinition(WProcessDefinition pd1, WProcessDefinition pd2) {
		// TODO Auto-generated method stub
	}

	@Override
	public long countProcessInstancesWithProcessDefinitionId(String processDefinitionId) {
		return processEngine
					.getRuntimeService()
						.createProcessInstanceQuery()
						.processDefinitionId(processDefinitionId)
						.count();
	}

	@Override
	public List<WProcessInstance> findProcessInstancesWithProcessDefinitionName(String processName) {
		List<WProcessInstance> wPis = new ArrayList<>();
		List<ProcessInstance> instancies = processEngine
				.getRuntimeService()
			    .createProcessInstanceQuery()
			    .processDefinitionKey(processName)
			    .list();
		for (ProcessInstance pi : instancies) {
			wPis.add(toWProcessInstance(pi));
		}
		return wPis;
	}

	@Override
	public List<WProcessInstance> getProcessInstanceTree(String rootProcessInstanceId) {
		List<WProcessInstance> wPis = new ArrayList<>();
		WProcessInstance processInstance = getProcessInstance(rootProcessInstanceId);
		if(processInstance != null)
			wPis.add(processInstance);
		getProcessInstanceTreeRecursively(rootProcessInstanceId, wPis);
		return wPis;
	}

	private void getProcessInstanceTreeRecursively(String rootProcessInstanceId, List<WProcessInstance> wPis) {
		List<HistoricProcessInstance> subprocessos =
				this.processEngine
						.getHistoryService()
							.createHistoricProcessInstanceQuery()
								.superProcessInstanceId(rootProcessInstanceId)
									.list();
		for (HistoricProcessInstance pi : subprocessos) {
			wPis.add(toWProcessInstance(pi));
			this.getProcessInstanceTreeRecursively(pi.getId(), wPis);
		}
	}

	@Override
	public WProcessInstance getProcessInstance(String processInstanceId) {
		ProcessInstance pi = processEngine
								.getRuntimeService()
									.createProcessInstanceQuery()
									.processInstanceId(processInstanceId)
									.singleResult();

		if(pi == null) {
			HistoricProcessInstance hpi = processEngine.getHistoryService()
				.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId)
				.singleResult();
			return toWProcessInstance(hpi);
		}

		return toWProcessInstance(pi);
	}

	@Override
	public WProcessInstance getRootProcessInstance(String processInstanceId) {
		HistoricProcessInstance pi;
		do {
			pi = processEngine
				.getHistoryService()
				.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId)
				.singleResult();
			processInstanceId = pi != null ? pi.getSuperProcessInstanceId() : null;
		} while (processInstanceId != null);
		return toWProcessInstance(pi);
	}

	@Override
	public List<String> findRootProcessInstances(String actorId, List<String> processInstanceIds, boolean nomesMeves,
			boolean nomesTasquesPersonals, boolean nomesTasquesGrup) {
		// TODO Auto-generated method stub
		return null;
	}

	public WProcessInstance findRootProcessInstance(String processInstanceId) {
		if (processInstanceId == null) {
			return null;
		}
		// S'executa com a comanda de Flowable: si ja n'hi ha una en curs (p. ex. un handler que s'executa mentre
		// s'inicia el procés) en reutilitza el context, i els findById consulten primer la memòria cau de la sessió.
		// Així es troben també les instàncies que encara no s'han desat a la base de dades.
		return processEngine.getManagementService().executeCommand(commandContext -> {
			ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);
			ExecutionEntity execution = executionEntityManager.findById(processInstanceId);
			if (execution != null) {
				String rootId = execution.getRootProcessInstanceId() != null
					? execution.getRootProcessInstanceId()
					: execution.getProcessInstanceId();
				ExecutionEntity root = rootId.equals(execution.getId())
					? execution
					: executionEntityManager.findById(rootId);
				if (root != null) {
					return toWProcessInstance((ProcessInstance) root);
				}
			}
			// Instància finalitzada: es puja per l'historial fins a la instància sense pare
			HistoricProcessInstanceEntityManager historicManager =
				CommandContextUtil.getHistoricProcessInstanceEntityManager(commandContext);
			HistoricProcessInstanceEntity historic = historicManager.findById(processInstanceId);
			for (int nivell = 0; historic != null && historic.getSuperProcessInstanceId() != null && nivell < 100; nivell++) {
				HistoricProcessInstanceEntity pare = historicManager.findById(historic.getSuperProcessInstanceId());
				if (pare == null) {
					break;
				}
				historic = pare;
			}
			return toWProcessInstance(historic);
		});
	}

	@Override
	public Long findExpedientIdByProcessInstanceId(String processInstanceId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WProcessInstance startProcessInstanceById(
		String actorId,
		String processDefinitionId) {
		return startProcessInstanceById(actorId, processDefinitionId, null);
	}

	@Override
	public WProcessInstance startProcessInstanceById(
		String actorId,
		String processDefinitionId,
		String processInstanceId) {
		try {
			Authentication.setAuthenticatedUserId(actorId);
			ProcessInstanceBuilder builder = processEngine
				.getRuntimeService()
				.createProcessInstanceBuilder()
				.processDefinitionId(processDefinitionId);
			if (processInstanceId != null) {
				builder.predefineProcessInstanceId(processInstanceId);
			}
			return toWProcessInstance(builder.start());
		} finally {
			Authentication.setAuthenticatedUserId(null);
		}
	}

	@Override
	public void signalProcessInstance(String processInstanceId, String transitionName) {
		try {
			List<Task> tasks = processEngine
										.getTaskService()
										.createTaskQuery()
										.processInstanceId(processInstanceId)
										.list();

			processEngine.getTaskService().bulkSaveTasks(tasks);
		} finally {

		}

	}

	/**
	 * Esborra la instància de procés: si està en curs l'atura (Flowable també esborra les instàncies dels seus
	 * subprocessos, les tasques i els timers) i n'esborra l'historial, com feia jBPM amb
	 * {@code GraphSession.deleteProcessInstance}. Si la instància ja no existeix no fa res, perquè l'esborrat de
	 * l'expedient recorre tot l'arbre d'instàncies i en esborrar el pare ja s'han esborrat els fills.
	 */
	@Override
	public void deleteProcessInstance(String processInstanceId) {
		if (processInstanceId == null) {
			return;
		}
		ProcessInstance pi = processEngine.getRuntimeService()
			.createProcessInstanceQuery()
			.processInstanceId(processInstanceId)
			.singleResult();
		if (pi != null) {
			processEngine.getRuntimeService().deleteProcessInstance(processInstanceId, MOTIU_EXPEDIENT_ESBORRAT);
		}
		HistoricProcessInstance hpi = processEngine.getHistoryService()
			.createHistoricProcessInstanceQuery()
			.processInstanceId(processInstanceId)
			.singleResult();
		if (hpi != null) {
			processEngine.getHistoryService().deleteHistoricProcessInstance(processInstanceId);
		}
	}

	/**
	 * Suspèn les instàncies de procés en curs: Flowable suspèn les seves execucions, tasques i jobs (els timers no
	 * es disparen). Les instàncies finalitzades o ja suspeses s'ignoren.
	 */
	@Override
	public void suspendProcessInstances(String[] processInstanceIds) {
		for (String processInstanceId : processInstanceIds) {
			ProcessInstance pi = getProcessInstanceEnCurs(processInstanceId);
			if (pi != null && !pi.isSuspended()) {
				processEngine.getRuntimeService().suspendProcessInstanceById(processInstanceId);
			}
		}
	}

	/** Reactiva les instàncies de procés suspeses. Les finalitzades o actives s'ignoren. */
	@Override
	public void resumeProcessInstances(String[] processInstanceIds) {
		for (String processInstanceId : processInstanceIds) {
			ProcessInstance pi = getProcessInstanceEnCurs(processInstanceId);
			if (pi != null && pi.isSuspended()) {
				processEngine.getRuntimeService().activateProcessInstanceById(processInstanceId);
			}
		}
	}

	private ProcessInstance getProcessInstanceEnCurs(String processInstanceId) {
		if (processInstanceId == null) {
			return null;
		}
		return processEngine.getRuntimeService()
			.createProcessInstanceQuery()
			.processInstanceId(processInstanceId)
			.singleResult();
	}

	@Override
	public void changeProcessInstanceVersion(String processInstanceId, int newVersion) {
		HistoricProcessInstance hpi = processEngine.getHistoryService()
			.createHistoricProcessInstanceQuery()
			.processInstanceId(processInstanceId)
			.singleResult();
		ProcessInstanceMigrationValidationResult result = processEngine
									.getProcessMigrationService()
									.createProcessInstanceMigrationBuilder()
									.migrateToProcessDefinition(hpi.getProcessDefinitionKey(), newVersion)
									.validateMigration(processInstanceId);
		if (result.isMigrationValid()) {
			processEngine
				.getProcessMigrationService()
				.createProcessInstanceMigrationBuilder()
				.migrateToProcessDefinition(hpi.getProcessDefinitionKey(), newVersion)
				.migrate(processInstanceId);
		}
	}

	@Override
	public Map<String, Object> getProcessInstanceVariables(String processInstanceId) {
		try {
			return processEngine
				.getRuntimeService()
				.getVariables(processInstanceId);
		} catch (FlowableObjectNotFoundException e) {
			List<HistoricVariableInstance> variables = processEngine
				.getHistoryService()
				.createHistoricVariableInstanceQuery()
				.executionId(processInstanceId)
				.list();
			Map<String, Object> response = new HashMap<String, Object>();
			for (HistoricVariableInstance variable : variables)
				response.put(variable.getVariableName(), variable.getValue());
			return response;
		}
	}

	@Override
	public Object getProcessInstanceVariable(String processInstanceId, String varName) {
		if(varName == null || processInstanceId == null)
			return null;
		return this.processEngine
				.getRuntimeService()
				.getVariable(processInstanceId, varName);
	}

	@Override
	public void setProcessInstanceVariable(String processInstanceId, String varName, Object value) {
		this.processEngine
			.getRuntimeService()
			.setVariable(processInstanceId, varName, value);
	}

	public void setProcessInstanceVariables(
		String processInstanceId,
		Map<String, Object> variables) {
		this.processEngine
			.getRuntimeService()
			.setVariables(processInstanceId, variables);
	}

	@Override
	public void deleteProcessInstanceVariable(String processInstanceId, String varName) {
		this.processEngine
			.getRuntimeService()
			.removeVariable(processInstanceId, varName);
	}

	@Override
	public WTaskInstance getTaskById(String taskId) {
		WTaskInstance task = toWTaskInstance(processEngine
				.getHistoryService()
				.createHistoricTaskInstanceQuery()
				.taskId(taskId)
				.includeIdentityLinks()
				.singleResult());
		if (task != null && task.getEndTime() == null) {
			// L'historial no guarda la suspensió (tasca o procés suspès): es consulta la tasca en curs
			Task enCurs = processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
			task.setSuspended(enCurs != null && enCurs.isSuspended());
		}
		return task;
	}

	@Override
	public List<WTaskInstance> findTaskInstanceByProcessInstanceId(String processInstanceId) {
		List<Task> tasks = this.processEngine
			.getTaskService()
			.createTaskQuery()
			.processInstanceId(processInstanceId)
			.includeIdentityLinks()
			.list();
		return tasks.stream().map(FlowableEngineImpl::toWTaskInstance).collect(Collectors.toList());
	}

	@Override
	public WTaskInstance findTaskInstanceByProcessInstanceIdAndName(String processInstanceId, String taskName) {
		Task task = this.processEngine
			.getTaskService()
			.createTaskQuery()
			.processInstanceId(processInstanceId)
			.taskDefinitionKey(taskName)
			.includeIdentityLinks()
			.singleResult();
		return toWTaskInstance(task);
	}

	@Override
	public String getTaskInstanceIdByExecutionTokenId(String executionTokenId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WTaskInstance takeTaskInstance(String taskId, String actorId) {
		processEngine
			.getTaskService()
			.setAssignee(taskId, actorId);
		return getTaskById(taskId);
	}

	@Override
	public WTaskInstance releaseTaskInstance(String taskId) {

		List<IdentityLink> il = processEngine
			.getTaskService()
			.getIdentityLinksForTask(taskId);
//		if(il.size() == 1) {
//			IdentityLink idLink = il.get(0);
//			processEngine.getTaskService().deleteCandidateUser(taskId, idLink.getUserId());
//			//processEngine.getTaskService().deleteUserIdentityLink(taskId, idLink.getUserId(), idLink.getType());
//		}
		processEngine
			.getTaskService()
			.unclaim(taskId);
		processEngine
			.getTaskService()
			.setAssignee(taskId, null);
		return getTaskById(taskId);
	}

	@Override
	public WTaskInstance startTaskInstance(String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Completa la tasca. Si s'indica {@code outcome}, el procés continua només per aquesta transició (l'id
	 * del <i>sequence flow</i> de sortida o, per compatibilitat, l'id de l'activitat destí), tal com feia
	 * jBPM 3. Sense {@code outcome}, Flowable segueix els fluxos de sortida habituals (condicions incloses).
	 *
	 * <p>Les variables es passen com a transitòries: les dades de l'expedient es desen a ExpedientDades,
	 * no al motor, però així són visibles a les expressions que s'avaluen en completar la tasca.</p>
	 */
	@Override
	public void endTaskInstance(String taskId, String outcome, Map<String, Object> variables) {
		if (outcome == null) {
			processEngine
				.getTaskService()
				.complete(taskId, null, variables);
			return;
		}
		processEngine.getManagementService().executeCommand(commandContext -> {
			Task task = processEngine
				.getTaskService()
				.createTaskQuery()
				.taskId(taskId)
				.singleResult();
			if (task == null) {
				throw new FlowableObjectNotFoundException("No s'ha trobat la tasca activa " + taskId, Task.class);
			}
			SequenceFlow transicio = cercarTransicioSortida(
				task.getProcessDefinitionId(),
				task.getTaskDefinitionKey(),
				outcome);
			if (transicio == null) {
				throw new FlowableIllegalArgumentException(
					"La tasca '" + task.getTaskDefinitionKey() + "' no té la transició de sortida '" + outcome + "'");
			}
			// L'agenda de Helium farà que, en sortir de la tasca, només se segueixi aquesta transició
			HeliumFlowableEngineAgenda.seleccionarTransicio(commandContext, task.getExecutionId(), transicio);
			processEngine
				.getTaskService()
				.complete(taskId, null, variables);
			return null;
		});
	}

	/**
	 * Cerca el flux de sortida d'una activitat a partir del seu id o, si no n'hi ha cap, de l'id de
	 * l'activitat destí (format de les transicions anterior).
	 */
	private SequenceFlow cercarTransicioSortida(String processDefinitionId, String activityId, String outcome) {
		FlowElement element = processEngine
			.getRepositoryService()
			.getBpmnModel(processDefinitionId)
			.getFlowElement(activityId);
		if (!(element instanceof FlowNode)) {
			return null;
		}
		List<SequenceFlow> sortides = ((FlowNode) element).getOutgoingFlows();
		for (SequenceFlow sortida : sortides) {
			if (outcome.equals(sortida.getId())) {
				return sortida;
			}
		}
		for (SequenceFlow sortida : sortides) {
			if (outcome.equals(sortida.getTargetRef())) {
				return sortida;
			}
		}
		return null;
	}

	/**
	 * Cancel·la la tasca sense fer avançar el procés, com feia Helium 3 amb jBPM ({@code setSignalling(false)}
	 * + {@code cancel()}): la tasca s'acaba a l'historial amb el motiu {@link #MOTIU_TASCA_CANCELADA} i
	 * l'execució es queda aturada a l'activitat de la tasca.
	 */
	@Override
	public WTaskInstance cancelTaskInstance(String taskId) {
		processEngine.getManagementService().executeCommand(commandContext -> {
			TaskEntity task = CommandContextUtil
				.getProcessEngineConfiguration(commandContext)
				.getTaskServiceConfiguration()
				.getTaskService()
				.getTask(taskId);
			if (task == null) {
				throw new FlowableObjectNotFoundException("No s'ha trobat la tasca activa " + taskId, Task.class);
			}
			// A diferència de TaskService.complete, no planifica el trigger de l'execució
			TaskHelper.deleteTask(task, MOTIU_TASCA_CANCELADA, false, true, true);
			return null;
		});
		return getTaskById(taskId);
	}

	@Override
	public WTaskInstance suspendTaskInstance(String taskId) {
		Task task = processEngine
			.getTaskService()
			.createTaskQuery()
			.taskId(taskId)
			.singleResult();
		((TaskEntityImpl)task).setSuspensionState(SuspensionState.SUSPENDED.getStateCode());
		processEngine.getTaskService().saveTask(task);
		return getTaskById(taskId);
	}

	@Override
	public WTaskInstance resumeTaskInstance(String taskId) {
		Task task = processEngine
			.getTaskService()
			.createTaskQuery()
			.taskId(taskId)
			.singleResult();
		((TaskEntityImpl)task).setSuspensionState(SuspensionState.ACTIVE.getStateCode());
		processEngine.getTaskService().saveTask(task);
		return getTaskById(taskId);
	}

	@Override
	public WTaskInstance reassignTaskInstance(String taskId, String expression, Long entornId) {

		// Esborra els candidats anteriors
		((TaskServiceImpl) processEngine.getTaskService())
			.getCommandExecutor()
			.execute(new AddCandidatesToTaskCmd(taskId));

		if(expression.startsWith("group(")) {
			String group = expression.replace("group(", "").replace(")", "");
			List<PersonaDto> persones = pluginHelper.personaFindAmbGrup(group);

			((TaskServiceImpl) processEngine.getTaskService())
				.getCommandExecutor()
				.execute(new AddCandidatesToTaskCmd(
					taskId,
					persones
						.stream()
						.map(PersonaDto::getCodi).toArray(String[]::new),
					group
				));
		} else if(expression.startsWith("user(")) {
			String user = expression.replace("user(", "").replace(")", "");
			processEngine.getTaskService().setAssignee(taskId, user);
		} else {
			processEngine.getTaskService().setAssignee(taskId, expression);
		}
		return this.getTaskById(taskId);
	}

	@Override
	public void setTaskInstanceActorId(String taskInstanceId, String actorId) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setTaskInstancePooledActors(String taskInstanceId, String[] pooledActors) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTaskInstanceInfoCache(String taskId, String titol, String infoCache) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<TascaOutcomeDto> findStartTaskOutcomes(String bpmId, String taskName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TascaOutcomeDto> findTaskInstanceOutcomes(String taskInstanceId) {
		TaskEntityImpl task = (TaskEntityImpl) this.processEngine
			.getTaskService()
			.createTaskQuery()
			.taskId(taskInstanceId)
			.singleResult();

		String processDefinitionId;
		String taskDefinitionKey;

		if(task != null) {
			processDefinitionId = task.getProcessDefinitionId();
			taskDefinitionKey = task.getTaskDefinitionKey();
		} else {
			HistoricTaskInstance htask = processEngine
				.getHistoryService()
				.createHistoricTaskInstanceQuery()
				.taskId(taskInstanceId)
				.singleResult();
			if(htask == null)
				return new ArrayList<>();

			processDefinitionId = htask.getProcessDefinitionId();
			taskDefinitionKey =  htask.getTaskDefinitionKey();
		}

		// Cada transició és un flux de sortida: l'id és el del flux (dos fluxos poden anar a la mateixa
		// activitat) i el nom és el del flux o, si no en té, el de l'activitat destí.
		List<TascaOutcomeDto> outcomes = new ArrayList<TascaOutcomeDto>();
		BpmnModel model = processEngine.getRepositoryService().getBpmnModel(processDefinitionId);
		FlowElement current = model.getFlowElement(taskDefinitionKey);
		if(current instanceof FlowNode) {
			List<SequenceFlow> sequences = ((FlowNode) current).getOutgoingFlows();
			if(sequences == null)
				return outcomes;
			for(SequenceFlow sequenceFlow : sequences) {
				String nom = sequenceFlow.getName();
				if (nom == null || nom.isBlank()) {
					FlowElement desti = sequenceFlow.getTargetFlowElement();
					nom = desti != null ? desti.getName() : null;
				}
				outcomes.add(new TascaOutcomeDto(sequenceFlow.getId(), nom));
			}
		}

		return outcomes;
	}

	@Override
	public Map<String, Object> getTaskInstanceVariables(String taskId) {
		return processEngine.getTaskService().getVariables(taskId);
	}

	@Override
	public Object getTaskInstanceVariable(String taskId, String varName) {
		return processEngine.getTaskService().getVariable(taskId, varName);
	}

	@Override
	public void setTaskInstanceVariable(String taskId, String varName, Object valor) {
		processEngine.getTaskService().setVariable(taskId, varName, valor);
	}

	@Override
	public void setTaskInstanceVariables(String taskId, Map<String, Object> variables, boolean deleteFirst) {
		if(deleteFirst)
			processEngine.getTaskService().removeVariables(taskId, variables.keySet());
		processEngine.getTaskService().setVariables(taskId, variables);
	}

	@Override
	public void deleteTaskInstanceVariable(String taskId, String varName) {
		processEngine.getTaskService().removeVariable(taskId, varName);
	}

	@Override
	public WToken getTokenById(String tokenId) {
		// Dins una comanda: toWToken consulta l'activitat actual, que Flowable carrega del model de procés
		return processEngine.getManagementService().executeCommand(commandContext -> toWToken(processEngine
				.getRuntimeService()
					.createExecutionQuery()
					.executionId(tokenId)
					.singleResult()));
	}

	@Override
	public Map<String, WToken> getActiveTokens(String processInstanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, WToken> getAllTokens(String processInstanceId) {
		List<WToken> tokens = processEngine.getManagementService().executeCommand(commandContext -> processEngine
								.getRuntimeService()
								.createExecutionQuery()
								.processInstanceId(processInstanceId)
								.list()
								.stream()
								.map(FlowableEngineImpl::toWToken)
								.collect(Collectors.toList()));
		Map<String, WToken> resposta = new HashMap<String, WToken>();
		for(WToken t : tokens)
			resposta.put(t.getId(), t);

		return resposta;
	}

	@Override
	public void tokenRedirect(String tokenId, String nodeName, boolean cancelTasks, boolean enterNodeIfTask,
			boolean executeNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean tokenActivar(String tokenId, boolean activar) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Reprèn una execució que espera una resposta externa (p. ex. la tasca d'un handler de portasignatures). Si
	 * s'indica la transició (id del flux de sortida o, per compatibilitat, id de l'activitat destí), el procés
	 * continua només per aquest flux; si no, pels fluxos de sortida habituals.
	 */
	@Override
	public void signalToken(String tokenId, String transitionName) {
		processEngine.getManagementService().executeCommand(commandContext -> {
			Execution execution = processEngine.getRuntimeService()
				.createExecutionQuery()
				.executionId(tokenId)
				.singleResult();
			if (execution == null) {
				throw new FlowableObjectNotFoundException("No s'ha trobat l'execució " + tokenId, Execution.class);
			}
			if (transitionName != null) {
				SequenceFlow transicio = cercarTransicioSortida(
					((ExecutionEntity) execution).getProcessDefinitionId(),
					execution.getActivityId(),
					transitionName);
				if (transicio == null) {
					throw new FlowableIllegalArgumentException(
						"L'activitat '" + execution.getActivityId() + "' no té la transició de sortida '" + transitionName + "'");
				}
				HeliumFlowableEngineAgenda.seleccionarTransicio(commandContext, tokenId, transicio);
			}
			processEngine.getRuntimeService().trigger(tokenId);
			return null;
		});
	}

	@Override
	public void evaluateScript(HeliumApi api, String script) {
		GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader());
		shell.setVariable("heliumApi", api);
		shell.evaluate(script);
	}

	@Override
	public Object evaluateExpression(String taskInstanceInstanceId, String processInstanceId, String expression,
			Map<String, Object> valors) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> listActions(String bpmId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeActionInstanciaProces(String processInstanceId, String actionName,
			String processDefinitionPareId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeActionInstanciaTasca(String taskInstanceId, String actionName, String processDefinitionPareId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void suspendTimer(String timerId, Date dueDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resumeTimer(String timerId, Date dueDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> findAreesByFiltre(String filtre) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findAreesByPersona(String personaCodi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findRolsByPersona(String persona) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String[]> findCarrecsByFiltre(String filtre) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findPersonesByGrupAndCarrec(String areaCodi, String carrecCodi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findCarrecsByPersonaAndGrup(String codiPersona, String codiArea) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findPersonesByCarrec(String codi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findPersonesByGrup(String rol) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findArrivingNodeNames(String tokenId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Finalització manual de l'expedient. Com feia Helium 3 amb jBPM ({@code ProcessInstanceEndCommand}), es
	 * cancel·len les tasques obertes sense fer avançar el procés. A més, les instàncies es suspenen perquè els
	 * timers i els jobs no continuïn el procés d'un expedient finalitzat. Les instàncies no s'esborren, així es pot
	 * desfer la finalització ({@link #desfinalitzarExpedient}). La data de finalització la desa Helium a
	 * l'expedient.
	 *
	 * <p>Si es crida des d'un handler que s'executa dins el motor (p. ex. {@code ExpedientFinalitzarHandler}), no es
	 * suspenen les instàncies: el procés s'està executant i ha de poder continuar fins al final.</p>
	 */
	@Override
	public void finalitzarExpedient(String[] processInstanceIds, Date dataFinalitzacio) {
		boolean dinsElMotor = Context.getCommandContext() != null;
		for (String processInstanceId : processInstanceIds) {
			if (getProcessInstanceEnCurs(processInstanceId) == null) {
				continue;
			}
			processEngine.getManagementService().executeCommand(commandContext -> {
				List<TaskEntity> tasques = CommandContextUtil
					.getProcessEngineConfiguration(commandContext)
					.getTaskServiceConfiguration()
					.getTaskService()
					.findTasksByProcessInstanceId(processInstanceId);
				for (TaskEntity tasca : new ArrayList<>(tasques)) {
					TaskHelper.deleteTask(tasca, MOTIU_TASCA_CANCELADA, false, true, true);
				}
				return null;
			});
			if (!dinsElMotor) {
				suspendProcessInstances(new String[] { processInstanceId });
			}
		}
	}

	/**
	 * Desfà la finalització de l'expedient: reactiva les instàncies que la finalització manual havia suspès. Les
	 * tasques cancel·lades en finalitzar no es tornen a crear (tampoc ho feia jBPM). Si el procés havia acabat
	 * (havia arribat a un esdeveniment de fi), Flowable no permet reobrir-lo: l'expedient es reobre però no té cap
	 * flux en curs.
	 */
	@Override
	public void desfinalitzarExpedient(String processInstanceId) {
		List<String> ids = getProcessInstanceTree(processInstanceId)
			.stream()
			.map(WProcessInstance::getId)
			.collect(Collectors.toList());
		resumeProcessInstances(ids.toArray(new String[0]));
	}

	@Override
	public List<String> findDefinicionsProcesIdNoUtilitzadesByEntorn(Long entornId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(Long expedientTipusId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WExpedientDto> findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(Long expedientTipusId,
																					  Long processDefinitionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void retrocedirAccio(String processInstanceId, String actionName, List<String> params,
			String processDefinitionPareId) {
		// TODO Auto-generated method stub

	}

	@Override
	public WProcessDefinition parse(String nomArxiu, byte[] contingut) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WProcessLog getProcessLogById(Long bpmLogId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long addProcessInstanceMessageLog(String processInstanceId, String string) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<WToken, List<WProcessLog>> getProcessInstanceLogs(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long addTaskInstanceMessageLog(String taskInstanceId, String messageLogPerTipus) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WTaskInstance> findTaskInstancesForProcessInstance(String processInstanceId, boolean completed) {
		List<WTaskInstance> wTask = new ArrayList<>();
		if(completed) {
			List<HistoricTaskInstance> htasks = processEngine
								.getHistoryService()
								.createHistoricTaskInstanceQuery()
								.includeIdentityLinks()
								.processInstanceId(processInstanceId)
								.list();
			for (HistoricTaskInstance task : htasks)
				wTask.add(toWTaskInstance(task));
		} else {
			List<Task> tasks = processEngine
								.getTaskService()
								.createTaskQuery()
								.includeIdentityLinks()
								.processInstanceId(processInstanceId)
								.list();

			for (Task task : tasks) {
				task.getIdentityLinks();
				wTask.add(toWTaskInstance(task));
			}
		}
		return wTask;
	}


//	@Override
//	public WExpedientDto expedientFindByProcessInstanceId(String processInstanceId) {
//		ProcessInstance pi = processEngine
//			.getRuntimeService()
//			.createProcessInstanceQuery()
//			.processInstanceId(processInstanceId)
//			.includeProcessVariables()
//			.singleResult();
//		if (pi != null && pi.getProcessVariables().get("__expedient_id__") != null)
//			return WExpedientDto.builder()
//					.id((Long)pi.getProcessVariables().get("__expedient_id__"))
//					.titol((String)Optional.ofNullable(pi.getProcessVariables().get("__expedient_titol__")).orElse(""))
//					.numero((String)Optional.ofNullable(pi.getProcessVariables().get("__expedient_numero__")).orElse(""))
//					.numeroDefault((String)Optional.ofNullable(pi.getProcessVariables().get("__expedient_numero_default__")).orElse(""))
//					.processInstanceId(processInstanceId)
//					.build();
//
//		HistoricProcessInstance hpi = processEngine.getHistoryService()
//			.createHistoricProcessInstanceQuery()
//			.processInstanceId(processInstanceId)
//			.includeProcessVariables()
//			.singleResult();
//		if(hpi == null || hpi.getProcessVariables().get("__expedient_id__") == null)
//			return null;
//
//		return WExpedientDto.builder()
//			.id((Long)Optional.ofNullable(hpi.getProcessVariables().get("__expedient_id__")).orElse(0L))
//			.titol((String)Optional.ofNullable(hpi.getProcessVariables().get("__expedient_titol__")).orElse(""))
//			.numero((String)Optional.ofNullable(hpi.getProcessVariables().get("__expedient_numero__")).orElse(""))
//			.numeroDefault((String)Optional.ofNullable(hpi.getProcessVariables().get("__expedient_numero_default__")).orElse(""))
//			.processInstanceId(processInstanceId)
//			.build();
//	}

	@Override
	public void deleteProcessInstanceTreeLogs(String processInstanceId) {
//		ProcessInstance pi = processEngine
//			.getRuntimeService()
//			.createProcessInstanceQuery()
//			.processInstanceId(processInstanceId)
//			.singleResult();
		processEngine
			.getHistoryService()
			.deleteHistoricProcessInstance(processInstanceId);
	}

	/** Mètode per modificar la descripció d'una tasca. */
	@Override
	public void describeTaskInstance(String id, String titol, Object descriptionWithFields) {
		Task task = processEngine
						.getTaskService()
						.createTaskQuery()
						.taskId(id)
						.singleResult();
		if(task != null)
			task.setDescription(String.valueOf(descriptionWithFields));
	}

	@Override
	public void resumeTimer(long timerId, Date dataFi) {
		// TODO Auto-generated method stub

	}

	@Override
	public void suspendTimer(long timerId, Date dueDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean tokenActivar(Long tokenId, boolean activar) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void tokenRedirect(long longValue, String nodeName, boolean cancelTasks, boolean enterNodeIfTask,
			boolean executeNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Long> findListTasks(String responsable, String titol, String tasca, List<Long> idsExpedients,
			Date dataCreacioInici, Date dataCreacioFi, Integer prioritat, Date dataLimitInici, Date dataLimitFi,
			PaginacioParamsDto paginacioParamsDto, boolean nomesTasquesPersonals, boolean nomesTasquesGrup, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WTaskInstance> tascaFindByFiltrePaginat(Long entornId, String responsable, String tasca, String titol,
			Object object, String expedient, Object object2, Long expedientTipusId, Date dataCreacioInici,
			Date dataCreacioFi, Integer prioritat, Date dataLimitInici, Date dataLimitFi,
			boolean mostrarAssignadesUsuari, boolean mostrarAssignadesGrup, boolean b, boolean administrador,
			PaginacioParamsDto paginacioParams) {
		// TODO Auto-generated method stub
		return new ArrayList<WTaskInstance>();
	}

	@Override
	public String getVariableIdFromVariableLog(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTaskInstanceIdByTokenId(String tokenId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeHandler(String processInstanceId, String handlerClassPerRecurs, Map<String, String> dades) {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeHandlerPredefinit(String processInstanceId, String handlerClasse, Map<String, String> dades) {
		// TODO Auto-generated method stub

	}

	@Override
	public WProcessDefinition parseProcess(byte[] contingut) {
		InputStreamProvider in = new BytesStreamSource(contingut);
		BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(in, false, false);
		org.flowable.bpmn.model.Process process =
				bpmnModel.getMainProcess();
		WProcessDefinition processDefinition = new WProcessDefinition();
		processDefinition.setKey(process.getId());
		processDefinition.setName(process.getName());
		processDefinition.setStartTaskName(this.getStartTaskName(bpmnModel));
		return processDefinition;
	}

	/// Mètodes per conviertir objectes Flowable a dades genèriques

	/** Converteix l'objecte ProcessDefintion a WProcessDefinition.
	 *
	 * @param pd
	 * @return
	 */
	public static WProcessDefinition toWProcessDefinition(ProcessDefinition pd) {
		WProcessDefinition wpd = null;
		if (pd != null) {
			wpd = new WProcessDefinition();
			wpd.setId(pd.getId());
			wpd.setKey(pd.getKey());
			wpd.setName(pd.getName());
			wpd.setVersion(pd.getVersion());
			wpd.setDeploymentId(pd.getDeploymentId());
		}
		return wpd;
	}

	/** Converteix l'objecte ProcessInstance a WProcessInstance.
	 *
	 * @param pi
	 * @return
	 */
	public static WProcessInstance toWProcessInstance(ProcessInstance pi) {
		WProcessInstance wpi = null;
		if (pi != null) {
			wpi = new WProcessInstance();
			wpi.setId(pi.getId());
			wpi.setKey(pi.getBusinessKey());
			wpi.setProcessDefinitionId(pi.getProcessDefinitionId());
			wpi.setProcessDefinitionKey(pi.getProcessDefinitionKey());
			wpi.setProcessDefinitionName(pi.getProcessDefinitionName());
			wpi.setProcessDefinitionVersion(pi.getProcessDefinitionVersion());
			wpi.setParentProcessInstanceId(pi.getParentId());
			wpi.setRootProcessInstanceId(pi.getRootProcessInstanceId());
			wpi.setStartTime(pi.getStartTime());
			wpi.setDescription(pi.getDescription());
			wpi.setSuspended(pi.isSuspended());
		}
		return wpi;
	}

	/** Converteix l'objecte HistoricProcessInstance a WProcessInstance.
	 *
	 * @param pi
	 * @return
	 */
	public static WProcessInstance toWProcessInstance(HistoricProcessInstance pi) {
		WProcessInstance wpi = null;
		if (pi != null) {
			wpi = new WProcessInstance();
			wpi.setId(pi.getId());
			wpi.setKey(pi.getBusinessKey());
			wpi.setProcessDefinitionId(pi.getProcessDefinitionId());
			wpi.setProcessDefinitionKey(pi.getProcessDefinitionKey());
			wpi.setProcessDefinitionName(pi.getProcessDefinitionName());
			wpi.setProcessDefinitionVersion(pi.getProcessDefinitionVersion());
			wpi.setParentProcessInstanceId(pi.getSuperProcessInstanceId());
			wpi.setStartTime(pi.getStartTime());
			wpi.setEndTime(pi.getEndTime());
			wpi.setDescription(pi.getDescription());
		}
		return wpi;
	}

	/** Converteix l'objecte Execution a WToken.
	 *
	 * @param e
	 * @return
	 */
	public static WToken toWToken(Execution e) {
		WToken wt = null;
		if (e != null) {
			wt = new WToken();
			wt.setId(e.getId());
			wt.setName(e.getName());
			wt.setProcessInstanceId(e.getProcessInstanceId());
			wt.setSuperRootTokenId(e.getSuperExecutionId());
			if (e instanceof ExecutionEntityImpl) {
				ExecutionEntityImpl fe = (ExecutionEntityImpl) e;
				wt.setProcessInstanceKey(fe.getProcessInstance() != null? fe.getProcessInstance().getBusinessKey() : null);
				wt.setStart(fe.getStartTime());
				wt.setRoot(fe.isMultiInstanceRoot());
				if (fe.getParent() != null && !fe.equals(fe.getParent())) {
					wt.setParent(toWToken(fe.getParent()));
				}
				if (fe.getSuperExecution() != null && !fe.equals(fe.getSuperExecution())) {
					wt.setSuperToken(toWToken(fe.getParent()));
				}
				wt.setProcessDefinitionId(fe.getProcessDefinitionId());
				wt.setProcessDefinitionKey(fe.getProcessDefinitionName());
				wt.setProcessDefinitionName(fe.getProcessDefinitionName());
				wt.setProcessDefinitionversion(fe.getProcessDefinitionVersion());
				FlowElement actual = fe.getCurrentFlowElement();
				wt.setNodeName(actual != null ? actual.getName() : null);
				wt.setReceiveTask(actual instanceof ReceiveTask);
				if (actual instanceof FlowNode) {
					// Transicions per les quals es pot reprendre l'execució (signalToken): l'id de cada flux de
					// sortida i, per compatibilitat, l'id de l'activitat destí
					for (SequenceFlow sortida : ((FlowNode) actual).getOutgoingFlows()) {
						wt.getSortides().add(sortida.getId());
						wt.getSortides().add(sortida.getTargetRef());
					}
				}
			}

		}
		return wt;
	}

	/** Converteix l'objecte Task a WTaskInstance.
	 *
	 * @param t Task
	 * @return
	 */
	public static WTaskInstance toWTaskInstance(Task t) {
		WTaskInstance wt = null;
		if (t != null) {
			wt = new WTaskInstance();
			wt.setId(t.getId());
			wt.setTaskName(t.getTaskDefinitionKey());
			wt.setName(t.getName());
			wt.setDescription(t.getDescription());
			wt.setCreateTime(t.getCreateTime());
			wt.setStartTime(t.getCreateTime());
			wt.setClaimTime(t.getClaimTime());
			wt.setEndTime(null); // No es poden consultar les tasques acabades, s'ha de mirar l'històric
			wt.setDueDate(t.getDueDate());
			wt.setPriority(t.getPriority());
			wt.setActorId(t.getAssignee());
			wt.setProcessInstanceId(t.getProcessInstanceId());
			wt.setProcessDefinitionId(t.getProcessDefinitionId());

			wt.setSuspended(t.isSuspended());
			if(t instanceof TaskEntityImpl)
				wt.setCancelled(((TaskEntityImpl)t).isCanceled());


			wt.setSuspended(t.isSuspended());
			if (t instanceof TaskEntityImpl) {
				TaskEntityImpl ft = (TaskEntityImpl) t;
				wt.setCancelled(ft.isCanceled());
				for (var identityLink : ft.getIdentityLinks()) {
					if (identityLink.getUserId() != null) {
						wt.getPooledActors().add(identityLink.getUserId());
					} else if (identityLink.getGroupId() != null) {
						wt.getRols().add(identityLink.getGroupId());
					}
				}
			}
		}
		return wt;
	}

	/** Converteix l'objecte Task a WTaskInstance.
	 *
	 * @param t Task
	 * @return
	 */
	public static WTaskInstance toWTaskInstance(HistoricTaskInstance t) {
		WTaskInstance wt = null;
		if (t != null) {
			wt = new WTaskInstance();
			wt.setId(t.getId());
			wt.setTaskName(t.getTaskDefinitionKey());
			wt.setDescription(t.getName());
			wt.setCreateTime(t.getCreateTime());
			wt.setStartTime(t.getClaimTime());
			wt.setEndTime(t.getEndTime());
			wt.setDueDate(t.getDueDate());
			wt.setPriority(t.getPriority());
			wt.setActorId(t.getAssignee());
			wt.setProcessInstanceId(t.getProcessInstanceId());
			wt.setProcessDefinitionId(t.getProcessDefinitionId());
			wt.setCancelled(MOTIU_TASCA_CANCELADA.equals(t.getDeleteReason()));

			for (var identityLink : t.getIdentityLinks()) {
				if (identityLink.getUserId() != null) {
					wt.getPooledActors().add(identityLink.getUserId());
				} else if (identityLink.getGroupId() != null) {
					wt.getRols().add(identityLink.getGroupId());
				}
			}
		}
		return wt;
	}

	@Override
	public void completeTask(String processInstanceId, String taskName, Map<String, Object> variables) {
		// Obté la tasca per nom
		Task task = processEngine
				.getTaskService()
				.createTaskQuery()
				.processInstanceId(processInstanceId)
				.taskDefinitionKey(taskName)
				.singleResult();
		if (task != null) {
			// Completa la tasca
			processEngine
				.getTaskService()
				.complete(task.getId(), null, variables);
		}
	}

}
