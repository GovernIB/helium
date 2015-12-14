/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantDetallRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirVistaDocumentRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ResultatProcesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPlugin;
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

	public RespostaAnotacioRegistre registrarNotificacio(RegistreNotificacio registreNotificacio) {
		try {
			return getTramitacioPlugin().registrarNotificacio(registreNotificacio);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al crear una notificació", ex);
			throw new PluginException("Error al crear una notificació", ex);
		}
	}

	public DadesTramit obtenirDadesTramit(
			ObtenirDadesTramitRequest request) {
		try {
			return getTramitacioPlugin().obtenirDadesTramit(request);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al obtenir les dades del tràmit " + request, ex);
			throw new PluginException("Error al obtenir les dades del tràmit " + request, ex);
		}
	}

	public DadesVistaDocument obtenirVistaDocument(
			ObtenirVistaDocumentRequest request) {
		try {
			return getTramitacioPlugin().obtenirVistaDocument(request);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al obtenir la vista del document " + request, ex);
			throw new PluginException("Error al obtenir la vista del document " + request, ex);
		}
	}
	
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(
			String numeroRegistre) throws TramitacioPluginException {
		try {
			return getTramitacioPlugin().obtenirJustificantRecepcio(numeroRegistre);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al obtenir justificant de recepció", ex);
			throw new PluginException("Error al obtenir justificant de recepció", ex);
		}
	}
	
	public RespostaJustificantDetallRecepcio obtenirJustificantDetallRecepcio(
			String numeroRegistre) throws TramitacioPluginException {
		try {
			return getTramitacioPlugin().obtenirJustificantDetallRecepcio(numeroRegistre);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al obtenir justificant de recepció", ex);
			throw new PluginException("Error al obtenir justificant de recepció", ex);
		}
	}	

	public void comunicarResultatProcesTramit(
			ResultatProcesTramitRequest request) {
		try {
			getTramitacioPlugin().comunicarResultatProcesTramit(request);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al comunicar el resultat del proces", ex);
			throw new PluginException("Error al comunicar el resultat del proces", ex);
		}
	}



	@SuppressWarnings("rawtypes")
	private TramitacioPlugin getTramitacioPlugin() {
		if (tramitacioPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.class");
			if (pluginClass == null) {
				String bantelUrl = GlobalProperties.getInstance().getProperty("app.bantel.entrades.url");
				if (bantelUrl.contains("v1")) {
					pluginClass = "net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginSistrav1";
				} else {
					pluginClass = "net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginSistrav2";
				}
			}
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
