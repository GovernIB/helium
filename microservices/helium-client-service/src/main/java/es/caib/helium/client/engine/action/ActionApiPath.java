package es.caib.helium.client.engine.action;

public class ActionApiPath {

	public static final String API_PATH = "/api/v1";

	public static final String EVALUATE_SCRIPT = API_PATH + "/processInstances/{processInstanceId}/evaluateScript";

	public static final String EVALUATE_EXPRESSION_BY_PROCES_INSTANCE_ID = API_PATH + "/processInstances/{processInstanceId}/evaluateExpression";

	public static final String EVALUATE_EXPRESSION = API_PATH + "/evaluateExpression";

	public static final String LIST_ACTIONS = API_PATH + "/processDefinitions/{processDefinition}/actions";

	public static final String EXECUTE_ACTION_INSTANCIA_PROCES = API_PATH + "/processInstances/{processInstanceId}/actions/{actionName}/execute";

	public static final String EXECUTE_ACTION_INSTANCIA_TASCA = API_PATH + "/taskInstances/{taskInstanceId}/actions/{actionName}/execute";
	
}
