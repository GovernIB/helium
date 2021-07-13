package es.caib.helium.client.engine.processDefiniton;

public class ProcessDefinitionApiPath {
	
	public static final String API_PATH = "/api/v1/processDefinitions";

	public static final String GET_PROCESS_DEFINITION = API_PATH + "";

	public static final String GET_SUB_PROCESS_DEFINITIONS = API_PATH + "/{processDefinitionId}/subProcessDefinition";
	
	public static final String GET_TASK_NAMES_FROM_DEPLOYED_PROCESS_DEFINITON = API_PATH + "/{processDefinitionId}/taskNames";
	
	public static final String GET_START_TASK_NAME = API_PATH + "/{processDefinitionId}/startTaskName";
	
	public static final String FIND_PROCESS_DEFINITION_WITH_PROCESS_INSTANCE_ID = API_PATH + "/byProcessInstance/{processInstanceId}";
	
	public static final String UPDATE_SUB_PROCESS_DEFINITON = API_PATH + "";
	
	
}
