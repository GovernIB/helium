package es.caib.helium.integracio.controller.portafirmes;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.servo.util.Strings;

import es.caib.helium.integracio.domini.portafirmes.PortaFirma;
import es.caib.helium.integracio.domini.portafirmes.PortaFirmesFlux;
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
	
	@PostMapping(consumes = "application/json")
	public ResponseEntity<Void> enviarPortaFirmes(@Valid @RequestBody PortaFirmesFlux document, BindingResult errors) {
		
		if (errors.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (portaFirmesService.enviarPortaFirmes(document)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.GATEWAY_TIMEOUT);
	}
	
	@GetMapping(value = "{processInstance}/pendents", produces = "application/json")
	public ResponseEntity<List<PortaFirma>> getPendentsFirmarByProcessInstance(@PathVariable("processInstance") Integer processInstance) throws Exception  {
		

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
}
