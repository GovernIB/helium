package es.caib.helium.client.integracio.firma;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.integracio.firma.model.FirmaPost;

public interface FirmaFeignClient {
	
	@RequestMapping(method = RequestMethod.POST, value = FirmaPath.FIRMAR)
	public ResponseEntity<byte[]> firmar(
			@Valid @RequestBody FirmaPost firma, 
			@RequestParam("entornId") Long entornId);
}
