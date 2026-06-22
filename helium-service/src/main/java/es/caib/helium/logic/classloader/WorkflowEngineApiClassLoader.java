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

	private static final ThreadLocal<String> deploymentId = new ThreadLocal<>();

	private final WorkflowEngineApi workflowEngineApi;
	private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

	public WorkflowEngineApiClassLoader(WorkflowEngineApi workflowEngineApi, ClassLoader parent) {
		super(parent);
		this.workflowEngineApi = workflowEngineApi;
	}

	public WorkflowEngineApiClassLoader(WorkflowEngineApi workflowEngineApi) {
		super();
		this.workflowEngineApi = workflowEngineApi;
	}

	public static void setDeploymentId(String value) {
		deploymentId.set(value);
	}

	public static String getCurrentDeploymentId() {
		return deploymentId.get();
	}

	public static void clearCurrentDeploymentId() {
		deploymentId.remove();
	}

	@Override
	protected byte[] loadResourceBytes(String name, boolean isClass) throws IOException {
		if (deploymentId.get() == null) {
			throw new IOException("Deployment " + (isClass ? "class" : "resource") + " " + name + " not found: empty deploymentId");
		}
		try {
			// Si el recurs no es troba ja es llença una exception des del mètode getResourceBytes
			return workflowEngineApi.getResourceBytes(deploymentId.get(), name);
		} catch (Exception ex) {
			throw new IOException("Deployment " + (isClass ? "class" : "resource") + " " + name + " not found (" +
				"deploymentId=" + deploymentId.get() + ")");
		}
	}

}
