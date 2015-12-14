package net.conselldemallorca.helium.wsintegraciones.backoffice;

import java.util.List;

import javax.jws.WebService;

import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirVistaDocumentRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ResultatProcesTipus;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ResultatProcesTramitRequest;
import net.conselldemallorca.helium.ws.backoffice.plugin.PluginService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.caib.bantel.ws.v2.model.referenciaentrada.ReferenciaEntrada;
import es.caib.bantel.ws.v2.model.referenciaentrada.ReferenciasEntrada;
import es.caib.bantel.ws.v2.services.BantelFacade;
import es.caib.bantel.ws.v2.services.BantelFacadeException;

/**
 * Backoffice per a gestionar les entrades de BANTEL
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(endpointInterface = "es.caib.bantel.ws.v2.services.BantelFacade")
public class BantelV2Backoffice extends BaseBackoffice implements BantelFacade {

	public void avisoEntradas(ReferenciasEntrada numeroEntradas) throws BantelFacadeException {

		List<ReferenciaEntrada> entrades = numeroEntradas.getReferenciaEntrada();

		for (ReferenciaEntrada referenciaEntrada : entrades) {

			ObtenirDadesTramitRequest request = new ObtenirDadesTramitRequest();
			request.setNumero(referenciaEntrada.getNumeroEntrada());
			request.setClau(referenciaEntrada.getClaveAcceso());
			boolean error = false;
			PluginService ps = new PluginService();

			try {
				DadesTramit dadesTramit = ps.obtenirDadesTramitSelenium(request);
				// DadesTramit dadesTramit = ServiceProxy.getInstance().getPluginService().obtenirDadesTramitSelenium(request);

				logger.info("Petició de processament tramit " + request + " amb identificador " + dadesTramit.getIdentificador() + " --> " + dadesTramit);
				int numExpedients = processarTramit(dadesTramit);
				logger.info("El tramit " + request + " ha creat " + numExpedients + " expedients");
			} catch (Exception ex) {
				logger.error("Error a l'hora de processar el tramit " + request, ex);
				error = true;
			}

			try {
				ResultatProcesTramitRequest requestResultat = new ResultatProcesTramitRequest();
				requestResultat.setNumeroEntrada(referenciaEntrada.getNumeroEntrada());
				requestResultat.setClauAcces(referenciaEntrada.getClaveAcceso());
				if (!error)
					requestResultat.setResultatProces(ResultatProcesTipus.PROCESSAT);
				else
					requestResultat.setResultatProces(ResultatProcesTipus.ERROR);
				logger.info("Comunicant el resultat de processar el tràmit " + request + ": " + requestResultat.getResultatProces());
				ps.comunicarResultatProcesTramitSelenium(requestResultat);
			} catch (Exception ex) {
				error = true;
				logger.error("Error a l'hora de comunicar el resultat de processar el tramit " + request, ex);
			}
		}
	}

	public void establecerResultadoProceso(ReferenciaEntrada arg0, String arg1, String arg2) {
		logger.debug(" El resultado del proceso de creación del tramite ha ido bien y así lo ha recibido el WS de la Bandeja Telematica.");
	}

	protected DadesVistaDocument getVistaDocumentTramit(long referenciaCodi, String referenciaClau, String plantillaTipus, String idioma) {
		ObtenirVistaDocumentRequest request = new ObtenirVistaDocumentRequest();
		request.setReferenciaCodi(referenciaCodi);
		request.setReferenciaClau(referenciaClau);
		request.setPlantillaTipus(plantillaTipus);
		request.setIdioma(idioma);
		try {
			PluginService ps = new PluginService();
			// return ServiceProxy.getInstance().getPluginService().obtenirVistaDocument(request);
			return ps.obtenirVistaDocument(request);
		} catch (Exception ex) {
			logger.error("Error al obtenir el document del tramit " + request, ex);
			return null;
		}
	}

	private static final Log logger = LogFactory.getLog(BantelV2Backoffice.class);

}
