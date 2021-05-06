package es.caib.helium.camunda.service;

import java.util.Map;

public class VariableInstanceServiceImpl implements VariableInstanceService {
    @Override
    public Map<String, Object> getProcessInstanceVariables(String processInstanceId) {
        return null;
    }

    @Override
    public Object getProcessInstanceVariable(String processInstanceId, String varName) {
        return null;
    }

    @Override
    public void setProcessInstanceVariable(String processInstanceId, String varName, Object value) {

    }

    @Override
    public void deleteProcessInstanceVariable(String processInstanceId, String varName) {

    }
}
