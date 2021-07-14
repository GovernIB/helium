package es.caib.helium.client.integracio.validacio;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.helium.client.integracio.arxiu.model.ArxiuFirma;
import es.caib.helium.client.integracio.arxiu.model.ArxiuFirmaDetall;
import es.caib.helium.client.model.RespostaValidacioSignatura;
import es.caib.helium.client.integracio.validacio.model.VerificacioFirma;

public interface ValidacioFeignClient {

	@RequestMapping(method = RequestMethod.POST, value = ValidacioPath.VERIFICACIO)
	public ResponseEntity<RespostaValidacioSignatura> verificacio(
			@Valid @RequestBody VerificacioFirma verificacio);
	
	
	@RequestMapping(method = RequestMethod.POST, value = ValidacioPath.VALIDACIO_FIRMES)
	public ResponseEntity<List<ArxiuFirma>> validacioFirmes(
			@Valid @RequestBody VerificacioFirma validacio);
	
	@RequestMapping(method = RequestMethod.POST, value = ValidacioPath.VALIDACIO_DETALLS)
	public ResponseEntity<List<ArxiuFirmaDetall>> validacioDetalls(
			@Valid @RequestBody VerificacioFirma validacio);
}
