package net.conselldemallorca.helium.jbpm3.integracio;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.logic.intf.service.Jbpm3HeliumService;

/**
 * Classe que fa de pont entre jBPM i Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Jbpm3HeliumBridge {

	@Resource
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
