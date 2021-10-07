package net.conselldemallorca.helium.api.service;

import net.conselldemallorca.helium.api.dto.ExpedientDto;
import net.conselldemallorca.helium.api.dto.LlistatIds;
import net.conselldemallorca.helium.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.api.dto.ResultatConsultaPaginada;
import net.conselldemallorca.helium.api.exception.HeliumJbpmException;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

/**
 * Interfície comú dels motors de workflow amb els mètodes necessaris per desplegar, consultar,
 * executar i mantenir els workflows de processos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public interface WorkflowEngineApi {
	
	public static final String VAR_TASCA_DELEGACIO = "H3l1um#tasca.delegacio";
	public static final String VAR_TASCA_VALIDADA = "H3l1um#tasca.validada";
	
	public enum MostrarTasquesDto {
		MOSTRAR_TASQUES_TOTS,
		MOSTRAR_TASQUES_NOMES_GROUPS,
		MOSTRAR_TASQUES_NOMES_PERSONALS
	}
	
	// DEFINICIÓ DE PROCÉS
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	// Desplegaments
	////////////////////////////////////////////////////////////////////////////////
	
	/*
	 * Deployment:
	 * 
	 * getId()
	 * 		Activiti: deploymentId
	 * 		Jpmb: processDefinitionId
	 * getKey()
	 * getVersion()
	 * getProcessDefinitions()
	 * 		A Activiti un desplegament pot incloure diferents definicions de procés, 
	 * 		així que hem substituit la cridada getProcessDefinition() utilitzada per a 
	 * 		obtenir les tasques de la definició de procés desplegada, per aquesta, 
	 * 		que retora una llista
	 * 
	 * 		S'han modificat els mètodes de deploy, ja que ara poden generar vàries 
	 * 		definicions de procés. 
	 */
	
	/**
	 * Desplega un model BPMN2.0
	 * 
	 * @param nomArxiu
	 * @param contingut
	 * @return
	 */
	public WDeployment desplegar(
            String nomArxiu,
            byte[] contingut) throws HeliumJbpmException;
	
	// Afegim el següent mètode per a compatibilitat amb Activiti, on un desplegament pot 
	// incloure diverses definicions de procés. 
	/**
	 * Obté les dades d'un desplegament concret
	 * 
	 * @param deploymentId
	 * @return
	 */
	public WDeployment getDesplegament(String deploymentId) throws HeliumJbpmException;
	
	/**
	 * Elimina un desplegament concret
	 * 
	 * @param deploymentId
	 */
	public void esborrarDesplegament(String deploymentId) throws HeliumJbpmException;
	
	
	/**
	 * Obté els noms dels recursos desplegats en un desplegament concret
	 * 
	 * @param deploymentId
	 * @return
	 */
	public Set<String> getResourceNames(String deploymentId) throws HeliumJbpmException;
	
	/**
	 * Obté el contingut d'un recurs d'un desplegament concret. El recurs s'identifica amb el nom
	 * 
	 * @param deploymentId
	 * @param resourceName
	 * @return
	 */
	public byte[] getResourceBytes(
            String deploymentId,
            String resourceName) throws HeliumJbpmException;
	
	/**
	 * Actualitza els recursos de tipus acció, sense canviar la versió d'un desplagament
	 * 
	 * @param deploymentId
	 * @param deploymentContent
	 */
	public void updateDeploymentActions(
            Long deploymentId,
			byte[] deploymentContent) throws HeliumJbpmException, Exception;

	/**
	 * Actualitza els recursos de tipus acció, sense canviar la versió d'un desplagament
	 *
	 * @param deploymentOrigenId
	 * @param deploymentDestiId
	 */
	public void propagateDeploymentActions(
			String deploymentOrigenId,
			String deploymentDestiId) throws HeliumJbpmException;
	
	// Consulta de Definicions de Procés
	////////////////////////////////////////////////////////////////////////////////
	
	/*
	 * getProcessDefinition().getName() == getName()
	 * getVersion()
	 * getKey()
	 * getName()
	 */
	
	/**
	 * Obté una definició de procés donat el codi de desplegament i de la definició de procés 
	 * @param processDefinitionId
	 * @return
	 */
	public WProcessDefinition getProcessDefinition(
//            String deploymentId,
            String processDefinitionId) throws HeliumJbpmException;
	
	/**
	 * Obté les definicions de procés dels subprocessos donat el codi de desplegament i de la definició de procés pare
	 * 
	 * @param processDefinitionId
	 * @return
	 */
	public List<WProcessDefinition> getSubProcessDefinitions(
//            String deploymentId,
            String processDefinitionId) throws HeliumJbpmException;
	
	/**
	 * Obté els noms de les tasques d'una definició de procés donat el desplegament i el codi de definició de procés
	 * 
	 * @param deploymentId
	 * @param processDefinitionId
	 * @return
	 */
	public List<String> getTaskNamesFromDeployedProcessDefinition(
            String deploymentId,
            String processDefinitionId) throws HeliumJbpmException;
	
	/**
	 * Obté el nom de la tasca inicial d'una definició de procés
	 * 
	 * @param processDefinitionId
	 * @return
	 */
	public String getStartTaskName(String processDefinitionId) throws HeliumJbpmException;
	
	/**
	 * Obté la definició de procés d'una instància de procés
	 * @param processInstanceId
	 * @return
	 */
	public WProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) throws HeliumJbpmException;

	public void updateSubprocessDefinition(String pd1, String pd2) throws HeliumJbpmException;
	
	// DEFINICIÓ DE TASQUES
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	
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
	
	// Consulta de instància de procés
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Obté totes les instàncies de procés d'una definició de procés, donat el seu codi
	 * 
	 * @param processDefinitionId
	 * @return
	 */
	public List<WProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId) throws HeliumJbpmException;
	
	/**
	 * Obté totes les instàncies de procés d'una definició de procés, donat el seu nom
	 * 
	 * @param processName
	 * @return
	 */
	public List<WProcessInstance> findProcessInstancesWithProcessDefinitionName(String processName) throws HeliumJbpmException;
	
	/**
	 * Obté totes les instàncies de procés d'una definició de procés, donat el seu nom i l'entorn Helium
	 * 
	 * @param processName
	 * @param entornId
	 * @return
	 */
	// Com a entornId podem utilitzar el tenantId de la instància de procés, o la categoria de la definició de procés
	public List<WProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(
            String processName,
			String entornId) throws HeliumJbpmException;

	/**
	 * Obté les instàncies de procés del procés principal, i dels subprocessos donat l'identificador del procés principal
	 * 
	 * @param rootProcessInstanceId
	 * @return
	 */
	public List<WProcessInstance> getProcessInstanceTree(String rootProcessInstanceId) throws HeliumJbpmException;
	
	/**
	 * Obté la instància de procés donat el seu codi
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public WProcessInstance getProcessInstance(String processInstanceId) throws HeliumJbpmException;
	
	/**
	 * Obté la instància de procés principal donat el codi de la instància de procés principal, o d'un dels seus subprocessos
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public WProcessInstance getRootProcessInstance(String processInstanceId) throws HeliumJbpmException;
	
	/**
	 * Obté les instàncies de procés principals filtrades
	 * 
	 * @param actorId
	 * @param processInstanceIds
	 * @param nomesMeves
	 * @param nomesTasquesPersonals
	 * @param nomesTasquesGrup
	 * @return
	 */
	public List<String> findRootProcessInstances(
            String actorId,
            List<String> processInstanceIds,
            boolean nomesMeves,
            boolean nomesTasquesPersonals,
            boolean nomesTasquesGrup) throws HeliumJbpmException;
	
	// Tramitació
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Inicia una nova instància de procés
	 * 
	 * @param actorId
	 * @param processDefinitionId
	 * @param variables
	 * @return
	 */
	public WProcessInstance startProcessInstanceById(
            String actorId,
            String processDefinitionId,
            Map<String, Object> variables) throws HeliumJbpmException;
	
	/**
	 * Envia un disparador extern a una instància de procés
	 * 
	 * @param processInstanceId
	 * @param transitionName
	 */
	public void signalProcessInstance(
            String processInstanceId,
            String transitionName) throws HeliumJbpmException;
	
	/**
	 * Elimina una instància de procés existent
	 * 
	 * @param processInstanceId
	 */
	public void deleteProcessInstance(String processInstanceId) throws HeliumJbpmException;
	
	/**
	 * Suspen les instàncies de procés indicades
	 * 
	 * @param processInstanceIds
	 */
	public void suspendProcessInstances(String[] processInstanceIds) throws HeliumJbpmException;
	
	/**
	 * Activa les instàncies de procés indicades
	 * 
	 * @param processInstanceIds
	 */
	public void resumeProcessInstances(String[] processInstanceIds) throws HeliumJbpmException;
	
	/**
	 * Canvia la versió de la instància de procés indicada
	 * 
	 * @param processInstanceId
	 * @param newVersion
	 */
	public void changeProcessInstanceVersion(
            String processInstanceId,
            int newVersion) throws HeliumJbpmException;
	
	
	// VARIABLES DE PROCÉS
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	// Consulta de variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Obté les variables d'una instància de procés concreta
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public Map<String, Object> getProcessInstanceVariables(String processInstanceId) throws HeliumJbpmException;
	
	/**
	 * Obté una variable d'una instància de procés concreta
	 * 
	 * @param processInstanceId
	 * @param varName
	 * @return
	 */
	public Object getProcessInstanceVariable(
            String processInstanceId,
            String varName) throws HeliumJbpmException;

	
	// Actualització de variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Assigna el valor indicat a una variable de la instància de procés
	 * 
	 * @param processInstanceId
	 * @param varName
	 * @param value
	 */
	public void setProcessInstanceVariable(
            String processInstanceId,
            String varName,
            Object value) throws HeliumJbpmException;
	
	/**
	 * Elimina una variable d'una instància de procés
	 * 
	 * @param processInstanceId
	 * @param varName
	 */
	public void deleteProcessInstanceVariable(
            String processInstanceId,
            String varName) throws HeliumJbpmException;

	
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
	
	// Consulta de tasques
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Obté la instància d0una tasca donat el seu codi
	 * 
	 * @param taskId
	 * @return
	 */
	public WTaskInstance getTaskById(String taskId) throws HeliumJbpmException; // Instancia de tasca
	
	/**
	 * Obté la llista de instàncies de tasca d'una instància de procés
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public List<WTaskInstance> findTaskInstancesByProcessInstanceId(String processInstanceId) throws HeliumJbpmException;
	
	/**
	 * Obté l'identificador de la instància de tasca activa donat el seu token d'execució
	 *  
	 * @param executionTokenId
	 * @return
	 */
	public String getTaskInstanceIdByExecutionTokenId(String executionTokenId) throws HeliumJbpmException;
	
	/**
	 * Obté un llistat paginat de instàncies de tasques donat un filtre concret 
	 * 
	 * @param entornId
	 * @param actorId
	 * @param taskName
	 * @param titol
	 * @param expedientId
	 * @param expedientTitol
	 * @param expedientNumero
	 * @param expedientTipusId
	 * @param dataCreacioInici
	 * @param dataCreacioFi
	 * @param prioritat
	 * @param dataLimitInici
	 * @param dataLimitFi
	 * @param mostrarAssignadesUsuari
	 * @param mostrarAssignadesGrup
	 * @param nomesPendents
	 * @param paginacioParams
	 * @param nomesCount
	 * @return
	 */
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
            boolean nomesCount) throws HeliumJbpmException;

	/**
	 * Obté un llistat d'identificadors de instàncies de tasques donat un filtre concret
	 * 
	 * @param responsable
	 * @param tasca
	 * @param tascaSel
	 * @param idsPIExpedients
	 * @param dataCreacioInici
	 * @param dataCreacioFi
	 * @param prioritat
	 * @param dataLimitInici
	 * @param dataLimitFi
	 * @param paginacioParams
	 * @param nomesTasquesPersonals
	 * @param nomesTasquesGrup
	 * @param nomesAmbPendents
	 * @return
	 */
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
            boolean nomesAmbPendents) throws HeliumJbpmException;
	
	
	// Tramitació de tasques
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Agafa una tasca per a ser tramitada per un usuari
	 *  
	 * @param taskId
	 * @param actorId
	 */
	public WTaskInstance takeTaskInstance(
            String taskId,
            String actorId) throws HeliumJbpmException;
	
	/**
	 * Allibera una tasca per a que pugui ser tramitada per altres usuaris
	 * 
	 * @param taskId
	 */
	public WTaskInstance releaseTaskInstance(String taskId) throws HeliumJbpmException;
	
	/**
	 * Inicia la tramitació d'una tasca
	 * 
	 * @param taskId
	 * @return
	 */
	public WTaskInstance startTaskInstance(String taskId) throws HeliumJbpmException;

	/**
	 * Completa la tramitació d'una tasca
	 *
	 * @param taskId
	 * @param outcome
	 * @return
	 */
	public void endTaskInstance(String taskId, String outcome) throws HeliumJbpmException;
