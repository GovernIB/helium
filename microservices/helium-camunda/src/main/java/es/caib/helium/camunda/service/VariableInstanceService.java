package es.caib.helium.camunda.service;

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
    public Object getProcessInstanceVariable(
            String processInstanceId,
            String varName);


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
            Object value);

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
