package es.caib.helium.client.integracio.firma;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.integracio.firma.model.FirmaPost;
import es.caib.helium.client.integracio.firma.model.FirmaResposta;

public interface FirmaFeignClient {
	
	@RequestMapping(method = RequestMethod.POST, value = FirmaPath.FIRMAR)
	public ResponseEntity<FirmaResposta> firmar(
			@Valid @RequestBody FirmaPost firma, 
			@RequestParam("entornId") Long entornId);
}
