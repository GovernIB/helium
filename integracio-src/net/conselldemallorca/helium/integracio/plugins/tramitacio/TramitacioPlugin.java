/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.tramitacio;

/**
 * Interfície per accedir al sistema de tramitacio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TramitacioPlugin {

	public void publicarExpedient(PublicarExpedientRequest request) throws TramitacioPluginException;

	public void publicarEvent(PublicarEventRequest request) throws TramitacioPluginException;

	public DadesTramit obtenirDadesTramit(ObtenirDadesTramitRequest request) throws TramitacioPluginException;

	public void comunicarResultatProcesTramit(ResultatProcesTramitRequest request) throws TramitacioPluginException;

	public DadesVistaDocument obtenirVistaDocument(ObtenirVistaDocumentRequest request) throws TramitacioPluginException;

}
