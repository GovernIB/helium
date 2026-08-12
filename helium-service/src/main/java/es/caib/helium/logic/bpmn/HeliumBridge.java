package es.caib.helium.logic.bpmn;

import es.caib.helium.logic.intf.service.BpmnHeliumService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Classe que fa de pont entre jBPM i Helium.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumBridge {

	private BpmnHeliumService bpmHeliumService;

	@Autowired
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
