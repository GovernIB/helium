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
import org.springframework.web.bind.annotation.RestController;

import es.caib.helium.integracio.domini.firma.FirmaPost;
import es.caib.helium.integracio.service.firma.FirmaService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(FirmaController.API_PATH)
public class FirmaController {

	public static final String API_PATH = "/api/v1/firma";

	@Autowired
	private FirmaService firmaService;

	@ExceptionHandler({ Exception.class })
	public void handleException(Exception e) {
		e.printStackTrace();
	}

	@PostMapping(consumes = "application/json")
	public ResponseEntity<Void> firmar(@Valid @RequestBody FirmaPost firma, BindingResult error)
			throws Exception {

		if (error.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}

		firmaService.firmar(firma);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
