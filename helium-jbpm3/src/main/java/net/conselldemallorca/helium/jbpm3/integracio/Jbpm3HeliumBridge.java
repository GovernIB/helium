package net.conselldemallorca.helium.jbpm3.integracio;

import javax.annotation.Resource;

import net.conselldemallorca.helium.v3.core.api.service.Jbpm3HeliumService;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Classe que fa de pont entre jBPM i Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Jbpm3HeliumBridge {

	@Resource(name="heliumServiceV3")
	private Jbpm3HeliumService jbpm3HeliumService;

	@Autowired
	public void setJbpm3HeliumService(Jbpm3HeliumService jbpm3HeliumService) {
		INSTANCE.jbpm3HeliumService = jbpm3HeliumService;
	}

	private static Jbpm3HeliumBridge INSTANCE = new Jbpm3HeliumBridge();

	private Jbpm3HeliumBridge() {}

	public static Jbpm3HeliumBridge getInstance() {
		return INSTANCE;
	}

	public static Jbpm3HeliumService getInstanceService() {
		return INSTANCE.getJbpm3HeliumService();
	}

	public Jbpm3HeliumService getJbpm3HeliumService() {
		return jbpm3HeliumService;
	}
}
