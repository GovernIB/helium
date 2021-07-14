package es.caib.helium.client.integracio.tramitacio;

import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public interface TramitacioClientService {

	Boolean existeixExpedient(Long unidadAdministrativa, String identificadorExpediente, Long entornId);

	RespostaJustificantRecepcio getJustificant(String numRegistre, Long entornId);
	
	RespostaJustificantDetallRecepcio getJustificantDetall(String numRegistre, Long entornId);
	
	DadesTramit getDadesTramit(String numero, String clau, Long entornId);
	
	DadesVistaDocument obtenirVistaDocument(ObtenirVistaDocumentRequest request, Long entornId);
	
	void tramitacioComunicarResultatProces(ResultatProcesTramitRequest request, Long entornId);
	
	void crearExpedientZonaPersonal(PublicarExpedientRequest request, Long entornId);
	
	void crearEventZonaPersonal(PublicarEventRequest request, Long entornId);

	RespostaAnotacioRegistre registrarNotificacio(RegistreNotificacio notificacio, Long entornId);
}
