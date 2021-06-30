package es.caib.helium.camunda.service;

import es.caib.helium.camunda.model.DelegationInfo;
import es.caib.helium.camunda.model.ResultatCompleteTask;
import es.caib.helium.camunda.model.WTaskInstance;

import java.util.List;

public interface TaskInstanceService {

    // Consulta de tasques
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Obté la instància d0una tasca donat el seu codi
     *
     * @param taskId
     * @return
     */
    public WTaskInstance getTaskById(String taskId); // Instancia de tasca

    /**
     * Obté la llista de instàncies de tasca d'una instància de procés
     *
     * @param processInstanceId
     * @return
     */
    public List<WTaskInstance> findTaskInstancesByProcessInstanceId(String processInstanceId);

    /**
     * Obté l'identificador de la instància de tasca activa donat el seu token d'execució
     *
     * @param executionTokenId
     * @return
     */
    public Long getTaskInstanceIdByExecutionTokenId(Long executionTokenId);


    // Tramitació de tasques
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Agafa una tasca per a ser tramitada per un usuari
     *
     * @param taskId
     * @param actorId
     */
    public void takeTaskInstance(
            String taskId,
            String actorId);

    /**
     * Allibera una tasca per a que pugui ser tramitada per altres usuaris
     *
     * @param taskId
     */
    public void releaseTaskInstance(String taskId);

    /**
     * Inicia la tramitació d'una tasca
     *
     * @param taskId
     * @return
     */
    public WTaskInstance startTaskInstance(String taskId);
//	public void endTaskInstance(String taskId, String outcome);

    /**
     * Completa la tramitació d'una tasca
     *
     * @param task
     * @param outcome
     * @return
     */
    public ResultatCompleteTask completeTaskInstance(
            String taskId,
            String outcome);

    /**
     * Cancel·la una tasca i continua amb l'execució de la instància de procés
     *
     * @param taskId
     * @return
     */
    public WTaskInstance cancelTaskInstance(String taskId);

    /**
     * Suspén una tasca
     *
     * @param taskId
     * @return
     */
    public WTaskInstance suspendTaskInstance(String taskId);

    /**
     * Activa una tasca suspesa
     *
     * @param taskId
     * @return
     */
    public WTaskInstance resumeTaskInstance(String taskId);

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
            Long entornId);

    /**
     * Delega una tasca a un altre usuari
     *
     * @param task
     * @param actorId
     * @param comentari
     * @param supervisada
     */
    public void delegateTaskInstance(
            WTaskInstance task,
            String actorId,
            String comentari,
            boolean supervisada);

    /**
     * Obté la informació d'una delegació realitzada
     *
     * @param taskId
     * @param includeActors
     * @return
     */
    public DelegationInfo getDelegationTaskInstanceInfo(
            String taskId,
            boolean includeActors);

    /**
     * Cancel·la una delegació realitzada, i retorna la tasca a l'usuari original
     * @param task
     */
    public void cancelDelegationTaskInstance(WTaskInstance task);

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
            String infoCache);

}
