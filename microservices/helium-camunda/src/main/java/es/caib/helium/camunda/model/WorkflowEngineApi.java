package es.caib.helium.camunda.model;

import es.caib.helium.camunda.service.ActionService;
import es.caib.helium.camunda.service.DeploymentService;
import es.caib.helium.camunda.service.ExecutionService;
import es.caib.helium.camunda.service.ProcessDefinitionService;
import es.caib.helium.camunda.service.ProcessInstanceService;
import es.caib.helium.camunda.service.TaskInstanceService;
import es.caib.helium.camunda.service.TaskVariableService;
import es.caib.helium.camunda.service.TimerService;
import es.caib.helium.camunda.service.VariableInstanceService;

/**
 * Interfície comú dels motors de workflow amb els mètodes necessaris per desplegar, consultar,
 * executar i mantenir els workflows de processos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public interface WorkflowEngineApi extends
		DeploymentService,
		ProcessDefinitionService,
		ProcessInstanceService,
		VariableInstanceService,
		TaskInstanceService,
		TaskVariableService,
		ExecutionService,
		ActionService,
		TimerService {
	
	public static final String VAR_TASCA_DELEGACIO = "H3l1um#tasca.delegacio";
	public static final String VAR_TASCA_VALIDADA = "H3l1um#tasca.validada";
	
	public enum MostrarTasquesDto {
		MOSTRAR_TASQUES_TOTS,
		MOSTRAR_TASQUES_NOMES_GROUPS,
		MOSTRAR_TASQUES_NOMES_PERSONALS
	}
	
	// INSTÀNCIA DE PROCÉS
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	/*
	 * getId()
	 * getProcessDefinitionId()
	 * getProcessInstance().getProcessDefinition().getName() ==> Activiti::ProcessInstance.getProcessDefinitionName()
	 * getParentProcessInstanceId()
	 * getProcessInstance().getId()
	 * getEnd() ==> Activiti::HistoricProcessInstance.getEndTime()
	 * getProcessInstance().getTaskMgmtInstance().getUnfinishedTasks(currentToken) ==> Retroces!!!
	 */
	
	// INSTÀNCIA DE TASQUES
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////

	/*
	 * getProcessInstanceId()
	 * getId()
	 * getTaskInstance().getActorId() ==> Activiti::getAssignee()
	 * getTaskInstance().getProcessInstance().getExpedient().getId()
	 * getTaskInstance().getSelectedOutcome()
	 * getTaskInstance().getId()
	 * getProcessDefinitionId()
	 * getTaskInstance().getPooledActors() ==> Activiti::TaskService.getIdentityLinksForTask(String taskId)
	 * 
	 */
	

	// VARIABLES DE TASQUES
	////////////////////////////////////////////////////////////////////////////////
	
	//TODO: Comprovar si s'ha d'implementr el mètode per finalitzar expedients demanat en la versió 3.2.45
	// finalitzarExpedient(, boolean cancel·larTasquesActives)
	

//	// A ELIMINAR
//	////////////////////////////////////////////////////////////////////////////////
//	public List<WTaskInstance> findTasks(List<Long> ids); // 2.6
//	public List<WTaskInstance> findPersonalTasks(String usuariBo); // 2.6
//
//	// A MOURE FORA DE LA FUNCIONALITAT DEL MOTOR DE WORKFLOW
//	////////////////////////////////////////////////////////////////////////////////
//
//	// Transicions (Sequence flow)
//	public List<String> findStartTaskOutcomes(String jbpmId, String taskName);
//	public List<String> findTaskInstanceOutcomes(String taskInstanceId);
//	public List<String> findArrivingNodeNames(String tokenId); // Retrocedir??
		
	
//	// Expedients
//	//public ExpedientDto expedientFindByProcessInstanceId(String processInstanceId);
//	public ResultatConsultaPaginada<Long> expedientFindByFiltre(
//			Long entornId,
//			String actorId,
//			Collection<Long> tipusIdPermesos,
//			String titol,
//			String numero,
//			Long tipusId,
//			Date dataCreacioInici,
//			Date dataCreacioFi,
//			Long estatId,
//			Double geoPosX,
//			Double geoPosY,
//			String geoReferencia,
//			boolean nomesIniciats,
//			boolean nomesFinalitzats,
//			boolean mostrarAnulats,
//			boolean mostrarNomesAnulats,
//			boolean nomesAlertes,
//			boolean nomesErrors,
//			boolean nomesTasquesPersonals,
//			boolean nomesTasquesGrup,
//			boolean nomesTasquesMeves,
//			Pageable paginacio,
//			boolean nomesCount);
//
//			/*
//			| V3
//			|- ExpedientServiceImpl
//			|		- consultaFindNomesIdsPaginat
//			|		- consultaFindPaginat
//			|		- findAmbFiltrePaginat
//			|		- findIdsAmbFiltre
//			*/
//	public void desfinalitzarExpedient(String processInstanceId);
//
//	/** Mètode per finalitzar l'expedient. */
//	public void finalitzarExpedient(String[] processInstanceIds, Date dataFinalitzacio);

	
//	// Tasques en segón pla
//	public void marcarFinalitzar(String taskId, Date marcadaFinalitzar, String outcome, String rols);
//	public void marcarIniciFinalitzacioSegonPla(String taskId, Date iniciFinalitzacio);
//	public void guardarErrorFinalitzacio(String taskId, String errorFinalitzacio);
//	public List<Object[]> getTasquesSegonPlaPendents();
//
//	// Eliminació de definicions de procés
//	public List<String> findDefinicionsProcesIdNoUtilitzadesByEntorn(Long entornId);
//	public List<String> findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(Long expedientTipusId);
//	public List<ExpedientDto> findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
//			Long expedientTipusId,
//			Long processDefinitionId);
	
	// Avaluació d'expressions
	
//	/** Avalua una expressió amb uns valors de variables en el contexte.
//	 *
//	 * @param expression
//	 * @param expectedClass
//	 * @param context
//	 * @return
//	 */
//	@SuppressWarnings("rawtypes")
//	public Object evaluateExpression(
//			String expression,
//			Class expectedClass,
//			Map<String, Object> context);
	

//	/** Mètode per obtenir una definició de procés a partir del contingut comprimit del mateix.
//	 *
//	 * @param zipInputStream
//	 * @return
//	 * @throws Exception
//	 */
//	public WProcessDefinition parse(ZipInputStream zipInputStream ) throws Exception;

}
