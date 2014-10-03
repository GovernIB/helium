package net.conselldemallorca.helium.ws.backoffice.plugin;

import net.conselldemallorca.helium.core.model.dao.PluginTramitacioDao;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirVistaDocumentRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ResultatProcesTramitRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar les consultes als plugins
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PluginService {

	private PluginTramitacioSeleniumDao pluginTramitacioSeleniumDao;
	private PluginTramitacioDao pluginTramitacioDao;

	public PluginService() {
		this.pluginTramitacioSeleniumDao = new PluginTramitacioSeleniumDao();
		this.pluginTramitacioDao = new PluginTramitacioDao();
	}
	
	public void publicarExpedientSelenium(
			PublicarExpedientRequest request) {
		pluginTramitacioSeleniumDao.publicarExpedient(request);
	}
	public void publicarEventSelenium(
			PublicarEventRequest request) {
		pluginTramitacioSeleniumDao.publicarEvent(request);
	}
	public DadesTramit obtenirDadesTramitSelenium(
			ObtenirDadesTramitRequest request) {
		return pluginTramitacioSeleniumDao.obtenirDadesTramit(request);
	}
	public DadesVistaDocument obtenirVistaDocumentSelenium(
			ObtenirVistaDocumentRequest request) {
		return pluginTramitacioSeleniumDao.obtenirVistaDocument(request);
	}
	public void comunicarResultatProcesTramitSelenium(
			ResultatProcesTramitRequest request) {
		pluginTramitacioSeleniumDao.comunicarResultatProcesTramit(request);
	}
	public DadesVistaDocument obtenirVistaDocument(
			ObtenirVistaDocumentRequest request) {
		return pluginTramitacioDao.obtenirVistaDocument(request);
	}
	@Autowired
	public void setPluginTramitacioSeleniumDao(PluginTramitacioSeleniumDao pluginTramitacioSeleniumDao) {
		this.pluginTramitacioSeleniumDao = pluginTramitacioSeleniumDao;
	}
}
