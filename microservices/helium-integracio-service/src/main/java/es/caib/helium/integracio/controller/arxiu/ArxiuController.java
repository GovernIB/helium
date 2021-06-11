package es.caib.helium.integracio.controller.arxiu;

import javax.validation.Valid;
import javax.ws.rs.QueryParam;

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
import org.springframework.web.bind.annotation.RestController;

import com.netflix.servo.util.Strings;

import es.caib.plugins.arxiu.api.Document;
import es.caib.helium.integracio.domini.arxiu.DocumentArxiu;
import es.caib.helium.integracio.domini.arxiu.ExpedientArxiu;
import es.caib.helium.integracio.service.arxiu.ArxiuService;
import es.caib.plugins.arxiu.api.Expedient;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(ArxiuController.API_PATH)
public class ArxiuController {

	public static final String API_PATH = "/api/v1/arxiu";

	@Autowired
	private final ArxiuService arxiuService;
	
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Void> handleException(Exception e) {
		e.printStackTrace();
		return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping(value = "expedients/{uuId}", produces = "application/json")
	public ResponseEntity<Expedient> getExpedientsByUuId(@PathVariable("uuId") String uuId) throws Exception { 
		
		if (Strings.isNullOrEmpty(uuId)) {
			return new ResponseEntity<Expedient>(HttpStatus.BAD_REQUEST);
		}
		var expedient = arxiuService.getExpedient(uuId);
		if (expedient != null) {
			return new ResponseEntity<Expedient>(expedient, HttpStatus.OK);
		}
		return new ResponseEntity<Expedient>(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "expedients", consumes = "application/json")
	public ResponseEntity<Void> crearExpedient(@Valid @RequestBody ExpedientArxiu expedient, BindingResult errors) throws Exception {
		
		if (errors.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (arxiuService.crearExpedient(expedient)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}

	@PutMapping(value = "expedients", consumes = "application/json")
	public ResponseEntity<Void> modificarExpedient(@Valid @RequestBody ExpedientArxiu expedient, BindingResult errors) throws Exception {
		
		if (errors.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (arxiuService.modificarExpedient(expedient)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}
	
	@DeleteMapping(value = "expedients/{uuId}")
	public ResponseEntity<Void> deleteExpedient(@PathVariable("uuId") String uuId) throws Exception { 
		
		// 400 bad input parameter
		if (Strings.isNullOrEmpty(uuId)) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		if (arxiuService.deleteExpedient(uuId)) {
			//200 ok
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		// 204 no content
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}
	
	@PostMapping(value = "expedients/{arxiuUuId}/tancar")
	public ResponseEntity<Void> tancarExpedient(@PathVariable String arxiuUuId) throws Exception {
		
		if (arxiuUuId == null) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (arxiuService.tancarExpedient(arxiuUuId)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}

	@PostMapping(value = "expedients/{arxiuUuId}/obrir")
	public ResponseEntity<Void> obrirExpedient(@PathVariable String arxiuUuId) throws Exception {
		
		if (arxiuUuId == null) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (arxiuService.obrirExpedient(arxiuUuId)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}
	
	// Documents
	
	@GetMapping(value = "documents/{uuId}", produces = "application/json")
	public ResponseEntity<Document> getDocument(@PathVariable("uuId") String uuId,
			@QueryParam("versio") String versio,
			@QueryParam("ambContingut") boolean ambContingut,
			@QueryParam("isSignat") boolean isSignat) throws Exception {
		
		if (Strings.isNullOrEmpty(uuId)) {
			return new ResponseEntity<Document>(HttpStatus.BAD_REQUEST);
		}
		var document = arxiuService.getDocument(uuId, versio, ambContingut, isSignat);
		if (document != null) {
			return new ResponseEntity<Document>(document, HttpStatus.OK);
		}
		return new ResponseEntity<Document>(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping(value = "documents", consumes = "application/json")
	public ResponseEntity<Void> crearDocument(@Valid @RequestBody DocumentArxiu document, BindingResult errors) throws Exception {
		
		if (errors.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (arxiuService.crearDocument(document)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}

	@PutMapping(value = "documents", consumes = "application/json")
	public ResponseEntity<Void> modificarExpedient(@Valid @RequestBody DocumentArxiu document, BindingResult errors) throws Exception {
		
		if (errors.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (arxiuService.modificarDocument(document)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}
	
	@DeleteMapping(value = "documents/{uuId}")
	public ResponseEntity<Void> deleteDocument(@PathVariable("uuId") String uuId) throws Exception { 
		
		// 400 bad input parameter
		if (Strings.isNullOrEmpty(uuId)) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		if (arxiuService.deleteDocument(uuId)) {
			//200 ok
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}
}
