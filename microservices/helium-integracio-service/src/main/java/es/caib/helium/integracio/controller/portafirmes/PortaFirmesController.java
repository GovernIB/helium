package es.caib.helium.integracio.controller.portafirmes;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.servo.util.Strings;

import es.caib.helium.integracio.domini.portafirmes.PortaFirma;
import es.caib.helium.integracio.domini.portafirmes.PortaFirmesFlux;
import es.caib.helium.integracio.enums.portafirmes.TipusEstat;
import es.caib.helium.integracio.service.portafirmes.PortaFirmesService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(PortaFirmesController.API_PATH)
public class PortaFirmesController {

	public static final String API_PATH = "/api/v1/portasignatures";
	
	@Autowired
	private final PortaFirmesService portaFirmesService;

	@ExceptionHandler({ Exception.class })
	public void handleException(Exception e) {
		e.printStackTrace();
	}

	@GetMapping(value = "{documentId}", produces = "application/json")
	public ResponseEntity<PortaFirma> getByDocumentId(@PathVariable("documentId") Long documentId) throws Exception{ 
		
		// 400 bad input parameter
		if (documentId == null) {
			return new ResponseEntity<PortaFirma>(HttpStatus.BAD_REQUEST);
		}
		var document = portaFirmesService.getByDocumentId(documentId);
		if (document != null) {
			//200 ok
			return new ResponseEntity<PortaFirma>(document, HttpStatus.OK);
		}
		// 204 no content
		return new ResponseEntity<PortaFirma>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "proces/{processInstanceId}", produces = "application/json")
	public ResponseEntity<PortaFirma> getByProcessInstanceId(@PathVariable("processInstanceId") String processInstanceId) throws Exception { 
		
		// 400 bad input parameter
		if (processInstanceId == null) {
			return new ResponseEntity<PortaFirma>(HttpStatus.BAD_REQUEST);
		}
		var document = portaFirmesService.getByProcessInstanceId(processInstanceId);
		if (document != null) {
			//200 ok
			return new ResponseEntity<PortaFirma>(document, HttpStatus.OK);
		}
		// 204 no content
		return new ResponseEntity<PortaFirma>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "proces/{processInstanceId}/ds/{documentStoreId}", produces = "application/json")
	public ResponseEntity<PortaFirma> getByProcessInstanceIdAndDocumentStoreId(
			@PathVariable("processInstanceId") String processInstanceId, @PathVariable Long documentStoreId) throws Exception{ 
		
		// 400 bad input parameter
		if (processInstanceId == null || documentStoreId == null) {
			return new ResponseEntity<PortaFirma>(HttpStatus.BAD_REQUEST);
		}
		var document = portaFirmesService.getByProcessInstanceIdAndDocumentStoreId(processInstanceId, documentStoreId);
		if (document != null) {
			//200 ok
			return new ResponseEntity<PortaFirma>(document, HttpStatus.OK);
		}
		// 204 no content
		return new ResponseEntity<PortaFirma>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(produces = "application/json")
	public ResponseEntity<List<PortaFirma>> getPendentsFirmar(@QueryParam("filtre") String filtre) throws Exception{ 
		
		// 400 bad input parameter
		if (Strings.isNullOrEmpty(filtre)) {
			return new ResponseEntity<List<PortaFirma>>(HttpStatus.BAD_REQUEST);
		}
		List<PortaFirma> pendents = portaFirmesService.getPendentsFirmar(filtre);
		if (pendents != null && !pendents.isEmpty()) {
			//200 ok
			return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.OK);
		}
		// 204 no content
		return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "expedient/{expedientId}/estat/{estat}", produces = "application/json")
	public ResponseEntity<List<PortaFirma>> getByExpedientIdAndEstat(
			@PathVariable("expedientId") Long expedientId, @PathVariable("estat") TipusEstat estat) throws Exception{ 
		
		// 400 bad input parameter
		if (expedientId == null || estat == null) {
			return new ResponseEntity<List<PortaFirma>>(HttpStatus.BAD_REQUEST);
		}
		List<PortaFirma> pendents = portaFirmesService.getByExpedientIdAndEstat(expedientId, estat);
		if (pendents != null && !pendents.isEmpty()) {
			//200 ok
			return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.OK);
		}
		// 204 no content
		return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "proces/{processInstanceId}/not/estat", produces = "application/json")
	public ResponseEntity<List<PortaFirma>> getByProcessInstanceIdAndEstatNotIn(
			@PathVariable("processInstanceId") String processInstanceId, @RequestParam("estats") List<TipusEstat> estats) throws Exception{ 
		
		// 400 bad input parameter
		if (processInstanceId == null || estats == null) {
			return new ResponseEntity<List<PortaFirma>>(HttpStatus.BAD_REQUEST);
		}
		List<PortaFirma> pendents = portaFirmesService.getByProcessInstanceIdAndEstatNotIn(processInstanceId, estats);
		if (pendents != null && !pendents.isEmpty()) {
			//200 ok
			return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.OK);
		}
		// 204 no content
		return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(value = "{processInstance}/pendents", produces = "application/json")
	public ResponseEntity<List<PortaFirma>> getPendentsFirmarByProcessInstance(@PathVariable("processInstance") String processInstance) throws Exception  {
		

		// 400 bad input parameter
		if (processInstance == null) {
			return new ResponseEntity<List<PortaFirma>>(HttpStatus.BAD_REQUEST);
		}
		List<PortaFirma> pendents = portaFirmesService.getPendentsFirmarByProcessInstance(processInstance);
		if (pendents != null && !pendents.isEmpty()) {
			//200 ok
			return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.OK);
		}
		// 204 no content
		return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(consumes = "application/json")
	public ResponseEntity<Void> enviarPortaFirmes(@Valid @RequestBody PortaFirmesFlux document, BindingResult errors) throws Exception {
		
		if (errors.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (portaFirmesService.enviarPortaFirmes(document)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.GATEWAY_TIMEOUT);
	}

	@PostMapping(value = "portafirma", consumes = "application/json")
	public ResponseEntity<Void> postPortaFirma(@Valid @RequestBody PortaFirma document, BindingResult errors) throws Exception {
		
		if (errors.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (portaFirmesService.guardar(document)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.GATEWAY_TIMEOUT);
	}
	
	@DeleteMapping
	public ResponseEntity<Void> cancelarEnviaments(@RequestParam("documents") List<Long> documents) throws Exception{
		
		if (documents == null || !portaFirmesService.cancelarEnviament(documents)) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@GetMapping(value = "processar/{documentId}/callback")
	public ResponseEntity<PortaFirma> processarDocumentCallBack(
			@PathVariable("documentId") Long documentId,
			@QueryParam("rebutjat") boolean rebutjat, 
			@QueryParam("motiuRebuig") String motiu) throws Exception {
		
		if (documentId == null) {
			return new ResponseEntity<PortaFirma>(HttpStatus.BAD_REQUEST);
		}
		
		var portaFirma = portaFirmesService.processarCallBack(documentId, rebutjat, motiu);
		if (portaFirma == null) {
			return new ResponseEntity<PortaFirma>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<PortaFirma>(portaFirma, HttpStatus.OK);
	}

}
