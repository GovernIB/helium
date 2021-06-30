package es.caib.helium.camunda.service;

import es.caib.helium.camunda.model.VariableRest;

import java.util.Map;

public interface VariableInstanceService {

    // Consulta de variables
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Obté les variables d'una instància de procés concreta
     *
     * @param processInstanceId
     * @return
     */
    public Map<String, Object> getProcessInstanceVariables(String processInstanceId);

    /**
     * Obté una variable d'una instància de procés concreta
     *
     * @param processInstanceId
     * @param varName
     * @return
     */
    public VariableRest getProcessInstanceVariable(
            String processInstanceId,
            String varName);


    // Actualització de variables
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Assigna el valor indicat a una variable de la instància de procés
     *
     * @param processInstanceId
     * @param variable
     */
    public void setProcessInstanceVariable(
            String processInstanceId,
            VariableRest variable);

    /**
     * Elimina una variable d'una instància de procés
     *
     * @param processInstanceId
     * @param varName
     */
    public void deleteProcessInstanceVariable(
            String processInstanceId,
            String varName);

}
