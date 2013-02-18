/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import net.conselldemallorca.helium.v3.core.api.service.ConfigService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.OrganitzacioService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;

import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * FactoryBean per a Jbpm3HeliumBridge.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Jbpm3HeliumBridgeFactoryBean implements SmartFactoryBean<Jbpm3HeliumBridge> {

	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private PluginService pluginService;
	@Autowired
	private DissenyService dissenyService;
	@Autowired
	private TerminiService terminiService;
	@Autowired
	private OrganitzacioService organitzacioService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private TascaService tascaService;

	public Jbpm3HeliumBridge getObject() {
		Jbpm3HeliumBridge instance = Jbpm3HeliumBridge.getInstance();
		if (!instance.isConfigured()) {
			instance.configServices(
					expedientService,
					documentService,
					pluginService,
					dissenyService,
					terminiService,
					organitzacioService,
					configService,
					tascaService);
		}
        return instance;
    }

    public Class<Jbpm3HeliumBridge> getObjectType() {
        return Jbpm3HeliumBridge.class;
    }

    public boolean isSingleton() {
        return true;
    }

	public boolean isPrototype() {
		return false;
	}

	public boolean isEagerInit() {
		return true;
	}

}
