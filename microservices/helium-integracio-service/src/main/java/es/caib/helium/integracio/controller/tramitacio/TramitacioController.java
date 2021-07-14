package es.caib.helium.integracio.controller.tramitacio;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.servo.util.Strings;

import es.caib.helium.integracio.domini.registre.RegistreNotificacio;
import es.caib.helium.integracio.domini.registre.RespostaAnotacioRegistre;
import es.caib.helium.integracio.domini.tramitacio.DadesTramit;
import es.caib.helium.integracio.domini.tramitacio.DadesVistaDocument;
import es.caib.helium.integracio.domini.tramitacio.ObtenirVistaDocumentRequest;
import es.caib.helium.integracio.domini.tramitacio.PublicarEventRequest;
import es.caib.helium.integracio.domini.tramitacio.PublicarExpedientRequest;
import es.caib.helium.integracio.domini.tramitacio.RespostaJustificantDetallRecepcio;
import es.caib.helium.integracio.domini.tramitacio.RespostaJustificantRecepcio;
import es.caib.helium.integracio.domini.tramitacio.ResultatProcesTramitRequest;
import es.caib.helium.integracio.service.tramitacio.TramitacioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(TramitacioController.API_PATH)
public class TramitacioController {
	
	public static final String API_PATH = "/api/v1/tramitacio";

	@Autowired
	private TramitacioService tramitacioService;
	
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Void> handleException(Exception e) {
		e.printStackTrace();
		return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = "expedient/{identificadorExpediente}/unitat/{unidadAdministrativa}", produces = "application/json")
	public ResponseEntity<Boolean> existeixExpedient(
			@PathVariable("unidadAdministrativa") Long unidadAdministrativa,
			@PathVariable("identificadorExpediente") String identificadorExpediente,
			@RequestParam("entornId") Long entornId) throws Exception {

		log.info("Existeix expedient");
		if (unidadAdministrativa == null || Strings.isNullOrEmpty(identificadorExpediente) ) {
			return new ResponseEntity<Boolean>(HttpStatus.BAD_REQUEST);
		}
		var existeix = tramitacioService.existeixExpedient(unidadAdministrativa, identificadorExpediente, entornId);
		return new ResponseEntity<Boolean>(existeix, HttpStatus.OK);
	}

	@GetMapping(value = "{numRegistre}/justificant", produces = "application/json")
	public ResponseEntity<RespostaJustificantRecepcio> getJustificant(
			@Valid @PathVariable("numRegistre") String numRegistre,
			@RequestParam("entornId") Long entornId) throws Exception {

		log.info("Obtenint el justificant de recepcio");
		if (Strings.isNullOrEmpty(numRegistre) ) {
			return new ResponseEntity<RespostaJustificantRecepcio>(HttpStatus.BAD_REQUEST);
		}
		var justificant = tramitacioService.obtenirJustificantRecepcio(numRegistre, entornId);
		if (justificant == null) {
			return new ResponseEntity<RespostaJustificantRecepcio>(justificant, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<RespostaJustificantRecepcio>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{numRegistre}/justificant/detall", produces = "application/json")
	public ResponseEntity<RespostaJustificantDetallRecepcio> getJustificantDetall(
			@Valid @PathVariable("numRegistre") String numRegistre,
			@RequestParam("entornId") Long entornId) throws Exception {
		
		log.info("Obtenint el detall del justificant de recepcio");
		if (Strings.isNullOrEmpty(numRegistre) ) {
			return new ResponseEntity<RespostaJustificantDetallRecepcio>(HttpStatus.BAD_REQUEST);
		}
		var justificant = tramitacioService.obtenirJustificantDetallRecepcio(numRegistre, entornId);
		if (justificant == null) {
			return new ResponseEntity<RespostaJustificantDetallRecepcio>(justificant, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<RespostaJustificantDetallRecepcio>(HttpStatus.NO_CONTENT);
	}
	
//	@GetMapping(value = "{numero}/dades/tramit", produces = "application/json")
	@GetMapping(value = "dades/tramit", produces = "application/json")
	public ResponseEntity<DadesTramit> getDadesTramit(
			@Valid @RequestParam("numero") String numero, 
			@RequestParam("clau") String clau,
			@RequestParam("entornId") Long entornId) throws Exception {
		
		log.info("Obtenint les dades del tramit");
		if (Strings.isNullOrEmpty(numero) || Strings.isNullOrEmpty(clau)) {
			return new ResponseEntity<DadesTramit>(HttpStatus.BAD_REQUEST);
		}

		var dadesTramit = tramitacioService.obtenirDadesTramit(numero, clau, entornId);
		if (dadesTramit == null) {
			return new ResponseEntity<DadesTramit>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<DadesTramit>(dadesTramit, HttpStatus.OK);
	}

	@PostMapping(value = "vista/document", consumes = "application/json", produces = "application/json")
	public ResponseEntity<DadesVistaDocument> obtenirVistaDocument(
			@Valid @RequestBody ObtenirVistaDocumentRequest request, 
			@RequestParam("entornId") Long entornId,
			BindingResult errors) throws Exception {
		
		log.info("Obtenint vista document");
		if (errors.hasErrors()) {
			return new ResponseEntity<DadesVistaDocument>(HttpStatus.BAD_REQUEST);
		}
		
		var vista = tramitacioService.obtenirVistaDocument(request, entornId);
		if (vista == null) {
			return new ResponseEntity<DadesVistaDocument>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<DadesVistaDocument>(vista, HttpStatus.OK);
	}
	
	@PostMapping(value = "comunicar/resultat/proces", consumes = "application/json")
	public ResponseEntity<Void> tramitacioComunicarResultatProces(
			@Valid @RequestBody ResultatProcesTramitRequest request, 
			@RequestParam("entornId") Long entornId,
			BindingResult errors) throws Exception {
		
		if (errors.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (tramitacioService.comunicarResultatProcesTramit(request, entornId)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}

	@PostMapping(value = "zonaper/expedient/crear", consumes = "application/json")
	public ResponseEntity<Void> crearExpedientZonaPersonal(
			@Valid @RequestBody PublicarExpedientRequest request, 
			@RequestParam("entornId") Long entornId,
			BindingResult errors) throws Exception {
		
		log.info("Creant expedient zona personal");
		if (errors.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (tramitacioService.crearExpedientZonaPersonal(request, entornId)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}
	
	@PostMapping(value = "zonaper/event/crear", consumes = "application/json")
	public ResponseEntity<Void> crearEventZonaPersonal(
			@Valid @RequestBody PublicarEventRequest request, 
			@RequestParam("entornId") Long entornId,
			BindingResult errors) throws Exception {
		
		log.info("Creant event zona personal");
		if (errors.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (tramitacioService.crearEventZonaPersonal(request, entornId)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}

	@PostMapping(value = "zonaper/notificacio/registrar", consumes = "application/json")
	public ResponseEntity<RespostaAnotacioRegistre> registrarNotificacio(
			@Valid @RequestBody RegistreNotificacio notificacio, 
			@RequestParam("entornId") Long entornId,
			BindingResult errors) throws Exception {
		
		log.info("Registrant notificacio zona personal");
		if (errors.hasErrors()) {
			return new ResponseEntity<RespostaAnotacioRegistre>(HttpStatus.BAD_REQUEST);
		}
		
		var registre = tramitacioService.registrarNotificacio(notificacio, entornId);
		if (registre == null) {
			return new ResponseEntity<RespostaAnotacioRegistre>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<RespostaAnotacioRegistre>(registre, HttpStatus.OK);
	}
}
