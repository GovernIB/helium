package es.caib.helium.logic.flowable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.CallActivity;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.ReceiveTask;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.SubProcess;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.io.InputStreamProvider;
import org.flowable.common.engine.impl.util.io.BytesStreamSource;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.Task;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.WExpedientDto;
import es.caib.helium.logic.intf.dto.engine.WDeployment;
import es.caib.helium.logic.intf.dto.engine.WProcessDefinition;
import es.caib.helium.logic.intf.dto.engine.WProcessInstance;
import es.caib.helium.logic.intf.dto.engine.WProcessLog;
import es.caib.helium.logic.intf.dto.engine.WTaskInstance;
import es.caib.helium.logic.intf.dto.engine.WToken;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;

/** Implementació de l'API del WorkflowEngine pel motor BPMN 2.0. Flowable
 *
 */
@Component
public class FlowableEngineImpl implements WorkflowEngineApi {

	@Autowired
	private ProcessEngine processEngine;

	@Override
	public WProcessDefinition desplegar(String nomArxiu, byte[] contingut) {
		Deployment deployment = processEngine
			.getRepositoryService()
			.createDeployment()
			.addInputStream(nomArxiu, new ByteArrayInputStream(contingut))
			.deploy();
		ProcessDefinition pd = processEngine
		        .getRepositoryService()
	                .createProcessDefinitionQuery()
	                .deploymentId(deployment.getId())
	                .singleResult();
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
		ProcessDefinition wpd = processEngine
									.getRepositoryService()
									.getProcessDefinition(deploymentId);
		processEngine
			.getRepositoryService()
			.deleteDeployment(wpd.getDeploymentId());
	}

	@Override
	public Set<String> getResourceNames(String deploymentId) {
		// TODO Auto-generated method stub
		return new HashSet<>();
	}

