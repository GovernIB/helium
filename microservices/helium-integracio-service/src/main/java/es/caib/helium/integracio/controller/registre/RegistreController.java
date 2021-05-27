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
import es.caib.helium.integracio.domini.registre.RespostaConsultaRegistre;
import es.caib.helium.integracio.service.registre.RegistreService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(RegistreController.API_PATH)
public class RegistreController {
	
	public static final String API_PATH = "/api/v1/regweb";

	@Autowired
	private RegistreService registreService;

	@ExceptionHandler({ Exception.class })
	public void handleException(Exception e) {
		e.printStackTrace();
	}
	
	@PostMapping(value="sortida", consumes = "application/json")
	public ResponseEntity<Void> crearRegistreSortida(@Valid @RequestBody RegistreAssentament registre, BindingResult error)
			throws Exception {

		if (error.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		registreService.registrarSortida(registre, "Helium", "3.2"); //TODO moure el hardcoded a properties
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@GetMapping(value="{numeroRegistre}/oficina", produces = "application/json")
	public ResponseEntity<RespostaConsultaRegistre> getRegistreSortida(
			@Valid @PathVariable("numeroRegistre") String numeroRegistre,
			@RequestParam("usuariCodi") String usuariCodi,
			@RequestParam("entitatCodi") String entitatCodi
			)
			throws Exception {
		var resposta = registreService.obtenirRegistreSortida(numeroRegistre, usuariCodi, entitatCodi);
		if (resposta == null) {
			return new ResponseEntity<RespostaConsultaRegistre>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<RespostaConsultaRegistre>(resposta, HttpStatus.OK);
	}
}
