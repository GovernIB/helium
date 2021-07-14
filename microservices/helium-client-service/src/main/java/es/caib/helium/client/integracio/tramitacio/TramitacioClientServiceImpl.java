package es.caib.helium.client.integracio.tramitacio;

import es.caib.helium.client.integracio.registre.model.RegistreNotificacio;
import es.caib.helium.client.integracio.registre.model.RespostaAnotacioRegistre;
import es.caib.helium.client.integracio.tramitacio.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class TramitacioClientServiceImpl implements TramitacioClientService {

	private final String missatgeLog = "Cridant Integracio Service - Tramitacio - ";
	
	private TramitacioFeignClient tramitacioClient;

	@Override
	public Boolean existeixExpedient(Long unidadAdministrativa, String identificadorExpediente, Long entornId) {

		log.debug(missatgeLog + " Existeix l'expedient amb identificador " + identificadorExpediente
				+  " i unitat administrativa " + unidadAdministrativa + " per l'entornId " + entornId);
		var responseEntity = tramitacioClient.existeixExpedient(unidadAdministrativa, identificadorExpediente, entornId);
		return Objects.requireNonNull(responseEntity.getBody());
	}

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
