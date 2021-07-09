package es.caib.helium.back.controller.rest;

import es.caib.helium.logic.intf.dto.CampTipusIgnored;
import es.caib.helium.logic.intf.service.WorkflowBridgeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST de terminis.
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/bridge/api/variables")
public class VariableRestController {
	
	private final WorkflowBridgeService workflowBridgeService;

	@GetMapping(value="/{varCodi}/campAndIgnored")
	public ResponseEntity<CampTipusIgnored> getCampAndIgnored(
			@PathVariable("varCodi") String varCodi,
			@RequestParam(value = "processDefinitionId") String processDefinitionId,
			@RequestParam("expedientId") Long expedientId) {
		return new ResponseEntity<>(
				workflowBridgeService.getCampAndIgnored(
						processDefinitionId,
						expedientId,
						varCodi),
				HttpStatus.OK);
	}

	private static final Log logger = LogFactory.getLog(VariableRestController.class);
}
