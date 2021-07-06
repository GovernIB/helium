package es.caib.helium.camunda.service;

import es.caib.helium.client.engine.model.VariableRest;

import java.util.List;

public interface TaskVariableService {

    /**
     * Obté les variables de la instància de procés.
     * @param taskId
     * @return Retorna un Map de codi i valor de les variables de la instància de procés.
     */
    public List<VariableRest> getTaskInstanceVariables(String taskId);

    /**
     * Obé el valor d'una variable d'una instàcia de procés.
     * @param taskId
     * @param varName
     * @return
     */
    public VariableRest getTaskInstanceVariable(String taskId, String varName);

    /**
     * Fixa el valor de la variable de la instància de procés.
     * @param taskId
     * @param variable
     */
    public void setTaskInstanceVariable(String taskId, VariableRest variable);

    /**
     * Fixa el valor de vàries variables a la vegada de la instància de la tasca.
     * Es pot especificar si esborrar abans les variables.
     * @param taskId
     * @param variables
     * @param deleteFirst
     */
    public void setTaskInstanceVariables(String taskId, List<VariableRest> variables, boolean deleteFirst);

    /** Esborra una variable de la instància de la tasca
     *
     * @param taskId
     * @param varName
     */
    public void deleteTaskInstanceVariable(String taskId, String varName);

}
