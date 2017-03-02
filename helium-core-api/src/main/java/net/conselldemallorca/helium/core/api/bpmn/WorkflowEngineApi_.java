package net.conselldemallorca.helium.core.api.bpmn;

import java.util.Map;

public interface WorkflowEngineApi_ {
	
//	// DEFINICIÓ DE PROCÉS
//	////////////////////////////////////////////////////////////////////////////////
//	
//	// Desplegaments
//	public JbpmProcessDefinition desplegar(String nomArxiu, byte[] contingut);
//	public void esborrarDesplegament(String jbpmId);
//	public Set<String> getResourceNames(String jbpmId);
//	public byte[] getResourceBytes(String jbpmId, String resourceName);
//	public void updateHandlers(Long jbpmId, Map<String, byte[]> handlers);
//	
//	// Process Definitions
//	public JbpmProcessDefinition getProcessDefinition(String jbpmId);
//	public List<JbpmProcessDefinition> getSubProcessDefinitions(String jbpmId);
//	public List<String> getTaskNamesFromDeployedProcessDefinition(JbpmProcessDefinition dpd);
//	public String getStartTaskName(String jbpmId);
//	public JbpmProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId);
//		
//	// DEFINICIÓ DE TASQUES
//	////////////////////////////////////////////////////////////////////////////////
//	
//	
//	// INSTÀNCIA DE PROCÉS
//	////////////////////////////////////////////////////////////////////////////////
//	
//	public List<JbpmProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId);
//	public List<JbpmProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(String processName, Long entornId);
//	public List<JbpmProcessInstance> getProcessInstanceTree(String rootProcessInstanceId);
//	public JbpmProcessInstance getProcessInstance(String processInstanceId);
//	public JbpmProcessInstance getRootProcessInstance(String processInstanceId);
//	public List<String> findRootProcessInstancesWithTasksCommand( // 3.0
//			String actorId,
//			List<String> processInstanceIds,
//			boolean nomesMeves,
//			boolean nomesTasquesPersonals,
//			boolean nomesTasquesGrup);
//	
//	public JbpmProcessInstance startProcessInstanceById(String actorId, String processDefinitionId, Map<String, Object> variables);
//	public void signalProcessInstance(String processInstanceId, String transitionName);
//	public void deleteProcessInstance(String processInstanceId);
//	public void suspendProcessInstances(String[] processInstanceIds);
//	public void resumeProcessInstances(String[] processInstanceIds);
//	
//	public void changeProcessInstanceVersion(String processInstanceId, int newVersion);
//	
//	// VARIABLES DE PROCÉS
//	////////////////////////////////////////////////////////////////////////////////
//	public Map<String, Object> getProcessInstanceVariables(String processInstanceId);
//	public Object getProcessInstanceVariable(String processInstanceId, String varName);
//
//	public void setProcessInstanceVariable(String processInstanceId, String varName, Object value);
//	public void deleteProcessInstanceVariable(String processInstanceId, String varName);
//
//	
//	// INSTÀNCIA DE TASQUES
//	////////////////////////////////////////////////////////////////////////////////
//
//	public JbpmTask getTaskById(String taskId); // Instancia de tassca
//	public List<JbpmTask> findTaskInstancesForProcessInstance(String processInstanceId);
//	public Long getTaskInstanceIdByTokenId(Long tokenId);
//	public ResultatConsultaPaginad<JbpmTask> tascaFindByFiltrePaginat(
//			Long entornId,							// Id de l'entorn
//			Long expedientTipusId,					// Id del tipus d'expedient
//			Long expedientId,						// Id de l'expedient
//			String expedientTitol,					// Títol de l'expedient
//			String expedientNumero,					// Número de l'expedient
//			String taskName,						// Nom de la tasca
//			String taskTitol,						// Títol de la tasca
//			String actorId,							// Id de l'actor
//			Date dataCreacioInici,					// Data de creació de la tasca - Inici de filtre
//			Date dataCreacioFi,						// Data de creació de la tasca - Fi de filtre
//			Date dataLimitInici,					// Data límit de la tasca - Inici de filtre
//			Date dataLimitFi,						// Data límit de la tasca - Fi de filtre
//			Integer prioritat,						// Priortat de la tasca
//			boolean mostrarAssignadesUsuari,		// Mostrar tasques assignades a un usuari
//			boolean mostrarAssignadesGrup,			// Mostrar tasques assignades a un grup
//			boolean nomesPendents,					// Mostrar només les tasques pendents
//			boolean nomesCount,						// Mostrar només el nombre de tasques
//			PaginacioParamsDto paginacioParams);	// Paràmetres de la paginació
//
//			/*
//			| V3
//			|- TascaHelper (v3)
//			|		- findTasquesPerExpedient				=> ExpedientServiceImpl::findParticipants -> BaseExpedientController::mostrarInformacioExpedientPerPipella
//			|														- ExpedientDadaController::dades
//			|														- ExpedientDadaController::dadesAmbOcults
//			|															- ExpedientDocumentController::document
//			|														- ExpedientV3Controller::info
//			|														- ExpedientTerminiV3Controller::terminis
//			|- TascaServiceImpl	
//			|		- findTasquesPerFiltrePaginat 			=> TascaLlistatV3Controller::datatable
//			|
//			| V2.6
//			|- TascaService
//			|		- findTasquesFiltre 					=> TascaConsultaController::getPaginaTasques / consultaDissenyResultat
//			|												=> TascaConsultaController::getPaginaTasquesGrup / grupLlistatGet
//			|												=> TascaLlistatController::getPaginaTasquesPersonals / personaLlistatGet
//			|		- findTasquesGrupTramitacio				=> TramitacioServiceImpl(ws)::consultaTasquesGrup
//			|												=> TramitacioServiceImpl(ws)::consultaTasquesGrupByCodi
//			|												=> IndexController::index26
//			|		- findTasquesPersonalsTramitacio		=> TramitacioServiceImpl(ws)::consultaTasquesPersonals
//			|												=> TramitacioServiceImpl(ws)::consultaTasquesPersonalsByCodi
//			|												=> TramitacioServiceImpl(ws)::alliberarTasca
//			|												=> IndexController::index26
//			|		- isTasquesGrupTramitacio				=> TramitacioServiceImpl(ws)::agafarTasca
//			|		- countTasquesGrupEntorn				=> IndexController::index26
//			|												=> TascaController::massivaSeleccio
//			|		- countTasquesGrupFiltre				=> TascaLlistatController::getPaginaTasquesCountGrup / personaLlistatGet
//			|		- countTasquesPersonalsEntorn			=> IndexController::index26
//			|												=> TascaController::massivaSeleccio
//			|		- countTasquesPersonalsFiltre			=> TascaLlistatController::getPaginaTasquesPersonalsCount / grupLlistatGet
//			|- ExpedientService
//			|		- actualizarCacheExpedient / editar 	=> ExpedientEditarController::editarPost
//			*/
//	public LlistatIds findListTasks( // 3.0
//			String responsable,
//			String tasca,
//			String tascaSel,
//			List<Long> idsPIExpedients,
//			Date dataCreacioInici,
//			Date dataCreacioFi,
//			Integer prioritat,
//			Date dataLimitInici,
//			Date dataLimitFi,
//			PaginacioParamsDto paginacioParams,
//			boolean nomesTasquesPersonals,
//			boolean nomesTasquesGrup,
//			boolean nomesAmbPendents);
//	public void takeTaskInstance(String taskId, String actorId);
//	public void releaseTaskInstance(String taskId);
//	public JbpmTask reassignTaskInstance(String taskId, String expression, Long entornId);
//	public JbpmTask cloneTaskInstance(String taskId, String actorId, Map<String, Object> variables);
//	public JbpmTask startTaskInstance(String taskId);
//	public JbpmTask cancelTaskInstance(String taskId);
//	public JbpmTask suspendTaskInstance(String taskId);
//	public JbpmTask resumeTaskInstance(String taskId);
//	public void endTaskInstance(String taskId, String outcome);
//	public void describeTaskInstance(String taskId, String titol, String description); // Revisar necessitat!!!
//
//	// Retrocés - Treure???
//	public void setTaskInstanceActorId(String taskInstanceId, String actorId);
//	public void setTaskInstancePooledActors(String taskInstanceId, String[] pooledActors);
//		
//	// Transicions (Sequence flow)
//	public List<String> findStartTaskOutcomes(String jbpmId, String taskName);
//	public List<String> findTaskInstanceOutcomes(String taskInstanceId);
//	public List<String> findArrivingNodeNames(String tokenId); // Retrocedir??
//	
//	
//	// VARIABLES DE TASQUES
//	////////////////////////////////////////////////////////////////////////////////
//	
//	public Map<String, Object> getTaskInstanceVariables(String taskId);
//	public Object getTaskInstanceVariable(String taskId, String varName);
//	public void setTaskInstanceVariable(String taskId, String codi, Object valor);
//	public void setTaskInstanceVariables(String taskId, Map<String, Object> variables, boolean deleteFirst);
//	public void deleteTaskInstanceVariable(String taskId, String varName);
//	
//	
//	// FILS D'EXECUCIÓ (Token / Execution path)
//	////////////////////////////////////////////////////////////////////////////////
//	public JbpmToken getTokenById(String tokenId);
//	public Map<String, JbpmToken> getActiveTokens(String processInstanceId);
//	public Map<String, JbpmToken> getAllTokens(String processInstanceId);
//	
//	public void tokenRedirect(long tokenId, String nodeName, boolean cancelTasks, boolean enterNodeIfTask, boolean executeNode);
//	public boolean tokenActivar(long tokenId, boolean activar);
//	public void signalToken(long tokenId, String transitionName);
//	
//	// ACCIONS
//	////////////////////////////////////////////////////////////////////////////////
//	public Map<String, Object> evaluateScript(String processInstanceId, String script, Set<String> outputNames);
//	public Object evaluateExpression(String taskInstanceInstanceId, String processInstanceId, String expression, Map<String, Object> valors);
//	
//	public List<String> listActions(String jbpmId);
//	public void executeActionInstanciaProces(String processInstanceId, String actionName);
//	public void executeActionInstanciaTasca(String taskInstanceId, String actionName);
//	public void retrocedirAccio(String processInstanceId, String actionName, List<String> params);
//
//	// TIMERS
//	////////////////////////////////////////////////////////////////////////////////
//	public List<Timer> findTimersWithProcessInstanceId(String processInstanceId);
//	public void suspendTimer(long timerId, Date dueDate);
//	public void resumeTimer(long timerId, Date dueDate);
//	
//	// A ELIMINAR
//	////////////////////////////////////////////////////////////////////////////////
//	public List<JbpmTask> findTasks(List<Long> ids); // 2.6
//	public List<JbpmTask> findPersonalTasks(String usuariBo); // 2.6
//
//	// A MOURE FORA DE LA FUNCIONALITAT DEL MOTOR DE WORKFLOW
//	////////////////////////////////////////////////////////////////////////////////
//	
//	// Expedients
//	public ProcessInstanceExpedient expedientFindByProcessInstanceId(String processInstanceId);
//	public ResultatConsultaPaginadaJbpm<Long> expedientFindByFiltre(
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
//			PaginacioParamsDto paginacioParams,
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
//	// Tasques en segón pla 
//	public void marcarFinalitzar(String taskId, Date marcadaFinalitzar, String outcome);
//	public void marcarIniciFinalitzacioSegonPla(String taskId, Date iniciFinalitzacio);
//	public void guardarErrorFinalitzacio(String taskId, String errorFinalitzacio);
//	public List<Object[]> getTasquesSegonPlaPendents();
//	
//	// Eliminació de definicions de procés
//	public List<String> findDefinicionsProcesIdNoUtilitzadesByEntorn(Long entornId);
//	public List<String> findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(Long expedientTipusId);
//	public List<ProcessInstanceExpedient> findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
//			Long expedientTipusId,
//			Long processDefinitionId);
//	
//	// Retroacció
//	public Map<Token, List<ProcessLog>> getProcessInstanceLogs(String processInstanceId);
//	public long addProcessInstanceMessageLog(String processInstanceId, String message);
//	public long addTaskInstanceMessageLog(String taskInstanceId, String message);
//	public Long getVariableIdFromVariableLog(long variableLogId);
//	public Long getTaskIdFromVariableLog(long variableLogId);	
//	public void cancelProcessInstance(long id);
//	public void revertProcessInstanceEnd(long id);
//	public void cancelToken(long id);
//	public void revertTokenEnd(long id);
//	public JbpmTask findEquivalentTaskInstance(long tokenId, long taskInstanceId);
//	public boolean isProcessStateNodeJoinOrFork(long processInstanceId, String nodeName);
//	public boolean isJoinNode(long processInstanceId, String nodeName);
//	public ProcessLog getProcessLogById(Long id);
//	public Node getNodeByName(long processInstanceId, String nodeName);
//	public boolean hasStartBetweenLogs(long begin, long end, long taskInstanceId);
//	public void deleteProcessInstanceTreeLogs(String rootProcessInstanceId);
//
//	
//	// NO S'UTILITZA
//	////////////////////////////////////////////////////////////////////////////////
////	public void describeProcessInstance(String processInstanceId,String description);  
////	public void suspendProcessInstance(String processInstanceId);
////	public void resumeProcessInstance(String processInstanceId);
////	public List<JbpmProcessInstance> findProcessInstancesWithProcessDefinitionName(String processName);
////	public JbpmTask reassignTaskInstance(String taskId, String expression);
////	public LlistatIds findListTasks( // 2.6
////			String usuariBo,
////			String tascaSel,
////			String titol,
////			List<Long> idsPIExpedients,
////			Date dataCreacioInici,
////			Date dataCreacioFi,
////			Integer prioritat,
////			Date dataLimitInici,
////			Date dataLimitFi,
////			int firstRow,
////			int maxResults,
////			String sort,
////			boolean asc,
////			MostrarTasquesDto mostrarTasques);
////	public LlistatIds findListPersonalTasks( // 2.6
////			String usuariBo,
////			String tasca,
////			List<Long> idsPIExpedients,
////			Date dataCreacioInici,
////			Date dataCreacioFi,
////			Integer prioritat,
////			Date dataLimitInici,
////			Date dataLimitFi,
////			int firstRow,
////			int maxResults,
////			String sort,
////			boolean asc);
////	public LlistatIds findListGroupTasks( // 2.6
////			String usuariBo,
////			String tasca,
////			List<Long> idsPIExpedients,
////			Date dataCreacioInici,
////			Date dataCreacioFi,
////			Integer prioritat,
////			Date dataLimitInici,
////			Date dataLimitFi,
////			int firstRow,
////			int maxResults,
////			String sort,
////			boolean asc);
////	public LlistatIds findListIdsTasks(String actorId, List<Long> idsPIExpedients); // 2.6
////	public List<JbpmTask> findPersonalTasks(List<Long> ids, String usuariBo); // 2.6
////	public List<Long> findIdsRootProcessInstanceGroupTasks(List<Long> ids, String usuariBo); // 2.6
////	public List<JbpmTask> findGroupTasks(List<Long> ids, String usuariBo); // 2.6
////	public LlistatIds findListIdsPersonalTasks(String actorId, List<Long> idsPIExpedients); // 2.6
////	public LlistatIds findListIdsGroupTasks(String actorId, List<Long> idsPIExpedients); // 2.6
////	public List<JbpmTask> findGroupTasks(String actorId); // 2.6
////	public void retryJob(Long jobId);
	
}
