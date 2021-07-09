package es.caib.helium.back.controller.rest;

import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.service.WorkflowBridgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST de terminis.
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/bridge/api/definicionsProces")
public class DefinicioProcesRestController {
	
	private final WorkflowBridgeService workflowBridgeService;


	@GetMapping(value="/{processInstanceId}/versio")
	public ResponseEntity<Integer> getDefinicioProcesVersioAmbJbpmKeyIProcessInstanceId(
			@RequestParam(value = "jbpmKey") String jbpmKey,
			@PathVariable("processInstanceId") String processInstanceId) {
		return new ResponseEntity<>(
				workflowBridgeService.getDefinicioProcesVersioAmbJbpmKeyIProcessInstanceId(
						jbpmKey,
						processInstanceId),
				HttpStatus.OK);
	}

	@GetMapping(value="/{processInstanceId}")
	public ResponseEntity<DefinicioProcesDto> getDefinicioProcesPerProcessInstanceId(
			@PathVariable("processInstanceId") String processInstanceId) {
		return new ResponseEntity<>(
				workflowBridgeService.getDefinicioProcesPerProcessInstanceId(processInstanceId),
				HttpStatus.OK);
	}

	@GetMapping(value="/{processInstanceId}/id")
	public ResponseEntity<Long> getDefinicioProcesIdPerProcessInstanceId(
			@PathVariable("processInstanceId") String processInstanceId) {
		return new ResponseEntity<>(
				workflowBridgeService.getDefinicioProcesIdPerProcessInstanceId(processInstanceId),
				HttpStatus.OK);
	}

	@GetMapping(value="/byJbpmKeyAndVersio/entornId")
	public ResponseEntity<Long> getDefinicioProcesEntornAmbJbpmKeyIVersio(
			@RequestParam(value = "jbpmKey") String jbpmKey,
			@RequestParam(value = "version") Integer version) {
		return new ResponseEntity<>(
				workflowBridgeService.getDefinicioProcesEntornAmbJbpmKeyIVersio(
						jbpmKey,
						version),
				HttpStatus.OK);
	}

	@GetMapping(value="/byEntornAndJbpmKey/entornId")
	public ResponseEntity<Long> getDarreraVersioEntornAmbEntornIJbpmKey(
			@RequestParam(value = "entornId") Long entornId,
			@RequestParam(value = "jbpmKey") String jbpmKey) {
		return new ResponseEntity<>(
				workflowBridgeService.getDarreraVersioEntornAmbEntornIJbpmKey(
						entornId,
						jbpmKey),
				HttpStatus.OK);
	}

	@PostMapping(value="/initialize")
	public ResponseEntity<Void> initializeDefinicionsProces() {
		workflowBridgeService.initializeDefinicionsProces();
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value="/{processInstanceId}/byHerencia/id")
	public ResponseEntity<String> getProcessDefinitionIdHeretadaAmbPid(
			@PathVariable("processInstanceId") String processInstanceId) {
		return new ResponseEntity<>(
				workflowBridgeService.getProcessDefinitionIdHeretadaAmbPid(processInstanceId),
				HttpStatus.OK);
	}

}