//	public ResultatCompleteTask completeTaskInstance(
//            WTaskInstance task,
//            String outcome) throws HeliumJbpmException;

	/**
	 * Cancel·la una tasca i continua amb l'execució de la instància de procés
	 * 
	 * @param taskId
	 * @return
	 */
	public WTaskInstance cancelTaskInstance(String taskId) throws HeliumJbpmException;
	
	/**
	 * Suspén una tasca
	 * 
	 * @param taskId
	 * @return
	 */
	public WTaskInstance suspendTaskInstance(String taskId) throws HeliumJbpmException;
	
	/**
	 * Activa una tasca suspesa
	 * 
	 * @param taskId
	 * @return
	 */
	public WTaskInstance resumeTaskInstance(String taskId) throws HeliumJbpmException;
	
	// Reassignació / Delegació
	
	/**
	 * Reassigna una instància de tasca segons la expressió donada
	 * 
	 * @param taskId
	 * @param expression
	 * @param entornId
	 * @return
	 */
	public WTaskInstance reassignTaskInstance(
            String taskId,
            String expression,
            Long entornId) throws HeliumJbpmException;
	
//	/**
//	 * Delega una tasca a un altre usuari
//	 *
//	 * @param task
//	 * @param actorId
//	 * @param comentari
//	 * @param supervisada
//	 */
//	public void delegateTaskInstance(
//            WTaskInstance task,
//            String actorId,
//            String comentari,
//            boolean supervisada) throws HeliumJbpmException;
//
//	/**
//	 * Obté la informació d'una delegació realitzada
//	 *
//	 * @param taskId
//	 * @param includeActors
//	 * @return
//	 */
//	public DelegationInfo getDelegationTaskInstanceInfo(
//            String taskId,
//            boolean includeActors) throws HeliumJbpmException;
//
//	/**
//	 * Cancel·la una delegació realitzada, i retorna la tasca a l'usuari original
//	 * @param task
//	 */
//	public void cancelDelegationTaskInstance(WTaskInstance task) throws HeliumJbpmException;
	
	// Caché
	
	/**
	 * Desa la informació de caché de la tasca
	 * 
	 * @param taskId
	 * @param titol
	 * @param infoCache
	 */
	public void updateTaskInstanceInfoCache(
            String taskId,
            String titol,
            String infoCache) throws HeliumJbpmException;
	
	// VARIABLES DE TASQUES
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Obté les variables de la instància de procés.
	 * @param taskId
	 * @return Retorna un Map de codi i valor de les variables de la instància de procés.
	 */
	public Map<String, Object> getTaskInstanceVariables(String taskId) throws HeliumJbpmException;
	
	/** 
	 * Obé el valor d'una variable d'una instàcia de procés.
	 * @param taskId
	 * @param varName
	 * @return
	 */
	public Object getTaskInstanceVariable(String taskId, String varName) throws HeliumJbpmException;
	
	/** 
	 * Fixa el valor de la variable de la instància de procés.
	 * @param taskId
	 * @param varName
	 * @param valor
	 */
	public void setTaskInstanceVariable(String taskId, String varName, Object valor) throws HeliumJbpmException;
	
	/**
	 * Fixa el valor de vàries variables a la vegada de la instància de la tasca. 
	 * Es pot especificar si esborrar abans les variables.
	 * @param taskId
	 * @param variables
	 * @param deleteFirst
	 */
	public void setTaskInstanceVariables(String taskId, Map<String, Object> variables, boolean deleteFirst) throws HeliumJbpmException;
	
	/** Esborra una variable de la instància de la tasca
	 * 
	 * @param taskId
	 * @param varName
	 */
	public void deleteTaskInstanceVariable(String taskId, String varName) throws HeliumJbpmException;
	
	//TODO: Comprovar si s'ha d'implementr el mètode per finalitzar expedients demanat en la versió 3.2.45 
	// finalitzarExpedient(, boolean cancel·larTasquesActives)
	
	// FILS D'EXECUCIÓ (Token / Execution path)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Obté la informació del token per identificador.
	 * 
	 * @param tokenId
	 * @return
	 */
	public WToken getTokenById(String tokenId) throws HeliumJbpmException;
	
	/** Consulta la llista de tokens actius per una instància de procés.
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public Map<String, WToken> getActiveTokens(String processInstanceId) throws HeliumJbpmException;
	
	/** Retorna una llista de tots els tokens d'una instància de procés.
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public Map<String, WToken> getAllTokens(String processInstanceId) throws HeliumJbpmException;
	
	/** Mètode per redirigir la execució cap a un altre token
	 * 
	 * @param tokenId
	 * @param nodeName
	 * @param cancelTasks
	 * @param enterNodeIfTask
	 * @param executeNode
	 */
	public void tokenRedirect(String tokenId, String nodeName, boolean cancelTasks, boolean enterNodeIfTask, boolean executeNode) throws HeliumJbpmException;
	
	/** Mètode per activar o desactivar un token.
	 * 
	 * @param tokenId
	 * @param activar
	 * @return
	 */
	public boolean tokenActivar(String tokenId, boolean activar) throws HeliumJbpmException;
	
	/** Mètode per enviar un senyal a un token per a que avanci per una transició.
	 * 
	 * @param tokenId
	 * @param transitionName
	 */
	public void signalToken(String tokenId, String transitionName) throws HeliumJbpmException;
	
	// ACCIONS
	////////////////////////////////////////////////////////////////////////////////
	public Map<String, Object> evaluateScript(
            String processInstanceId,
            String script,
            Set<String> outputNames) throws HeliumJbpmException;
	public Object evaluateExpression(
            String taskInstanceInstanceId,
            String processInstanceId,
            String expression,
            Map<String, Object> valors) throws HeliumJbpmException;
	
	public List<String> listActions(String jbpmId) throws HeliumJbpmException;
	public void executeActionInstanciaProces(
            String processInstanceId,
            String actionName,
            String processDefinitionPareId) throws HeliumJbpmException;
	public void executeActionInstanciaTasca(
            String taskInstanceId,
            String actionName,
            String processDefinitionPareId) throws HeliumJbpmException;


	// TIMERS
	////////////////////////////////////////////////////////////////////////////////
	//public List<Timer> findTimersWithProcessInstanceId(String processInstanceId) throws HeliumJbpmException;

	public void suspendTimer(String timerId, Date dueDate) throws HeliumJbpmException;
	public void resumeTimer(String timerId, Date dueDate) throws HeliumJbpmException;


	// AREES I CARRECS
	////////////////////////////////////////////////////////////////////////////////
	public List<String> findAreesByFiltre(String filtre) throws HeliumJbpmException;
	public List<String> findAreesByPersona(String personaCodi) throws HeliumJbpmException;
	public List<String> findRolsByPersona(String persona) throws HeliumJbpmException;
	public List<String[]> findCarrecsByFiltre(String filtre) throws HeliumJbpmException;
	public List<String> findPersonesByGrupAndCarrec(String areaCodi, String carrecCodi) throws HeliumJbpmException;
	public List<String> findCarrecsByPersonaAndGrup(String codiPersona, String codiArea) throws HeliumJbpmException;
	public List<String> findPersonesByCarrec(String codi) throws HeliumJbpmException;
	public List<String> findPersonesByGrup(String rol) throws HeliumJbpmException;

	// A ELIMINAR
	////////////////////////////////////////////////////////////////////////////////
