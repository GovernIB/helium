package es.caib.helium.integracio.controller.registre;

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

import es.caib.helium.integracio.domini.registre.RegistreAssentament;
import es.caib.helium.integracio.domini.registre.RespostaAnotacioRegistre;
import es.caib.helium.integracio.domini.registre.RespostaConsultaRegistre;
import es.caib.helium.integracio.service.registre.RegistreService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping(RegistreController.API_PATH)
@Slf4j
public class RegistreController {
	
	public static final String API_PATH = "/api/v1/regweb";

	@Autowired
	private RegistreService registreService;

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Void> handleException(Exception e) {
		e.printStackTrace();
		return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
//	@GetMapping(value = "{numeroRegistre}/justificant/data", produces = "application/json")
//	public ResponseEntity<Date> getDataJustificant(@Valid @PathVariable("numeroRegistre") String numeroRegistre) throws Exception {
//		
//		var data = registreService.obtenirDataJustificant(numeroRegistre);
//		if (data == null) {
//			return new ResponseEntity<Date>(HttpStatus.NOT_FOUND);
//		}
//		return new ResponseEntity<Date>(data, HttpStatus.OK);
//	}
	
	@PostMapping(value="sortida", consumes = "application/json")
	public ResponseEntity<RespostaAnotacioRegistre> crearRegistreSortida(
			@Valid @RequestBody RegistreAssentament registre,
			@RequestParam("entornId") Long entornId,
			BindingResult error) throws Exception {

		log.info("Creant registre de sortida");
		if (error.hasErrors()) {
			return new ResponseEntity<RespostaAnotacioRegistre>(HttpStatus.BAD_REQUEST);
		}
		
		var resposta = registreService.registrarSortida(registre, "Helium", "3.2", entornId); //TODO moure el hardcoded a properties
		return new ResponseEntity<RespostaAnotacioRegistre>(resposta, HttpStatus.OK);
	}

	@GetMapping(value = "{numeroRegistre}/oficina", produces = "application/json")
	public ResponseEntity<RespostaConsultaRegistre> getRegistreOficinaNom(
			@Valid @PathVariable("numeroRegistre") String numeroRegistre,
			@RequestParam("usuariCodi") String usuariCodi,
			@RequestParam("entitatCodi") String entitatCodi,
			@RequestParam("entornId") Long entornId) throws Exception {
		
		log.info("Obtenint registre de sortida");
		var resposta = registreService.obtenirRegistreSortida(numeroRegistre, usuariCodi, entitatCodi, entornId);
		if (resposta == null) {
			return new ResponseEntity<RespostaConsultaRegistre>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<RespostaConsultaRegistre>(resposta, HttpStatus.OK);
	}
	
}
