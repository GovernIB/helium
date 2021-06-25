package es.caib.helium.camunda.service;

import es.caib.helium.camunda.model.WProcessDefinition;

import java.util.List;

public interface ProcessDefinitionService {

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
     * @param deploymentId
     * @param processDefinitionId
     * @return
     */
    public WProcessDefinition getProcessDefinition(
            String deploymentId,
            String processDefinitionId);

    /**
     * Obté les definicions de procés dels subprocesos donat el codi de desplegament i de la definició de procés pare
     *
     * @param deploymentId
     * @param processDefinitionId
     * @return
     */
    public List<WProcessDefinition> getSubProcessDefinitions(
            String deploymentId,
            String processDefinitionId);

    /**
     * Obté els noms de les tasques d'una definició de procés donat el desplegament i el codi de definició de procés
     *
     * @param dpd
     * @param processDefinitionId
     * @return
     */
    public List<String> getTaskNamesFromDeployedProcessDefinition(
            String deploymentId,
            String processDefinitionId);

    /**
     * Obté el nom de la tasca inicial d'una definició de procés
     *
     * @param processDefinitionId
     * @return
     */
    public String getStartTaskName(String processDefinitionId);

    /**
     * Obté la definició de procés d'una instància de procés
     * @param processInstanceId
     * @return
     */
    public WProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId);


}
