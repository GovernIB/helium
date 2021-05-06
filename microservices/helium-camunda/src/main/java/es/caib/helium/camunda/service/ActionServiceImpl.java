package es.caib.helium.camunda.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActionServiceImpl implements ActionService {

    @Override
    public Map<String, Object> evaluateScript(String processInstanceId, String script, Set<String> outputNames) {
        return null;
    }

    @Override
    public Object evaluateExpression(String taskInstanceInstanceId, String processInstanceId, String expression, Map<String, Object> valors) {
        return null;
    }

    @Override
    public List<String> listActions(String jbpmId) {
        return null;
    }

    @Override
    public void executeActionInstanciaProces(String processInstanceId, String actionName) {

    }

    @Override
    public void executeActionInstanciaTasca(String taskInstanceId, String actionName) {

    }

    @Override
    public void retrocedirAccio(String processInstanceId, String actionName, List<String> params) {

    }
}
