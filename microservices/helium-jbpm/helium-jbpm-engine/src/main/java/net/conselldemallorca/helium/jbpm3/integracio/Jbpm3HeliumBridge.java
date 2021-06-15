package net.conselldemallorca.helium.jbpm3.integracio;

import javax.annotation.Resource;

import net.conselldemallorca.helium.api.service.WorkflowBridgeService;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Classe que fa de pont entre jBPM i Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Jbpm3HeliumBridge {

	@Resource
	private WorkflowBridgeService workflowBridgeService;

	@Autowired
	public void setWorkflowBridgeService(WorkflowBridgeService workflowBridgeService) {
		INSTANCE.workflowBridgeService = workflowBridgeService;
	}

	private static Jbpm3HeliumBridge INSTANCE = new Jbpm3HeliumBridge();

	private Jbpm3HeliumBridge() {}

	public static Jbpm3HeliumBridge getInstance() {
		return INSTANCE;
	}

	public static WorkflowBridgeService getInstanceService() {
		return INSTANCE.getWorkflowBridgeService();
	}

	public WorkflowBridgeService getWorkflowBridgeService() {
		return workflowBridgeService;
	}
}
