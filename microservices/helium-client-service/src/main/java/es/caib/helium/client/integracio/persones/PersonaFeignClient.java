package es.caib.helium.client.integracio.persones;

import es.caib.helium.client.integracio.persones.model.Persona;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

public interface PersonaFeignClient {

	String SERVEI = "-persona";

	@RequestMapping(method = RequestMethod.POST, value = PersonesPath.GET_PERSONES)
	ResponseEntity<List<Persona>> getPersones(
			@Valid @RequestParam("textSearch") String textSearch,
			@RequestParam("entornId") Long entornId);

	@RequestMapping(method = RequestMethod.GET, value = PersonesPath.GET_PERSONA_BY_CODI)
	ResponseEntity<Persona> getPersonaByCodi(
			@PathVariable("codi") String codi, 
			@RequestParam("entornId") Long entornId);

	@RequestMapping(method = RequestMethod.GET, value = PersonesPath.GET_PERSONA_ROLS_BY_CODI)
	ResponseEntity<List<String>> getPersonaRolsByCodi(
			@PathVariable("codi") String codi, 
			@RequestParam("entornId") Long entornId);

	@RequestMapping(method = RequestMethod.GET, value = PersonesPath.GET_PERSONES_CODI_BY_ROL)
	ResponseEntity<List<String>> getPersonesCodiByRol(
			@PathVariable("rol") String rol,
			@RequestParam("entornId") Long entornId);

	@RequestMapping(method = RequestMethod.GET, value = PersonesPath.GET_PERSONES_BY_CODI)
	ResponseEntity<List<Persona>> getPersonesByCodi(
			@RequestParam("codis") List<String> codis,
			@RequestParam("entornId") Long entornId);
}