//	public List<WTaskInstance> findTasks(List<Long> ids); // 2.6

	// A MOURE FORA DE LA FUNCIONALITAT DEL MOTOR DE WORKFLOW
	////////////////////////////////////////////////////////////////////////////////
	// Transicions (Sequence flow)

	public List<String> findStartTaskOutcomes(String jbpmId, String taskName) throws HeliumJbpmException;
	public List<String> findTaskInstanceOutcomes(String taskInstanceId) throws HeliumJbpmException;
	public List<String> findArrivingNodeNames(String tokenId) throws HeliumJbpmException; // Retrocedir??

	// Expedients

	public ExpedientDto expedientFindByProcessInstanceId(String processInstanceId) throws HeliumJbpmException;
	public ResultatConsultaPaginada<Long> expedientFindByFiltre(
            Long entornId,
            String actorId,
            Collection<Long> tipusIdPermesos,
            String titol,
            String numero,
            Long tipusId,
            Date dataCreacioInici,
            Date dataCreacioFi,
            Date dataFiInici,
            Date dataFiFi,
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
            boolean nomesCount) throws HeliumJbpmException;
			/*
			| V3
			|- ExpedientServiceImpl
			|		- consultaFindNomesIdsPaginat
			|		- consultaFindPaginat
			|		- findAmbFiltrePaginat
			|		- findIdsAmbFiltre
			*/

	public void desfinalitzarExpedient(String processInstanceId) throws HeliumJbpmException;
	/** Mètode per finalitzar l'expedient. */
	public void finalitzarExpedient(String[] processInstanceIds, Date dataFinalitzacio) throws HeliumJbpmException;


	// Tasques en segón pla

	public void marcarFinalitzar(String taskId, Date marcadaFinalitzar, String outcome, String rols) throws HeliumJbpmException;
	public void marcarIniciFinalitzacioSegonPla(String taskId, Date iniciFinalitzacio) throws HeliumJbpmException;
	public void guardarErrorFinalitzacio(String taskId, String errorFinalitzacio) throws HeliumJbpmException;
	public List<Object[]> getTasquesSegonPlaPendents() throws HeliumJbpmException;

	// Eliminació de definicions de procés
	public List<String> findDefinicionsProcesIdNoUtilitzadesByEntorn(Long entornId) throws HeliumJbpmException;
	public List<String> findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(Long expedientTipusId) throws HeliumJbpmException;
	public List<ExpedientDto> findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
            Long expedientTipusId,
            Long processDefinitionId) throws HeliumJbpmException;

	// Avaluació d'expressions
	/** Avalua una expressió amb uns valors de variables en el contexte.
	 *
	 * @param expression
	 * @param expectedClass
	 * @param context
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Object evaluateExpression(
            String expression,
            Class expectedClass,
            Map<String, Object> context) throws HeliumJbpmException;


	// Retroacció
	public void retrocedirAccio(String processInstanceId,
                                String actionName,
                                List<String> params,
                                String processDefinitionPareId) throws HeliumJbpmException;
//	public Map<Token, List<ProcessLog>> getProcessInstanceLogs(String processInstanceId);
//	public long addProcessInstanceMessageLog(String processInstanceId, String message);
//	public long addTaskInstanceMessageLog(String taskInstanceId, String message);
//	public Long getVariableIdFromVariableLog(long variableLogId);
//	public Long getTaskIdFromVariableLog(long variableLogId);
//	public void cancelProcessInstance(long id);
//	public void revertProcessInstanceEnd(long id);
//	public void cancelToken(long id);
//	public void revertTokenEnd(long id);
//	public WTaskInstance findEquivalentTaskInstance(long tokenId, long taskInstanceId);
//	public boolean isProcessStateNodeJoinOrFork(long processInstanceId, String nodeName);
//	public boolean isJoinNode(long processInstanceId, String nodeName);
//	public ProcessLog getProcessLogById(Long id);
//	public Node getNodeByName(long processInstanceId, String nodeName);
//	public boolean hasStartBetweenLogs(long begin, long end, long taskInstanceId);
//	public void deleteProcessInstanceTreeLogs(String rootProcessInstanceId);

	public void setTaskInstanceActorId(String taskInstanceId, String actorId) throws HeliumJbpmException;
	public void setTaskInstancePooledActors(String taskInstanceId, String[] pooledActors) throws HeliumJbpmException;

	/** Mètode per obtenir una definició de procés a partir del contingut comprimit del mateix.
	 * 
	 * @param zipInputStream
	 * @return
	 * @throws Exception
	 */
	public WProcessDefinition parse(ZipInputStream zipInputStream) throws Exception, HeliumJbpmException;

}
