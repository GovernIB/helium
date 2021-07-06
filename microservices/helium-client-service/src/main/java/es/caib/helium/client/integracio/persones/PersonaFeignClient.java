package es.caib.helium.client.integracio.persones;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.integracio.firma.FirmaPath;
import es.caib.helium.client.integracio.persones.model.Persona;

public interface PersonaFeignClient {
	
	@RequestMapping(method = RequestMethod.POST, value = FirmaPath.FIRMAR)
	public ResponseEntity<List<Persona>> getPersones(
			@Valid @RequestParam("textSearch") String textSearch,
			@RequestParam("entornId") Long entornId);
	
	public ResponseEntity<Persona> getPersonaByCodi(
			@PathVariable("codi") String codi, 
			@RequestParam("entornId") Long entornId);
	
	public ResponseEntity<List<String>> getPersonaRolsByCodi(
			@PathVariable("codi") String codi, 
			@RequestParam("entornId") Long entornId);
}
