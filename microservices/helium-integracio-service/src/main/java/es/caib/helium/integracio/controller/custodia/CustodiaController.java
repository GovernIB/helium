package es.caib.helium.integracio.controller.custodia;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.servo.util.Strings;

import es.caib.helium.integracio.domini.custodia.CustodiaRequest;
import es.caib.helium.integracio.domini.validacio.RespostaValidacioSignatura;
import es.caib.helium.integracio.service.custodia.CustodiaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(CustodiaController.API_PATH)
public class CustodiaController {

	public static final String API_PATH = "/api/v1/custodia";
	
	@Autowired
	private CustodiaService custodiaService;

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Void> handleException(Exception e) {
		
		e.printStackTrace();
		return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@PostMapping(consumes = "application/json")
	public ResponseEntity<String> afegirSignatura(
			@Valid @RequestBody CustodiaRequest request,
			@RequestParam("entornId") Long entornId,
			BindingResult errors) throws Exception {
		
		log.info("Afegint signatura a custodia");
		var custodiaId = custodiaService.addSignature(request, entornId);
		if (!Strings.isNullOrEmpty(custodiaId)) {
			return new ResponseEntity<String>(HttpStatus.OK);
		}
		return new ResponseEntity<String>(HttpStatus.CONFLICT);
	}
	
	@GetMapping(value = "{documentId}", produces = "application/json")
	public ResponseEntity<List<byte[]>> getSignatures(
			@PathVariable("documentId") String documentId, 
			@RequestParam("entornId") Long entornId) throws Exception {
		
		log.info("Obtenint signatures de custodia");
		if (Strings.isNullOrEmpty(documentId)) {
			return new ResponseEntity<List<byte[]>>(HttpStatus.BAD_REQUEST);
		}
		
		var signatures = custodiaService.getSignatures(documentId, entornId);
		if (signatures == null || signatures.isEmpty()) {
			return new ResponseEntity<List<byte[]>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<byte[]>>(signatures, HttpStatus.OK);
	}

	@DeleteMapping(value = "{documentId}")
	public ResponseEntity<Void> deleteSignatures(
			@PathVariable("documentId") String documentId,
			@RequestParam("entornId") Long entornId) throws Exception {
		
		log.info("Esborrant signatura de custodia");
		if (Strings.isNullOrEmpty(documentId)) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (custodiaService.deleteSignatures(documentId, entornId)) { 
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(value = "{documentId}/arxiu", produces = "application/json")
	public ResponseEntity<byte[]> getSignaturesAmbArxiu(
			@PathVariable("documentId") String documentId,
			@RequestParam("entornId") Long entornId) throws Exception {
		
		log.info("Obtenint signatures amb arxiu");
		if (Strings.isNullOrEmpty(documentId)) {
			return new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		}
		
		var signatures = custodiaService.getSignaturesAmbArxiu(documentId, entornId);
		if (signatures == null || signatures.length == 0) {
			return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<byte[]>(signatures, HttpStatus.OK);
	}
	
	@GetMapping(value = "{documentId}/validacio", produces = "application/json")
	public ResponseEntity<List<RespostaValidacioSignatura>> getDadesValidacioSignatura(
			@PathVariable("documentId") String documentId,
			@RequestParam("entornId") Long entornId) throws Exception {
		
		log.info("Obtenint dades de validació de signatura");
		if (Strings.isNullOrEmpty(documentId)) {
			return new ResponseEntity<List<RespostaValidacioSignatura>>(HttpStatus.BAD_REQUEST);
		}
		
		var signatures = custodiaService.dadesValidacioSignatura(documentId, entornId);
		if (signatures == null || signatures.isEmpty()) {
			return new ResponseEntity<List<RespostaValidacioSignatura>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<RespostaValidacioSignatura>>(signatures, HttpStatus.OK);
	}
	
	@GetMapping(value = "{documentId}/url/comprovacio", produces = "application/json")
	public ResponseEntity<String> getUrlComprovacioSignatures(
			@PathVariable("documentId") String documentId,
			@RequestParam("entornId") Long entornId) throws Exception {
		
		log.info("Obtenint URL de comprovació de signatures");
		if (Strings.isNullOrEmpty(documentId)) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		
		var url = custodiaService.getUrlComprovacioSignatura(documentId, entornId);
		if (Strings.isNullOrEmpty(url)) {
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<String>(url, HttpStatus.OK);
	}
	
}
