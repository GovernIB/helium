package es.caib.helium.client.engine.task;

public class TaskApiPath {

	public static final String API_PATH = "/api/v1/taskInstances";
	
	public static final String GET_TASK_BY_ID = API_PATH + "/{taskId}";

	public static final String FIND_TASK_INSTANCES_BY_PROCESS_INSTANCE_ID = API_PATH + "/byProcessInstance/{processInstanceId}";

	public static final String GET_TASK_INSTANCE_ID_BY_EXECUTION_TOKEN_ID = API_PATH + "/byExecution/{executionTokenId}/id";

	public static final String TAKE_TASK_INSTANCE = API_PATH + "/{taskId}/take/{actorId}";
	
	public static final String RELEASE_TASK_INSTANCE = API_PATH + "/{taskId}/release";

	public static final String START_TASK_INSTANCE = API_PATH + "/{taskId}/start";

	public static final String END_TASK_INSTANCE = API_PATH + "/{taskId}/end";
	
	public static final String CANCEL_TASK_INSTANCE = API_PATH + "/{taskId}/cancel";

	public static final String SUSPEND_TASK_INSTANCE = API_PATH + "/{taskId}/suspend";

	public static final String RESUME_TASK_INSTANCE = API_PATH + "/{taskId}/resume";

	public static final String REASSING_TASK_INSTANCE = API_PATH + "/{taskId}/reassign";

	public static final String REASSING_TASK_INSTANCE_USER = API_PATH + "/{taskInstanceId}/reassign/user";

	public static final String REASSING_TASK_INSTANCE_GROUP = API_PATH + "/{taskInstanceId}/reassign/group";
	
	public static final String UPDATE_TASK_INSTANCE_INFO_CACHE = API_PATH + "/{taskId}";

	public static final String FIND_STARTTASK_OUT_TRANSITION = API_PATH + "/byProcessDefinition/{processDefinitionId}/tasks/{taskName}/leavingTransitions";

	public static final String FIND_TASK_OUT_TRANSITION = API_PATH + "/{taskInstanceId}/leavingTransitions";
}
