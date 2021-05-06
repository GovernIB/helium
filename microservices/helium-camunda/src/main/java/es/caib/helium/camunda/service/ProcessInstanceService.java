package es.caib.helium.camunda.service;

import es.caib.helium.camunda.model.WProcessInstance;

import java.util.List;
import java.util.Map;

public interface ProcessInstanceService {

    // Consulta de instància de procés
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Obté totes les instàncies de procés d'una definició de procés, donat el seu codi
     *
     * @param processDefinitionId
     * @return
     */
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId);

    /**
     * Obté totes les instàncies de procés d'una definició de procés, donat el seu nom
     *
     * @param processName
     * @return
     */
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionName(String processName);

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
            Long entornId);

    /**
     * Obté les instàncies de procés del procés principal, i dels subprocesos donat l'identificador del procés principal
     *
     * @param rootProcessInstanceId
     * @return
     */
    public List<WProcessInstance> getProcessInstanceTree(String rootProcessInstanceId);

    /**
     * Obté la instància de procés donat el seu codi
     *
     * @param processInstanceId
     * @return
     */
    public WProcessInstance getProcessInstance(String processInstanceId);

    /**
     * Obté la instància de procés principal donat el codi de la instància de procés principal, o d'un dels seus subprocesos
     *
     * @param processInstanceId
     * @return
     */
    public WProcessInstance getRootProcessInstance(String processInstanceId);

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
            boolean nomesTasquesGrup);

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
            Map<String, Object> variables);

    /**
     * Envia un disparador extern a una instància de procés
     *
     * @param processInstanceId
     * @param transitionName
     */
    public void signalProcessInstance(
            String processInstanceId,
            String transitionName);

    /**
     * Elimina una instància de procés existent
     *
     * @param processInstanceId
     */
    public void deleteProcessInstance(String processInstanceId);

    /**
     * Suspèn les instàncies de procés indicades
     *
     * @param processInstanceIds
     */
    public void suspendProcessInstances(String[] processInstanceIds);

    /**
     * Activa les instàncies de procés indicades
     *
     * @param processInstanceIds
     */
    public void resumeProcessInstances(String[] processInstanceIds);

    /**
     * Canvia la versió de la instància de procés indicada
     *
     * @param processInstanceId
     * @param newVersion
     */
    public void changeProcessInstanceVersion(
            String processInstanceId,
            int newVersion);

}
