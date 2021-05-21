package es.caib.helium.integracio.controller.persones;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.servo.util.Strings;

import es.caib.helium.integracio.domini.persones.Persona;
import es.caib.helium.integracio.service.persones.PersonaService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(PersonesController.API_PATH)
public class PersonesController {
	
	public static final String API_PATH = "/api/v1/persones";

	@Autowired
	private final PersonaService personaService;
	
	  //...
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e) {
        e.printStackTrace();
    }
	
	@GetMapping(produces = "application/json")
	public ResponseEntity<List<Persona>> getPersones(@Valid @QueryParam("textSearch") String textSearch) throws Exception { 
		
		// 400 bad input parameter
		if (Strings.isNullOrEmpty(textSearch)) {
			return new ResponseEntity<List<Persona>>(HttpStatus.BAD_REQUEST);
		}
		List<Persona> persones = personaService.getPersones(textSearch);
		if (persones != null && !persones.isEmpty()) {
			//200 ok
			return new ResponseEntity<List<Persona>>(persones, HttpStatus.OK);
		}
		// 204 no content
		return new ResponseEntity<List<Persona>>(persones, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{codi}", produces = "application/json")
	public ResponseEntity<Persona> getPersonaByCodi(@PathVariable("codi") String codi) throws Exception {
		
		// 400 bad input parameter
		if (Strings.isNullOrEmpty(codi)) {
			return new ResponseEntity<Persona>(HttpStatus.BAD_REQUEST);
		}
		
		var persones = personaService.getPersonaByCodi(codi);
		if (persones != null) {
			//200 ok
			return new ResponseEntity<Persona>(persones, HttpStatus.OK);
		}
		
		// 204 no content
		return new ResponseEntity<Persona>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{codi}/rols")
	public ResponseEntity<String> getPersonaRolsByCodi(@PathVariable("codi") String codi) {
		
		var rols = "rols"; // TODO AFEGIR CODI
		if (Strings.isNullOrEmpty(rols)) {
			return new ResponseEntity<String>(rols, HttpStatus.OK);
		}
		return new ResponseEntity<String>(rols, HttpStatus.NO_CONTENT);
		
	}
}
