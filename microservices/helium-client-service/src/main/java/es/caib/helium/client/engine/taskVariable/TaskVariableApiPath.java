package es.caib.helium.client.engine.taskVariable;

public class TaskVariableApiPath {
	
	public static final String API_PATH = "/api/v1/taskInstances";
	
	public static final String GET_TASK_INSTANCE_VARIABLES = API_PATH + "/{taskId}/taskInstanceVariables";

	public static final String GET_TASK_INSTANCE_VARIABLE = API_PATH + "/{taskId}/taskInstanceVariables/{varName:.+}";
	
	public static final String SET_TASK_INSTANCE_VARIABLE = API_PATH + "/{taskId}/taskInstanceVariables/{varName}";

	public static final String SET_TASK_INSTANCE_VARIABLES = API_PATH + "/{taskId}/taskInstanceVariables";

	public static final String DELETE_TASK_INSTANCE_VARIABLE = API_PATH + "/{taskId}/taskInstanceVariables/{varName:.+}";
}
