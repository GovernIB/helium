package es.caib.helium.client.engine.processInstance;

public class ProcessInstanceApiPath {

    public static final String API_PATH = "/api/v1/processInstances";

    public static final String FIND_PROCESS_INSTANCE_WITH_PROCESS_DEFINITION_ID = API_PATH + "/byProcessDefinition/{processDefinitionId}";

    public static final String FIND_PROCESS_INSTANCE_WITH_PROCESS_DEFINITION_NAME_AND_ENTORN = API_PATH + "/byProcessDefinitionName/{processName}";

    public static final String GET_PROCESS_INSTANCE_TREE = API_PATH + "/{rootProcessInstanceId}/tree";

    public static final String GET_PROCESS_INSTANCE = API_PATH + "/{processInstanceId}";

    public static final String GET_ROOT_PROCESS_INSTANCE = API_PATH + "/{processInstanceId}/root";

    public static final String START_PROCESS_INSTANCE_BY_ID = API_PATH + "/start";

    public static final String SIGNAL_PROCESS_INSTANCE = API_PATH + "/{processInstanceId}/signal";

    public static final String DELETE_PROCESS_INSTANCE = API_PATH + "/{processInstanceId}";

    public static final String SUSPEND_PROCESS_INSTANCES = API_PATH + "/suspend";

    public static final String RESUME_PROCESS_INSTANCES = API_PATH + "/resume";

    public static final String CHANGE_PROCESS_INSTANCE_VERSION = API_PATH + "/{processInstanceId}/version/";

}
