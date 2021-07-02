/**
 * 
 */
package net.conselldemallorca.helium.ws.backoffice;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.caib.bantel.ws.v1.model.referenciaentrada.ReferenciaEntrada;
import es.caib.bantel.ws.v1.model.referenciaentrada.ReferenciasEntrada;
import es.caib.bantel.ws.v1.services.BantelFacade;
import es.caib.bantel.ws.v1.services.BantelFacadeException;
import es.caib.helium.logic.intf.dto.TramitDto;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirVistaDocumentRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ResultatProcesTipus;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ResultatProcesTramitRequest;

/**
 * Backoffice per a gestionar les entrades de BANTEL
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(endpointInterface = "es.caib.bantel.ws.v1.services.BantelFacade")
public class BantelV1Backoffice extends BaseBackoffice implements BantelFacade {

	
	public void avisoEntradas(ReferenciasEntrada numeroEntradas)
			throws BantelFacadeException {
		List<ReferenciaEntrada> entrades = numeroEntradas.getReferenciaEntrada();
		for (ReferenciaEntrada referenciaEntrada: entrades) {
			ObtenirDadesTramitRequest request = new ObtenirDadesTramitRequest();
			request.setNumero(referenciaEntrada.getNumeroEntrada());
			request.setClau(referenciaEntrada.getClaveAcceso());
			logger.info("Petició de processament tramit " + request);
			boolean error = false;
			TramitDto dadesTramit = null;
			try {
				// Se sincronitza per consultar primer si ja existeix l'expedient
				synchronized(this) {
					logger.info("Processant el tramit " + request);
					dadesTramit = pluginHelper.tramitacioObtenirDadesTramit(request.getNumero(), request.getClau());
					// Comprova sija existeix l'expedient a partir del tràmit
					boolean existeix = existeixExpedient(dadesTramit.getNumero(),
														String.valueOf(dadesTramit.getClauAcces()));
					if (!existeix) {
						int numExpedients = processarTramit(dadesTramit);
						logger.info("El tramit " + request + " ha creat " + numExpedients + " expedients");
					} else {
						logger.info("Ja existeix un altre expedient donat d'alta pel tramit " + request);
					}
				}
			} catch (Exception ex) {
				logger.error("Error petició de processament tramit " + request + " amb identificador " + dadesTramit.getIdentificador() + " --> " + dadesTramit, ex);
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
				pluginHelper.tramitacioComunicarResultatProces(requestResultat);
			} catch (Exception ex) {
				logger.error("Error a l'hora de comunicar el resultat de processar el tramit " + request, ex);
			}
		}
	}

	protected DadesVistaDocument getVistaDocumentTramit(
			long referenciaCodi,
			String referenciaClau,
			String plantillaTipus,
			String idioma) {
		ObtenirVistaDocumentRequest request = new ObtenirVistaDocumentRequest();
		request.setReferenciaCodi(referenciaCodi);
		request.setReferenciaClau(referenciaClau);
		request.setPlantillaTipus(plantillaTipus);
		request.setIdioma(idioma);
		try {
			return pluginHelper.tramitacioObtenirVistaDocument(request);
		} catch (Exception ex) {
			return null;
		}
	}

	private static final Log logger = LogFactory.getLog(BantelV1Backoffice.class);

}
