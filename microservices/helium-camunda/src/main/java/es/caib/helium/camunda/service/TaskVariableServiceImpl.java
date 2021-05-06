package es.caib.helium.camunda.service;

import java.util.Map;

public class TaskVariableServiceImpl implements TaskVariableService {

    @Override
    public Map<String, Object> getTaskInstanceVariables(String taskId) {
        return null;
    }

    @Override
    public Object getTaskInstanceVariable(String taskId, String varName) {
        return null;
    }

    @Override
    public void setTaskInstanceVariable(String taskId, String varName, Object valor) {

    }

    @Override
    public void setTaskInstanceVariables(String taskId, Map<String, Object> variables, boolean deleteFirst) {

    }

    @Override
    public void deleteTaskInstanceVariable(String taskId, String varName) {

    }
}
