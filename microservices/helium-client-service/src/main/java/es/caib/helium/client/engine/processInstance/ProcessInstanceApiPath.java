package es.caib.helium.client.engine.processInstance;

public class ProcessInstanceApiPath {

    public static final String API_PATH = "/api/v1/processInstances";

    public static final String FIND_PROCESS_INSTANCE_WITH_PROCESS_DEFINITION_ID = "/byProcessDefinition/{processDefinitionId}";

    public static final String FIND_PROCESS_INSTANCE_WITH_PROCESS_DEFINITION_NAME_AND_ENTORN = "/byProcessDefinitionName/{processName}";

    public static final String GET_PROCESS_INSTANCE_TREE = "/{rootProcessInstanceId}/tree";

    public static final String GET_PROCESS_INSTANCE = "/{processInstanceId}";

    public static final String GET_ROOT_PROCESS_INSTANCE = "/{processInstanceId}/root";

    public static final String START_PROCESS_INSTANCE_BY_ID = "/start";

    public static final String SIGNAL_PROCESS_INSTANCE = "/{processInstanceId}/signal";

    public static final String DELETE_PROCESS_INSTANCE = "/{processInstanceId}";

    public static final String SUSPEND_PROCESS_INSTANCES = "/suspend";

    public static final String RESUME_PROCESS_INSTANCES = "/resume";

    public static final String CHANGE_PROCESS_INSTANCE_VERSION = "/{processInstanceId}/version/";

}
