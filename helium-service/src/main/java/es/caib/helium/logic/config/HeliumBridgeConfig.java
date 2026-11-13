package es.caib.helium.logic.config;

import es.caib.helium.bpmn.integracio.HeliumBridge;
import es.caib.helium.bpmn.service.BpmnHeliumService;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HeliumBridgeConfig {

	private final BpmnHeliumService bpmnHeliumService;

	@PostConstruct
	public void init() {
		HeliumBridge.getInstance().setBpmnHeliumService(bpmnHeliumService);
	}
}