	@Override
	public byte[] getResourceBytes(String deploymentId, String resourceName) {
		// TODO Auto-generated method stub
		return null;
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
		WProcessDefinition wpd = toWProcessDefinition(pd);
		return wpd;
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
	public List<String> getTaskNamesFromDeployedProcessDefinition(String processKey, Integer version) {
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

		List<String> taskNames = new ArrayList<>();
	    for (FlowElement element : process.getFlowElements()) {
	        if (element instanceof UserTask) {
	            UserTask userTask = (UserTask) element;
	        	taskNames.add(userTask.getName());
	        }
	    }
		return taskNames;
	}

	@Override
	public String getStartTaskName(String processDefinitionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateSubprocessDefinition(WProcessDefinition pd1, WProcessDefinition pd2) {
		// TODO Auto-generated method stub

	}

	@Override
	public long countProcessInstancesWithProcessDefinitionId(String processDefinitionId) {
		long count =processEngine
					.getRuntimeService()
						.createProcessInstanceQuery()
						.processDefinitionId(processDefinitionId)
						.count();
		return count;
	}

	@Override
	public List<WProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(String processName,
			Long entornId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WProcessInstance> getProcessInstanceTree(String rootProcessInstanceId) {
		List<Execution> executions =  processEngine
				.getRuntimeService()
			    .createExecutionQuery()
			    .rootProcessInstanceId(rootProcessInstanceId)
			    .list();
		Set<String> processInstanceIds = new HashSet<>();
		for (Execution ex : executions) {
			if (!processInstanceIds.contains(ex.getProcessInstanceId())) {
				processInstanceIds.add(ex.getProcessInstanceId());
			}
		}
		List<ProcessInstance> pis = processEngine
				.getRuntimeService()
				.createProcessInstanceQuery()
				.processInstanceIds(processInstanceIds).list();
		List<WProcessInstance> wPis = new ArrayList<>();
		for (ProcessInstance pi : pis) {
			wPis.add(toWProcessInstance(pi));
		}
		return wPis;
	}

	@Override
	public WProcessInstance getProcessInstance(String processInstanceId) {
		ProcessInstance pi = processEngine
								.getRuntimeService()
									.createProcessInstanceQuery()
									.processInstanceId(processInstanceId)
									.singleResult();
		return toWProcessInstance(pi);
	}

	@Override
	public WProcessInstance getRootProcessInstance(String processInstanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findRootProcessInstances(String actorId, List<String> processInstanceIds, boolean nomesMeves,
			boolean nomesTasquesPersonals, boolean nomesTasquesGrup) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long findExpedientIdByProcessInstanceId(String processInstanceId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WProcessInstance startProcessInstanceById(String actorId, String processDefinitionId,
			Map<String, Object> variables) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void signalProcessInstance(String processInstanceId, String transitionName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteProcessInstance(String processInstanceId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void suspendProcessInstances(String[] processInstanceIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resumeProcessInstances(String[] processInstanceIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeProcessInstanceVersion(String processInstanceId, int newVersion) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Object> getProcessInstanceVariables(String processInstanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getProcessInstanceVariable(String processInstanceId, String varName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProcessInstanceVariable(String processInstanceId, String varName, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteProcessInstanceVariable(String processInstanceId, String varName) {
		// TODO Auto-generated method stub

	}

	@Override
	public WTaskInstance getTaskById(String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WTaskInstance> findTaskInstancesByProcessInstanceId(String processInstanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTaskInstanceIdByExecutionTokenId(String executionTokenId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WTaskInstance takeTaskInstance(String taskId, String actorId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WTaskInstance releaseTaskInstance(String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WTaskInstance startTaskInstance(String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void endTaskInstance(String taskId, String outcome) {
		// TODO Auto-generated method stub

	}

	@Override
	public WTaskInstance cancelTaskInstance(String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WTaskInstance suspendTaskInstance(String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WTaskInstance resumeTaskInstance(String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WTaskInstance reassignTaskInstance(String taskId, String expression, Long entornId) {
		// TODO Auto-generated method stub
		return null;
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
	public List<String> findStartTaskOutcomes(String jbpmId, String taskName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findTaskInstanceOutcomes(String taskInstanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getTaskInstanceVariables(String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getTaskInstanceVariable(String taskId, String varName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTaskInstanceVariable(String taskId, String varName, Object valor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTaskInstanceVariables(String taskId, Map<String, Object> variables, boolean deleteFirst) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteTaskInstanceVariable(String taskId, String varName) {
		// TODO Auto-generated method stub

	}

	@Override
	public WToken getTokenById(String tokenId) {
		Execution e = processEngine
				.getRuntimeService()
					.createExecutionQuery()
					.executionId(tokenId)
					.singleResult();
		return toWToken(e);
	}

	@Override
	public Map<String, WToken> getActiveTokens(String processInstanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, WToken> getAllTokens(String processInstanceId) {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public void signalToken(String tokenId, String transitionName) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Object> evaluateScript(String processInstanceId, String script, Set<String> outputNames) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evaluateExpression(String taskInstanceInstanceId, String processInstanceId, String expression,
			Map<String, Object> valors) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> listActions(String jbpmId) {
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

	@Override
	public void finalitzarExpedient(String[] processInstanceIds, Date dataFinalitzacio) {
		// TODO Auto-generated method stub

	}

	@Override
	public void desfinalitzarExpedient(String processInstanceId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void marcarFinalitzar(String taskId, Date marcadaFinalitzar, String outcome, String rols) {
		// TODO Auto-generated method stub

	}

	@Override
	public void marcarIniciFinalitzacioSegonPla(String taskId, Date iniciFinalitzacio) {
		// TODO Auto-generated method stub

	}

	@Override
	public void guardarErrorFinalitzacio(String taskId, String errorFinalitzacio) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Object[]> getTasquesSegonPlaPendents() {
		// TODO Auto-generated method stub
		return null;
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
	public void updateHandlers(long long1, Map<String, byte[]> recursos) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> getHandlerNames(String jbpmId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WProcessLog getProcessLogById(Long jbpmLogId) {
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
	public List<WTaskInstance> findTaskInstancesForProcessInstance(String processInstanceId) {
		List<Task> tasks = processEngine
									.getTaskService()
										.createTaskQuery()
											.processInstanceId(processInstanceId).list();
		List<WTaskInstance> wTask = new ArrayList<>();
		for(Task task : tasks) {
			wTask.add(toWTaskInstance(task));
		}
		return wTask;
	}


	@Override
	public List<Long> expedientFindByFiltre(Long entornId, String name, List<Long> tipusPermesosIds,
			Map<Long, List<Long>> unitatsPerTipusComu, String titol, String numero, Long unitatOrganitzativaId,
			Long expedientTipusId, Date dataInici1, Date dataInici2, Date dataFi1, Date dataFi2, Long estatId,
			Double geoPosX, Double geoPosY, String geoReferencia, String registreNumero, boolean equals,
			boolean equals2, boolean equals3, boolean equals4, boolean nomesAlertes, boolean nomesErrors,
			boolean nomesTasquesPersonals, boolean nomesTasquesGrup, boolean b, Object object,
			PaginacioParamsDto paginacioParams, boolean c, boolean nomesErrorsArxiu, Set<Long> idsSeleccionats) {
		// TODO Auto-generated method stub
		return new ArrayList<Long>();
	}

	@Override
	public es.caib.helium.commons.dto.ExpedientDto expedientFindByProcessInstanceId(String processInstanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteProcessInstanceTreeLogs(String processInstanceId) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> findRootProcessInstancesWithTasksCommand(String name, List<String> idsPI, boolean nomesMeves,
			boolean nomesTasquesPersonals, boolean nomesTasquesGrup) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WProcessInstance[] findProcessInstancesWithProcessDefinitionNameEntornAndTipus(String jbpmKey, Long entornId,
			Long expedientTipusId) {
		// TODO Auto-generated method stub
		return null;
	}

	/** Mètode per modificar la descripció d'una tasca. */
	@Override
	public void describeTaskInstance(String id, String titol, Object descriptionWithFields) {
		processEngine
			.getTaskService()
				.createTaskQuery()
				.taskId(id)
				.singleResult()
					.setDescription(String.valueOf(descriptionWithFields));
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
	public void signalToken(long longValue, String transicioOK) {
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
		return processDefinition;
	}

	/// Mètodes per conviertir objectes Flowable a dades genèriques
	
	/** Converteix l'objecte ProcessDefintion a WProcessDefinition.
	 *
	 * @param pd
	 * @return
	 */
	private WProcessDefinition toWProcessDefinition(ProcessDefinition pd) {
		WProcessDefinition wpd = null;
		if (pd != null) {
			wpd = new WProcessDefinition();
			wpd.setId(pd.getId());
			wpd.setKey(pd.getKey());
			wpd.setName(pd.getName());
			wpd.setVersion(pd.getVersion());
		}
		return wpd;
	}

	/** Converteix l'objecte ProcessInstance a WProcessInstance.
	 *
	 * @param pd
	 * @return
	 */
	private WProcessInstance toWProcessInstance(ProcessInstance pi) {
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

	/** Converteix l'objecte Execution a WToken.
	 *
	 * @param e
	 * @return
	 */
	private WToken toWToken(Execution e) {
		WToken wt = null;
		if (e != null) {
			wt = new WToken();
			wt.setId(e.getId());
			wt.setName(e.getName());
			wt.setProcessInstanceId(e.getProcessInstanceId());
			wt.setSuperRootTokenId(e.getSuperExecutionId());
			if (e instanceof ExecutionEntityImpl) {
				ExecutionEntityImpl fe = (ExecutionEntityImpl) e;
				wt.setProcessInstanceKey(fe.getProcessInstanceBusinessKey());
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
				wt.setNodeName(fe.getCurrentFlowElement().getName());
				if (fe.getCurrentFlowElement() instanceof ReceiveTask) {
					wt.setReceiveTask(true);
					ReceiveTask rt = (ReceiveTask) fe.getCurrentFlowElement();
					List<SequenceFlow> sortides = rt.getOutgoingFlows();
					for(SequenceFlow sortida : sortides) {
						wt.getSortides().add(sortida.getId());
					}
				}
			}
			
		}
		return wt;
	}
	
	/** Converteix l'objecte Task a WTaskInstance.
	 *
	 * @param e
	 * @return
	 */
	private WTaskInstance toWTaskInstance(Task t) {
		WTaskInstance wt = null;
		if (t != null) {
			wt = new WTaskInstance();
			wt.setId(t.getId());
			wt.setTaskName(t.getName());
			wt.setDescription(t.getDescription());
			wt.setCreateTime(t.getCreateTime());
			wt.setStartTime(t.getClaimTime());
			wt.setEndTime(null); // No es poden consultar les tasques acabades, s'ha de mirar l'històric
			wt.setDueDate(t.getDueDate());
			wt.setPriority(t.getPriority());
			wt.setActorId(t.getAssignee());
			wt.setProcessInstanceId(t.getProcessInstanceId());
			wt.setProcessDefinitionId(t.getProcessDefinitionId());
			

			wt.setSuspended(t.isSuspended());
			if (t instanceof TaskEntityImpl) {
				TaskEntityImpl ft = (TaskEntityImpl) t;
				wt.setCancelled(ft.isCanceled());
				for (var identityLink : ft.getQueryIdentityLinks()) {
					if (IdentityLinkType.CANDIDATE.equals(identityLink.getType())) {
						wt.getPooledActors().add(identityLink.getUserId());
					} else if (identityLink.getGroupId() != null) {
						wt.getRols().add(identityLink.getGroupId());
					}
				}			
			}
		}
		return wt;
	}

}
