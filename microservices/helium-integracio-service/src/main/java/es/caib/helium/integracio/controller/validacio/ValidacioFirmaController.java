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
import es.caib.helium.integracio.domini.arxiu.ArxiuFirmaDetall;
import es.caib.helium.integracio.domini.validacio.RespostaValidacioSignatura;
import es.caib.helium.integracio.domini.validacio.VerificacioFirma;
import es.caib.helium.integracio.service.validacio.ValidacioFirmaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping(ValidacioFirmaController.API_PATH)
@Slf4j
public class ValidacioFirmaController {

public static final String API_PATH = "/api/v1/firma";
	
	@Autowired
	private ValidacioFirmaService validacioFirmaService;
	
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Void> handleException(Exception e) {
		
		e.printStackTrace();
		return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@PostMapping(value = "verificacio", consumes = "application/json")
	public ResponseEntity<RespostaValidacioSignatura> verificacio(@Valid @RequestBody VerificacioFirma verificacio, BindingResult error) throws Exception {
		
		log.info("Verificant la firma " + verificacio.toString());
		
		if (error.hasErrors()) {
			return new ResponseEntity<RespostaValidacioSignatura>(HttpStatus.BAD_REQUEST);
		}
		
		var resposta = validacioFirmaService.verificarFirma(verificacio);
		if (resposta == null ) {
			return new ResponseEntity<RespostaValidacioSignatura>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<RespostaValidacioSignatura>(HttpStatus.OK);
	}
	
	@PostMapping(value = "validacio/firmar", consumes = "application/json")
	public ResponseEntity<List<ArxiuFirma>> validacioFirmes(
			@Valid @RequestBody VerificacioFirma validacio,
			BindingResult error) throws Exception {
		
		log.info("Validant i obtinguent les firmes " + validacio.toString());
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
	public ResponseEntity<List<ArxiuFirmaDetall>> validacioDetalls(@Valid @RequestBody VerificacioFirma validacio, BindingResult error) throws Exception {
		
		log.info("Obtenint detalls de la validació de la signatura");
		
		if (error.hasErrors()) {
			return new ResponseEntity<List<ArxiuFirmaDetall>>(HttpStatus.BAD_REQUEST);
		}
		
		var resposta = validacioFirmaService.validarSignaturaObtenirDetalls(validacio);
		if (resposta == null || resposta.isEmpty()) {
			return new ResponseEntity<List<ArxiuFirmaDetall>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<ArxiuFirmaDetall>>(resposta, HttpStatus.OK);
	}
}
