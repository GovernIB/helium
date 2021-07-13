package es.caib.helium.client.engine.execution;

public class ExecutionApiPath {
	
	public static final String API_PATH = "/api/v1";

	public static final String GET_TOKEN_BY_ID = API_PATH + "/executions/{tokenId}";

	public static final String GET_ACTIVE_TOKENS = API_PATH + "/processInstances/{processInstanceId}/executions/active";

	public static final String GET_ALL_TOKENS = API_PATH + "/processInstances/{processInstanceId}/executions";

	public static final String TOKEN_REDIRECT = API_PATH + "/executions/{tokenId}";

	public static final String TOKEN_ACTIVAR = API_PATH + "/executions/{tokenId}/activar/{activar}";

	public static final String SIGNAL_TOKEN = API_PATH + "/executions/{tokenId}/signal";
}
