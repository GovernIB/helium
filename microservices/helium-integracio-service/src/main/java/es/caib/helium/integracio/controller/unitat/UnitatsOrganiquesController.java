package es.caib.helium.integracio.controller.unitat;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.helium.integracio.domini.unitat.UnitatOrganica;
import es.caib.helium.integracio.service.unitat.UnitatsOrganiquesService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(UnitatsOrganiquesController.API_PATH)
public class UnitatsOrganiquesController {

	public static final String API_PATH = "/api/v1/unitats";

	@Autowired
	private UnitatsOrganiquesService unitatsService;

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Void> handleException(Exception e) {
		e.printStackTrace();
		return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = "{codi}", produces = "application/json")
	public ResponseEntity<UnitatOrganica> consultaUnitat(@Valid @PathVariable("codi") String codi) throws Exception {

		var unitat = unitatsService.consultaUnitat(codi);
		if (unitat == null) {
			return new ResponseEntity<UnitatOrganica>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<UnitatOrganica>(unitat, HttpStatus.OK);
	}

	@GetMapping(value = "{codi}/arbre", produces = "application/json")
	public ResponseEntity<List<UnitatOrganica>> consultaArbre(@Valid @PathVariable("codi") String codi) throws Exception {

		var unitat = unitatsService.findAmbPare(codi);
		if (unitat == null) {
			return new ResponseEntity<List<UnitatOrganica>>(HttpStatus.NOT_FOUND);
		}
		
		if (unitat.isEmpty()) {
			return new ResponseEntity<List<UnitatOrganica>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<UnitatOrganica>>(unitat, HttpStatus.OK);
	}
}
