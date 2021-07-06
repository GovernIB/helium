package es.caib.helium.client.integracio.tramitacio;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

public interface TramitacioFeignClient {

	@RequestMapping(method = RequestMethod.GET, value = TramitacioPath.GET_JUSTIFICANT)
	public ResponseEntity<RespostaJustificantRecepcio> getJustificant(
			@Valid @PathVariable("numRegistre") String numRegistre,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.GET, value = TramitacioPath.GET_JUSTIFICANT_DETALL)
	public ResponseEntity<RespostaJustificantDetallRecepcio> getJustificantDetall(
			@Valid @PathVariable("numRegistre") String numRegistre,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.GET, value = TramitacioPath.GET_DADES_TRAMIT)
	public ResponseEntity<DadesTramit> getDadesTramit(
			@Valid @RequestParam("numero") String numero, 
			@RequestParam("clau") String clau,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.POST, value = TramitacioPath.OBTENIR_VISTA_DOCUMENT)
	public ResponseEntity<DadesVistaDocument> obtenirVistaDocument(
			@Valid @RequestBody ObtenirVistaDocumentRequest request, 
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.POST, value = TramitacioPath.TRAMITACIO_COMUNICAR_RESULTAT_PROCES)
	public ResponseEntity<Void> tramitacioComunicarResultatProces(
			@Valid @RequestBody ResultatProcesTramitRequest request, 
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.POST, value = TramitacioPath.CREAR_EXPEDIENT_ZONA_PERSONAL)
	public ResponseEntity<Void> crearExpedientZonaPersonal(
			@Valid @RequestBody PublicarExpedientRequest request, 
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.POST, value = TramitacioPath.CREAR_EVENT_ZONA_PERSONAL)
	public ResponseEntity<Void> crearEventZonaPersonal(
			@Valid @RequestBody PublicarEventRequest request, 
			@RequestParam("entornId") Long entornId);

	@RequestMapping(method = RequestMethod.POST, value = TramitacioPath.REGISTRAR_NOTIFICACIO)
	public ResponseEntity<RespostaAnotacioRegistre> registrarNotificacio(
			@Valid @RequestBody RegistreNotificacio notificacio, 
			@RequestParam("entornId") Long entornId);
}
