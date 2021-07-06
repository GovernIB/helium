package es.caib.helium.integracio.controller.portafirmes;

import java.util.List;

import javax.validation.Valid;

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
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping(PortaFirmesController.API_PATH)
@Slf4j
public class PortaFirmesController {

	public static final String API_PATH = "/api/v1/portasignatures";
	
	@Autowired
	private final PortaFirmesService portaFirmesService;

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Void> handleException(Exception e) {
		e.printStackTrace();
		return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = "{documentId}", produces = "application/json")
	public ResponseEntity<PortaFirma> getByDocumentId(
			@PathVariable("documentId") Long documentId, 
			@RequestParam("entornId") Long entornId) throws Exception{ 
		
		log.info("Consultant al repository del portafirmes by documentId " + documentId);
		if (documentId == null) {
			return new ResponseEntity<PortaFirma>(HttpStatus.BAD_REQUEST);
		}
		var document = portaFirmesService.getByDocumentId(documentId, entornId);
		if (document != null) {
			return new ResponseEntity<PortaFirma>(document, HttpStatus.OK);
		}
		return new ResponseEntity<PortaFirma>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "proces/{processInstanceId}", produces = "application/json")
	public ResponseEntity<PortaFirma> getByProcessInstanceId(
			@PathVariable("processInstanceId") String processInstanceId, 
			@RequestParam("entornId") Long entornId) throws Exception { 
		
		log.info("Consultant el repository del portafirmes by procesInstanceId " + processInstanceId);
		if (processInstanceId == null) {
			return new ResponseEntity<PortaFirma>(HttpStatus.BAD_REQUEST);
		}
		var document = portaFirmesService.getByProcessInstanceId(processInstanceId, entornId);
		if (document != null) {
			return new ResponseEntity<PortaFirma>(document, HttpStatus.OK);
		}
		return new ResponseEntity<PortaFirma>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "proces/{processInstanceId}/ds/{documentStoreId}", produces = "application/json")
	public ResponseEntity<PortaFirma> getByProcessInstanceIdAndDocumentStoreId(
			@PathVariable("processInstanceId") String processInstanceId, 
			@PathVariable Long documentStoreId, 
			@RequestParam("entornId") Long entornId) throws Exception{ 
		
		log.info("Consultant el repository del portafirmes by processInstanceId " + processInstanceId + " i documentStoreId" + documentStoreId);
		if (processInstanceId == null || documentStoreId == null) {
			return new ResponseEntity<PortaFirma>(HttpStatus.BAD_REQUEST);
		}
		var document = portaFirmesService.getByProcessInstanceIdAndDocumentStoreId(processInstanceId, documentStoreId, entornId);
		if (document != null) {
			return new ResponseEntity<PortaFirma>(document, HttpStatus.OK);
		}
		return new ResponseEntity<PortaFirma>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(produces = "application/json")
	public ResponseEntity<List<PortaFirma>> getPendentsFirmar(
			@RequestParam("filtre") String filtre, 
			@RequestParam("entornId") Long entornId) throws Exception{ 
		
		log.info("Consultant  el repository del portafirmes by pendents i filtre " + filtre);
		if (Strings.isNullOrEmpty(filtre)) {
			return new ResponseEntity<List<PortaFirma>>(HttpStatus.BAD_REQUEST);
		}
		List<PortaFirma> pendents = portaFirmesService.getPendentsFirmar(filtre, entornId);
		if (pendents != null && !pendents.isEmpty()) {
			return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.OK);
		}
		return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "expedient/{expedientId}/estat/{estat}", produces = "application/json")
	public ResponseEntity<List<PortaFirma>> getByExpedientIdAndEstat(
			@PathVariable("expedientId") Long expedientId, 
			@PathVariable("estat") TipusEstat estat,
			@RequestParam("entornId") Long entornId) throws Exception{ 
		
		log.info("Consultant el repository del portafirmes by expedientId " + expedientId + " estat " + estat.name());
		if (expedientId == null || estat == null) {
			return new ResponseEntity<List<PortaFirma>>(HttpStatus.BAD_REQUEST);
		}
		List<PortaFirma> pendents = portaFirmesService.getByExpedientIdAndEstat(expedientId, estat, entornId);
		if (pendents != null && !pendents.isEmpty()) {
			return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.OK);
		}
		return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "proces/{processInstanceId}/not/estat", produces = "application/json")
	public ResponseEntity<List<PortaFirma>> getByProcessInstanceIdAndEstatNotIn(
			@PathVariable("processInstanceId") String processInstanceId, 
			@RequestParam("estats") List<TipusEstat> estats,
			@RequestParam("entornId") Long entornId) throws Exception{ 
		
		log.info("Consultant el repository del portafirmes by processInstanceId " + processInstanceId + " i estats not in " + estats.toString() );
		if (processInstanceId == null || estats == null) {
			return new ResponseEntity<List<PortaFirma>>(HttpStatus.BAD_REQUEST);
		}
		List<PortaFirma> pendents = portaFirmesService.getByProcessInstanceIdAndEstatNotIn(processInstanceId, estats, entornId);
		if (pendents != null && !pendents.isEmpty()) {
			return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.OK);
		}
		return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(value = "{processInstance}/pendents", produces = "application/json")
	public ResponseEntity<List<PortaFirma>> getPendentsFirmarByProcessInstance(
			@PathVariable("processInstance") String processInstance, 
			@RequestParam("entornId") Long entornId) throws Exception  {
		
		log.info("Consultant el repository del portafirmes by pendentes i processInstanceId " + processInstance);
		if (processInstance == null) {
			return new ResponseEntity<List<PortaFirma>>(HttpStatus.BAD_REQUEST);
		}
		List<PortaFirma> pendents = portaFirmesService.getPendentsFirmarByProcessInstance(processInstance, entornId);
		if (pendents != null && !pendents.isEmpty()) {
			return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.OK);
		}
		return new ResponseEntity<List<PortaFirma>>(pendents, HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(consumes = "application/json")
	public ResponseEntity<Void> enviarPortaFirmes(
			@Valid @RequestBody PortaFirmesFlux document, 
			BindingResult errors) throws Exception {
		
		log.info("Enviant document al portafirmes " + document.toString());
		if (errors.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (portaFirmesService.enviarPortaFirmes(document)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}

	@PostMapping(value = "portafirma", consumes = "application/json")
	public ResponseEntity<Void> postPortaFirma(
			@Valid @RequestBody PortaFirma document,
			@RequestParam("entornId") Long entornId, 
			BindingResult errors) throws Exception {
		
		log.info("Guardant al repository del portafirmes " + document.toString());
		if (errors.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (portaFirmesService.guardar(document, entornId)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}
	
	@DeleteMapping
	public ResponseEntity<Void> cancelarEnviaments(
			@RequestParam("documents") List<Long> documents, 
			@RequestParam("entornId") Long entornId) throws Exception {
		
		
		log.info("Cancelant enviaments al portafirmes " + documents.toString());
		if (documents == null || !portaFirmesService.cancelarEnviament(documents, entornId)) {
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@GetMapping(value = "processar/{documentId}/callback")
	public ResponseEntity<Boolean> processarDocumentCallBack(
			@PathVariable("documentId") Long documentId,
			@RequestParam("rebutjat") boolean rebutjat, 
			@RequestParam("motiuRebuig") String motiu,
			@RequestParam("entornId") Long entornId) throws Exception {
		
		log.info("Processant documentCallBack documentId " + documentId + " rebutjat " + rebutjat + " motiu " + motiu);
		if (documentId == null) {
			return new ResponseEntity<Boolean>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Boolean>(portaFirmesService.processarCallBack(documentId, rebutjat, motiu, entornId), HttpStatus.OK);
	}

}
