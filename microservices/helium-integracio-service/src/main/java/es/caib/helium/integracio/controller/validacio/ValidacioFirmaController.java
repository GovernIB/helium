package es.caib.helium.integracio.controller.validacio;

import java.util.List;

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

import es.caib.helium.integracio.domini.arxiu.ArxiuFirma;
import es.caib.helium.integracio.domini.validacio.VerificacioFirma;
import es.caib.helium.integracio.service.validacio.ValidacioFirmaService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(ValidacioFirmaController.API_PATH)
public class ValidacioFirmaController {

public static final String API_PATH = "/api/v1/firma";
	
	@Autowired
	private ValidacioFirmaService validacioFirmaService;
	
	@ExceptionHandler({Exception.class})
    public void handleException(Exception e) {
        e.printStackTrace();
    }
	
	@PostMapping(value = "verificacio", consumes = "application/json")
	public ResponseEntity<Void> verificacio(@Valid @RequestBody VerificacioFirma verificacio) throws Exception {
		
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@PostMapping(value = "validacio/firmar", consumes = "application/json")
	public ResponseEntity<List<ArxiuFirma>> validacioFirmes(@Valid @RequestBody VerificacioFirma validacio, BindingResult error) throws Exception {
		
		if (error.hasErrors()) {
			return new ResponseEntity<List<ArxiuFirma>>(HttpStatus.BAD_REQUEST);
		}
		
		var firmes = validacioFirmaService.validarFirma(validacio);
		if (firmes.isEmpty()) {
			return new ResponseEntity<List<ArxiuFirma>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<ArxiuFirma>>(firmes, HttpStatus.OK);
	}
	
	@PostMapping(value = "validacio/detalls", consumes = "application/json")
	public ResponseEntity<Void> validacioDetalls() throws Exception {
		
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}
