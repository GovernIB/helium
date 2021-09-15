package es.caib.helium.integracio.controller.arxiu;

import com.netflix.servo.util.Strings;
import es.caib.helium.integracio.domini.arxiu.ConsultaDocument;
import es.caib.helium.integracio.domini.arxiu.DocumentArxiu;
import es.caib.helium.integracio.domini.arxiu.ExpedientArxiu;
import es.caib.helium.integracio.service.arxiu.ArxiuService;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.Expedient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping(ArxiuController.API_PATH)
@Slf4j
public class ArxiuController {

	public static final String API_PATH = "/api/v1/arxiu";

	@Autowired
	private final ArxiuService arxiuService;
	
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Void> handleException(Exception e) {
		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error no controlat: " + e.getMessage(), e);
	}
	
	@GetMapping(value = "expedient/{uuId}", produces = "application/json")
	public ResponseEntity<Expedient> getExpedientByUuId(
			@PathVariable("uuId") String uuId,
			@RequestParam("entornId") Long entornId) throws Exception {
		
		log.info("Obtenint els expedients by uuId");
		if (Strings.isNullOrEmpty(uuId)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		var expedient = arxiuService.getExpedient(uuId, entornId);
		if (expedient != null) {
			return new ResponseEntity<>(expedient, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "expedients", consumes = "application/json")
	public ResponseEntity<Void> postExpedient(@Valid @RequestBody ExpedientArxiu expedient,
			@RequestParam("entornId") Long entornId,
			BindingResult errors) throws Exception {

		log.info("Creant expedient");
		if (errors.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}

		if (arxiuService.crearExpedient(expedient, entornId)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}

	@PutMapping(value = "expedients", consumes = "application/json")
	public ResponseEntity<Void> putExpedient(@Valid @RequestBody ExpedientArxiu expedient, 
			@RequestParam("entornId") Long entornId,
			BindingResult errors) throws Exception {
		
		log.info("Modificant expedient");
		if (errors.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		if (arxiuService.modificarExpedient(expedient, entornId)) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
	
	@DeleteMapping(value = "expedients/{uuId}")
	public ResponseEntity<Void> deleteExpedient(
			@PathVariable("uuId") String uuId,
			@RequestParam("entornId") Long entornId) throws Exception { 
		
		log.info("Esborrant expedient");
		if (Strings.isNullOrEmpty(uuId)) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		if (arxiuService.deleteExpedient(uuId, entornId)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}
	
	@PostMapping(value = "expedients/{arxiuUuId}/tancar")
	public ResponseEntity<Void> tancarExpedient(
			@PathVariable String arxiuUuId,
			@RequestParam("entornId") Long entornId) throws Exception {
		
		log.info("Tancant expedient");
		if (arxiuUuId == null) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (arxiuService.tancarExpedient(arxiuUuId, entornId)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}

	@PostMapping(value = "expedients/{arxiuUuId}/obrir")
	public ResponseEntity<Void> obrirExpedient(
			@PathVariable String arxiuUuId,
			@RequestParam("entornId") Long entornId) throws Exception {
		
		log.info("Obrint expedient");
		if (arxiuUuId == null) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (arxiuService.obrirExpedient(arxiuUuId, entornId)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}
	
	// Documents
	
	@GetMapping(value = "documents/{uuId}", produces = "application/json")
	public ResponseEntity<Document> getDocument(
			@PathVariable("uuId") String uuId,
			@RequestParam("consulta") ConsultaDocument consulta) throws Exception {

		log.info(("Obtenint document"));
		if (Strings.isNullOrEmpty(uuId)) {
			return new ResponseEntity<Document>(HttpStatus.BAD_REQUEST);
		}
		var document = arxiuService.getDocument(uuId, consulta);
		if (document != null) {
			return new ResponseEntity<Document>(document, HttpStatus.OK);
		}
		return new ResponseEntity<Document>(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping(value = "documents", consumes = "application/json")
	public ResponseEntity<ContingutArxiu> postDocument(
			@RequestBody DocumentArxiu document,
			@RequestParam("entornId") Long entornId,
			BindingResult errors) throws Exception {
		
		log.info("Creant document");
		if (errors.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		var docRetorn = arxiuService.crearDocument(document, entornId);
		if (docRetorn != null) {
			return new ResponseEntity<>(docRetorn, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@PutMapping(value = "documents", consumes = "application/json")
	public ResponseEntity<ContingutArxiu> putDocument(
			@RequestBody DocumentArxiu document,
			@RequestParam("entornId") Long entornId,
			BindingResult errors) throws Exception {
		
		log.info("Modificant document");
		if (errors.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		var docRetorn = arxiuService.modificarDocument(document, entornId);
		if (docRetorn != null) {
			return new ResponseEntity<>(docRetorn, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
	
	@DeleteMapping(value = "documents/{uuId}")
	public ResponseEntity<Void> deleteDocument(
			@PathVariable("uuId") String uuId,
			@RequestParam("entornId") Long entornId) throws Exception { 
		
		log.info("Esborrant document");
		if (Strings.isNullOrEmpty(uuId)) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		if (arxiuService.deleteDocument(uuId, entornId)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}
}
