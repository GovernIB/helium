package es.caib.helium.client.integracio.unitat;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.integracio.unitat.model.UnitatOrganica;

public interface UnitatFeignClient {
	
	@RequestMapping(method = RequestMethod.GET, value = UnitatPath.CONSULTA_UNITAT)
	public ResponseEntity<UnitatOrganica> consultaUnitat(
			@Valid @PathVariable("codi") String codi,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.GET, value = UnitatPath.CONSULTA_ARBRE)
	public ResponseEntity<List<UnitatOrganica>> consultaArbre(
			@Valid @PathVariable("codi") String codi,
			@RequestParam("entornId") Long entornId);
}
