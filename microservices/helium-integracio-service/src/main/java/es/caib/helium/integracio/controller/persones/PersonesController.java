package es.caib.helium.integracio.controller.persones;

import com.netflix.servo.util.Strings;
import es.caib.helium.integracio.domini.persones.Persona;
import es.caib.helium.integracio.service.persones.PersonaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(PersonesController.API_PATH)
@Slf4j
public class PersonesController {
	
	public static final String API_PATH = "/api/v1/persones";

	@Autowired
	private final PersonaService personaService;

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Void> handleException(Exception e) {
		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error no controlat: " + e.getMessage(), e);
	}
	
	@GetMapping(produces = "application/json")
	public ResponseEntity<List<Persona>> getPersones(
			@Valid @RequestParam("textSearch") String textSearch,
			@RequestParam("entornId") Long entornId) throws Exception { 
		
		log.info("Consultant les persones amb filtre " + textSearch);
		if (Strings.isNullOrEmpty(textSearch)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		List<Persona> persones = personaService.getPersones(textSearch, entornId);
		if (persones != null && !persones.isEmpty()) {
			return new ResponseEntity<>(persones, HttpStatus.OK);
		}
		return new ResponseEntity<>(persones, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{codi}", produces = "application/json")
	public ResponseEntity<Persona> getPersonaByCodi(
			@PathVariable("codi") String codi, 
			@RequestParam("entornId") Long entornId) throws Exception {
		
		log.info("Consultant les persones amb codi " + codi);
		if (Strings.isNullOrEmpty(codi)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		var persones = personaService.getPersonaByCodi(codi, entornId);
		if (persones != null) {
			return new ResponseEntity<>(persones, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{codi}/rols")
	public ResponseEntity<List<String>> getPersonaRolsByCodi(
			@PathVariable("codi") String codi, 
			@RequestParam("entornId") Long entornId) throws Exception {
		
		log.info("Consultant els rols de les persones amb codi" + codi);
		var rols = personaService.getPersonaRolsByCodi(codi, entornId);
		if (rols != null && !rols.isEmpty()) {
			return new ResponseEntity<>(rols, HttpStatus.OK);
		}
		return new ResponseEntity<>(rols, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{rol}/persones")
	public ResponseEntity<List<String>> getPersonesCodiByRol(
			@PathVariable("rol") String rol,
			@RequestParam("entornId") Long entornId) throws Exception {

		log.info("Consultant els rols de les persones amb codi" + rol);
		var persones = personaService.getPersonesCodiByRol(rol, entornId);
		if (persones != null && !persones.isEmpty()) {
			return new ResponseEntity<>(persones, HttpStatus.OK);
		}
		return new ResponseEntity<>(persones, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "codis")
	public ResponseEntity<List<Persona>> getPersonesByCodi(
			@RequestParam("codis") List<String> codis,
			@RequestParam("entornId") Long entornId) throws Exception {

		log.info("Consultant les persones amb codi " + codis);
		var persones = personaService.getPersonesByCodi(codis, entornId);
		if (persones != null && !persones.isEmpty()) {
			return new ResponseEntity<>(persones, HttpStatus.OK);
		}
		return new ResponseEntity<>(persones, HttpStatus.NO_CONTENT);
	}
}
