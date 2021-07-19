package es.caib.helium.camunda.service;

import es.caib.helium.client.engine.model.VariableRest;

import java.util.List;
import java.util.Set;

public interface ActionService {

    public static final String JUEL_SCRIPTING_LANGUAGE = "juel";
    public static final String GROOVY_SCRIPTING_LANGUAGE = "groovy";
    public static final String JAVASCRIPT_SCRIPTING_LANGUAGE = "javascript";
    public static final String ECMASCRIPT_SCRIPTING_LANGUAGE = "ecmascript";

    public List<VariableRest> evaluateScript(
            String processInstanceId,
            String scriptformat,
            String script,
            Set<String> outputNames);

    public Object evaluateExpression(
            String taskInstanceInstanceId,
            String processInstanceId,
            String expressionLanguage,
            String expression,
            List<VariableRest> valors);
    public Object evaluateExpression(
            String expressionLanguage,
            String expression,
//            String expectedClass,
            List<VariableRest> context);

    public List<String> listActions(String jbpmId);
    public void executeActionInstanciaProces(String processInstanceId, String actionName);
    public void executeActionInstanciaTasca(String taskInstanceId, String actionName);
    public void retrocedirAccio(String processInstanceId, String actionName, List<String> params);

}
