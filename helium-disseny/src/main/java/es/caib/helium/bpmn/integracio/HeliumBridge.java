package es.caib.helium.bpmn.integracio;

import es.caib.helium.bpmn.service.BpmnHeliumService;

/**
 * Classe que fa de pont entre BPMN i Helium.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumBridge {

	private BpmnHeliumService bpmHeliumService;

	public void setBpmnHeliumService(BpmnHeliumService bpmHeliumService) {
		INSTANCE.bpmHeliumService = bpmHeliumService;
	}

	private static HeliumBridge INSTANCE = new HeliumBridge();

	private HeliumBridge() {}

	public static HeliumBridge getInstance() {
		return INSTANCE;
	}

	public static BpmnHeliumService getInstanceService() {
		return INSTANCE.getBpmnHeliumService();
	}

	public BpmnHeliumService getBpmnHeliumService() {
		return bpmHeliumService;
	}
}
