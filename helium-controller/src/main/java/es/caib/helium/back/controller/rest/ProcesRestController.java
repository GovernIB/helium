package es.caib.helium.back.controller.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.helium.client.expedient.proces.ProcesClientService;
import es.caib.helium.client.expedient.proces.model.ProcesDto;
import lombok.RequiredArgsConstructor;

/**
 * API REST de processos.
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/bridge/api/processos")
public class ProcesRestController {
	
	private final ProcesClientService procesServiceService;

	@PostMapping
	public ResponseEntity<Void> crear(@RequestBody ProcesDto proces) {
		procesServiceService.createProcesV1(proces);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private static final Log logger = LogFactory.getLog(ProcesRestController.class);
}
