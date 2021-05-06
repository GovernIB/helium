package es.caib.helium.camunda.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ActionService {

    public Map<String, Object> evaluateScript(String processInstanceId, String script, Set<String> outputNames);
    public Object evaluateExpression(String taskInstanceInstanceId, String processInstanceId, String expression, Map<String, Object> valors);

    public List<String> listActions(String jbpmId);
    public void executeActionInstanciaProces(String processInstanceId, String actionName);
    public void executeActionInstanciaTasca(String taskInstanceId, String actionName);
    public void retrocedirAccio(String processInstanceId, String actionName, List<String> params);

}
