package es.caib.helium.client.integracio.tramitacio;

import java.util.Objects;

import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.registre.model.RegistreNotificacio;
import es.caib.helium.client.integracio.registre.model.RespostaAnotacioRegistre;
import es.caib.helium.client.integracio.tramitacio.model.DadesTramit;
import es.caib.helium.client.integracio.tramitacio.model.DadesVistaDocument;
import es.caib.helium.client.integracio.tramitacio.model.ObtenirVistaDocumentRequest;
import es.caib.helium.client.integracio.tramitacio.model.PublicarEventRequest;
import es.caib.helium.client.integracio.tramitacio.model.PublicarExpedientRequest;
import es.caib.helium.client.integracio.tramitacio.model.RespostaJustificantDetallRecepcio;
import es.caib.helium.client.integracio.tramitacio.model.RespostaJustificantRecepcio;
import es.caib.helium.client.integracio.tramitacio.model.ResultatProcesTramitRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TramitacioServiceImpl implements TramitacioService {

	private final String missatgeLog = "Cridant Integracio Service - Tramitacio - ";
	
	private TramitacioFeignClient tramitacioClient;
	
	@Override
	public RespostaJustificantRecepcio getJustificant(String numRegistre, Long entornId) {

		log.debug(missatgeLog + " obtinguent el justificant amb numero de registre " + numRegistre + " per l'entornId " + entornId);
		var responseEntity = tramitacioClient.getJustificant(numRegistre, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public RespostaJustificantDetallRecepcio getJustificantDetall(String numRegistre, Long entornId) {
		
		log.debug(missatgeLog + " obtinguent el detall justificant amb numero de registre " + numRegistre + " per l'entornId " + entornId);
		var responseEntity = tramitacioClient.getJustificantDetall(numRegistre, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public DadesTramit getDadesTramit(String numero, String clau, Long entornId) {
		
		log.debug(missatgeLog + " obtinguent dades del tramit numero " + numero + " clau " + clau + " per l'entornId " + entornId);
		var responseEntity = tramitacioClient.getDadesTramit(numero, clau, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public DadesVistaDocument obtenirVistaDocument(ObtenirVistaDocumentRequest request, Long entornId) {
		
		log.debug(missatgeLog + " obtinguent vista document " + request.toString() + " per l'entornId " + entornId);
		var responseEntity = tramitacioClient.obtenirVistaDocument(request, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void tramitacioComunicarResultatProces(ResultatProcesTramitRequest request, Long entornId) {
		
		log.debug(missatgeLog + " comunicant resultat del proces " + request.toString() + " per l'entornId " + entornId);
		tramitacioClient.tramitacioComunicarResultatProces(request, entornId);
	}

	@Override
	public void crearExpedientZonaPersonal(PublicarExpedientRequest request, Long entornId) {
		
		log.debug(missatgeLog + " creant expedient zona personal " + request.toString() + " per l'entornId " + entornId);
		tramitacioClient.crearExpedientZonaPersonal(request, entornId);
	}

	@Override
	public void crearEventZonaPersonal(PublicarEventRequest request, Long entornId) {

		log.debug(missatgeLog + " creant event zona personal " + request.toString() + " per l'entornId " + entornId);
		tramitacioClient.crearEventZonaPersonal(request, entornId);
	}

	@Override
	public RespostaAnotacioRegistre registrarNotificacio(RegistreNotificacio notificacio, Long entornId) {
		
		log.debug(missatgeLog + " registrant notificacio per l'entornId " + entornId);
		var responseEntity = tramitacioClient.registrarNotificacio(notificacio, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}
}
