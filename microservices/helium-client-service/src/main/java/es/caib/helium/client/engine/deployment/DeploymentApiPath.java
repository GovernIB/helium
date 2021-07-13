package es.caib.helium.client.engine.deployment;

public class DeploymentApiPath {
	
	public static final String API_PATH = "/api/v1/desplegaments";

	public static final String GET_DESPLEGAMENT = API_PATH + "/{deploymentId}";

	public static final String GET_DEPLOYMENTS = API_PATH + "";

	public static final String GET_RESOURCE_NAMES = API_PATH + "/{deploymentId}/resourceNames";

	public static final String GET_RESOURCE = API_PATH + "/{deploymentId}/resources/{resourceName}";

	public static final String CREATE_DEPLOYMENT = API_PATH + "";
	
	public static final String UPDATE_DEPLOYMENT_ACTIONS = API_PATH + "/{deploymentId}/actions";
	
	public static final String ESBORRAR_DESPLEGAMENT = API_PATH + "/{deploymentId}";
}
