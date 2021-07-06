package es.caib.helium.client.integracio.tramitacio;

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

@Service
public interface TramitacioService {

	public RespostaJustificantRecepcio getJustificant(String numRegistre, Long entornId);
	
	public RespostaJustificantDetallRecepcio getJustificantDetall(String numRegistre, Long entornId);
	
	public DadesTramit getDadesTramit(String numero, String clau, Long entornId);
	
	public DadesVistaDocument obtenirVistaDocument(ObtenirVistaDocumentRequest request, Long entornId);
	
	public void tramitacioComunicarResultatProces(ResultatProcesTramitRequest request, Long entornId);
	
	public void crearExpedientZonaPersonal(PublicarExpedientRequest request, Long entornId);
	
	public void crearEventZonaPersonal(PublicarEventRequest request, Long entornId);

	public RespostaAnotacioRegistre registrarNotificacio(RegistreNotificacio notificacio, Long entornId);
}
