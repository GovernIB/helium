package es.caib.helium.client.integracio.custodia;

import java.util.List;

import javax.validation.Valid;

import es.caib.helium.client.model.RespostaValidacioSignatura;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.integracio.custodia.model.CustodiaRequest;

public interface CustodiaFeignClient {

	@RequestMapping(method = RequestMethod.POST, value = CustodiaPath.AFEGIR_SIGNATURA)
	public ResponseEntity<String> afegirSignatura(
			@Valid @RequestBody CustodiaRequest request,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.GET, value = CustodiaPath.GET_SIGNATURES)
	public ResponseEntity<List<byte[]>> getSignatures(
			@PathVariable("documentId") String documentId, 
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.DELETE, value = CustodiaPath.DELETE_SIGNATURES)
	public ResponseEntity<Void> deleteSignatures(
			@PathVariable("documentId") String documentId,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.GET, value = CustodiaPath.GET_SIGNATURES_AMB_ARXIU)
	public ResponseEntity<byte[]> getSignaturesAmbArxiu(
			@PathVariable("documentId") String documentId,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.GET, value = CustodiaPath.GET_DADES_VALIDACIO_SIGNATURA)
	public ResponseEntity<List<RespostaValidacioSignatura>> getDadesValidacioSignatura(
			@PathVariable("documentId") String documentId,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.GET, value = CustodiaPath.GET_URL_COMPROVACIO_SIGNATURES)
	public ResponseEntity<String> getUrlComprovacioSignatures(
			@PathVariable("documentId") String documentId,
			@RequestParam("entornId") Long entornId);
}
