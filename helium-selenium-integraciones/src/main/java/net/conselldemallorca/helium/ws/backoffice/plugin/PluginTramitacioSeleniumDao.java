package net.conselldemallorca.helium.ws.backoffice.plugin;

import net.conselldemallorca.helium.core.model.dao.PluginTramitacioDao;
import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirVistaDocumentRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ResultatProcesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * Dao per accedir a la funcionalitat del plugin de tramitació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PluginTramitacioSeleniumDao {

	private net.conselldemallorca.helium.ws.backoffice.plugin.TramitacioSeleniumPlugin tramitacioPlugin;

	public void publicarExpedient(
			PublicarExpedientRequest request) {
		try {
			getTramitacioSeleniumPlugin().publicarExpedient(request);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al publicar l'expedient", ex);
			throw new PluginException("Error al publicar l'expedient", ex);
		}
	}

	public void publicarEvent(
			PublicarEventRequest request) {
		try {
			getTramitacioSeleniumPlugin().publicarEvent(request);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al publicar l'event", ex);
			throw new PluginException("Error al publicar l'event", ex);
		}
	}

	public DadesTramit obtenirDadesTramit(
			ObtenirDadesTramitRequest request) {
		try {
			return getTramitacioSeleniumPlugin().obtenirDadesTramit(request);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al obtenir les dades del tràmit " + request, ex);
			throw new PluginException("Error al obtenir les dades del tràmit " + request, ex);
		}
	}

	public DadesVistaDocument obtenirVistaDocument(
			ObtenirVistaDocumentRequest request) {
		try {
			return getTramitacioSeleniumPlugin().obtenirVistaDocument(request);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al obtenir la vista del document " + request, ex);
			throw new PluginException("Error al obtenir la vista del document " + request, ex);
		}
	}

	public void comunicarResultatProcesTramit(
			ResultatProcesTramitRequest request) {
		try {
			getTramitacioSeleniumPlugin().comunicarResultatProcesTramit(request);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al comunicar el resultat del proces", ex);
			throw new PluginException("Error al comunicar el resultat del proces", ex);
		}
	}



	@SuppressWarnings("rawtypes")
	private TramitacioSeleniumPlugin getTramitacioSeleniumPlugin() {
		if (tramitacioPlugin == null) {
			try {
				Class clazz = Class.forName("net.conselldemallorca.helium.ws.backoffice.plugin.TramitacioPluginSistrav3");
				tramitacioPlugin = (TramitacioSeleniumPlugin)clazz.newInstance();
			} catch (Exception ex) {
				logger.error("No s'ha pogut crear la instància del plugin de tramitació", ex);
				throw new PluginException("No s'ha pogut crear la instància del plugin de tramitació", ex);
			}
		}
		return tramitacioPlugin;
	}

	private static final Log logger = LogFactory.getLog(PluginTramitacioDao.class);

}
