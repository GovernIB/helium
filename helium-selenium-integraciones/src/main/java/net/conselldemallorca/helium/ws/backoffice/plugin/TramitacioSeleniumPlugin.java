package net.conselldemallorca.helium.ws.backoffice.plugin;

import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirVistaDocumentRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ResultatProcesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginException;


/**
 * Interfície per accedir al sistema de tramitacio especificament creat per proves a selenium
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TramitacioSeleniumPlugin {

	public void publicarExpedient(PublicarExpedientRequest request) throws TramitacioPluginException;

	public void publicarEvent(PublicarEventRequest request) throws TramitacioPluginException;

	public DadesTramit obtenirDadesTramit(ObtenirDadesTramitRequest request) throws TramitacioPluginException;

	public void comunicarResultatProcesTramit(ResultatProcesTramitRequest request) throws TramitacioPluginException;

	public DadesVistaDocument obtenirVistaDocument(ObtenirVistaDocumentRequest request) throws TramitacioPluginException;

}