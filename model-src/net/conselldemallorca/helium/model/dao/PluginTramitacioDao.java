/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentEvent;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentEventTipus;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.Event;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPlugin;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginException;
import net.conselldemallorca.helium.model.exception.PluginException;
import net.conselldemallorca.helium.model.hibernate.Expedient;
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
			PublicarExpedientRequest request) throws PluginException {
		try {
			getTramitacioPlugin().publicarExpedient(request);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al publicar l'expedient", ex);
			throw new PluginException("Error al publicar l'expedient", ex);
		}
	}

	public void publicarEvent(
			PublicarEventRequest request) throws PluginException {
		try {
			getTramitacioPlugin().publicarEvent(request);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al publicar l'event", ex);
			throw new PluginException("Error al publicar l'event", ex);
		}
	}
	public void publicarEvento(
			Expedient expedient,
			String titol,
			String text,
			String textSms,
			String enllasConsulta,
			Date data,
			String adjuntTitol,
			String adjuntArxiuNom,
			byte[] adjuntArxiuContingut,
			String adjuntModel,
			Integer adjuntVersio) throws PluginException {
		try {
			PublicarEventRequest requestEvent = new PublicarEventRequest();
			requestEvent.setExpedientIdentificador(expedient.getNumeroIdentificador());
			requestEvent.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
			Event event = new Event();
			event.setTitol(titol);
			event.setText(text);
			event.setTextSMS(textSms);
			event.setEnllasConsulta(enllasConsulta);
			List<DocumentEvent> documents = new ArrayList<DocumentEvent>();
			DocumentEvent document = new DocumentEvent();
			document.setNom(adjuntTitol);
			document.setArxiuNom(adjuntArxiuNom);
			document.setArxiuContingut(adjuntArxiuContingut);
			document.setTipus(DocumentEventTipus.ARXIU);
			documents.add(document);
			event.setDocuments(documents);
			requestEvent.setEvent(event);
			getTramitacioPlugin().publicarEvent(requestEvent);
		} catch (TramitacioPluginException ex) {
			logger.error("Error al publicar l'event", ex);
			throw new PluginException("Error al publicar l'event", ex);
		}
	}

	public DadesTramit obtenirDadesTramit(
			ObtenirDadesTramitRequest request) throws PluginException {
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
