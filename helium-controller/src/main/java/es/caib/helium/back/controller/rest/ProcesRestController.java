package es.caib.helium.back.controller.rest;

import java.util.Date;

import javax.json.Json;
import javax.json.JsonPatchBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.client.expedient.proces.ProcesClientService;
import es.caib.helium.client.expedient.proces.model.ProcesDto;
import es.caib.helium.client.helper.PatchHelper;
import lombok.RequiredArgsConstructor;

/**
 * API REST de processos.
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/bridge/api/processos")
public class ProcesRestController {
	
	private final ProcesClientService procesClientService;

	@PostMapping
	public ResponseEntity<Void> crear(@RequestBody ProcesDto proces) {
		procesClientService.createProcesV1(proces);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{procesId}/finalitzar")
	public ResponseEntity<Void> finalitzar(
			@PathVariable("procesId") String procesId,
			@RequestBody Date end) {
		
		// Informa la data de fi
        JsonPatchBuilder jpb = Json.createPatchBuilder();
        PatchHelper.replaceDateProperty(jpb, "dataFi", end);
		procesClientService.patchProcesV1(
				procesId,
				PatchHelper.toJsonNode(jpb));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private static final Log logger = LogFactory.getLog(ProcesRestController.class);
}
