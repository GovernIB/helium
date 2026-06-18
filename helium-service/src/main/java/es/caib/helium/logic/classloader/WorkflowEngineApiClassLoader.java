package es.caib.helium.logic.classloader;

import es.caib.helium.logic.intf.service.WorkflowEngineApi;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementació de ClassLoader que carrega els recursos del WorkflowEngineApi.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class WorkflowEngineApiClassLoader extends RecursClassLoader {

	private final WorkflowEngineApi workflowEngineApi;
	private final String deploymentId;
	private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

	public WorkflowEngineApiClassLoader(WorkflowEngineApi workflowEngineApi, String deploymentId, ClassLoader parent) {
		super(parent);
		this.workflowEngineApi = workflowEngineApi;
		this.deploymentId = deploymentId;
	}

	public WorkflowEngineApiClassLoader(WorkflowEngineApi workflowEngineApi, String deploymentId) {
		super();
		this.workflowEngineApi = workflowEngineApi;
		this.deploymentId = deploymentId;
	}

	@Override
	protected byte[] loadResourceBytes(String name, Boolean isClass) throws IOException {
		try {
			// Si el recurs no es troba ja es llença una exception des del mètode getResourceBytes
			return workflowEngineApi.getResourceBytes(deploymentId, name);
		} catch (Exception ex) {
			throw new IOException("Resource " + name + " not found (" +
				"deploymentId=" + deploymentId + ")");
		}
	}

}
