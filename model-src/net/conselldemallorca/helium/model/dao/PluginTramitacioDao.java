/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPlugin;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginException;
import net.conselldemallorca.helium.model.exception.PluginException;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

/**
 * Dao per accedir a la funcionalitat del plugin de tramitació
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class PluginTramitacioDao {

	private TramitacioPlugin tramitacioPlugin;



	public void publicarExpedient(
			PublicarExpedientRequest request) {
		try {
			getTramitacioPlugin().publicarExpedient(request);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al publicar l'expedient", ex);
			throw new PluginException("Error al publicar l'expedient", ex);
		}
	}

	public void publicarEvent(
			PublicarEventRequest request) {
		try {
			getTramitacioPlugin().publicarEvent(request);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al publicar l'event", ex);
			throw new PluginException("Error al publicar l'event", ex);
		}
	}

	public DadesTramit obtenirDadesTramit(
			ObtenirDadesTramitRequest request) {
		try {
			return getTramitacioPlugin().obtenirDadesTramit(request);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al obtenir les dades del tràmit " + request, ex);
			throw new PluginException("Error al obtenir les dades del tràmit" + request, ex);
		}
	}



	@SuppressWarnings("unchecked")
	private TramitacioPlugin getTramitacioPlugin() {
		if (tramitacioPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class clazz = Class.forName(pluginClass);
					tramitacioPlugin = (TramitacioPlugin)clazz.newInstance();
				} catch (Exception ex) {
					logger.error("No s'ha pogut crear la instància del plugin de tramitació", ex);
					throw new PluginException("No s'ha pogut crear la instància del plugin de tramitació", ex);
				}
			}
		}
		return tramitacioPlugin;
	}

	private static final Log logger = LogFactory.getLog(PluginTramitacioDao.class);

}
