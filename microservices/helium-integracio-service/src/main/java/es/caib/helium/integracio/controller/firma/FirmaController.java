package es.caib.helium.integracio.controller.firma;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.caib.helium.integracio.domini.firma.FirmaPost;
import es.caib.helium.integracio.service.firma.FirmaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping(FirmaController.API_PATH)
@Slf4j
public class FirmaController {

	public static final String API_PATH = "/api/v1/firma";

	@Autowired
	private FirmaService firmaService;

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Void> handleException(Exception e) {
		e.printStackTrace();
		return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping(consumes = "application/json")
	public ResponseEntity<byte[]> firmar(@Valid @RequestBody FirmaPost firma, @RequestParam("entornId") Long entornId, BindingResult error)
			throws Exception {

		log.info("Firmant el document " + firma.toString());
		if (error.hasErrors()) {
			return new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		}

		var firmaContingut = firmaService.firmar(firma, entornId);
		if (firmaContingut == null || firmaContingut.length == 0) {
			return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<byte[]>(firmaContingut, HttpStatus.OK);
	}

}
