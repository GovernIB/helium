package es.caib.helium.back.controller.rest;

import es.caib.helium.logic.intf.dto.DominiRespostaFilaDto;
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

import java.util.List;
import java.util.Map;

/**
 * API REST de terminis.
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/bridge/api/dominis")
public class DominiRestController {
	
	private final WorkflowBridgeService workflowBridgeService;

	@GetMapping(value="/{dominiId}/resultats")
	public ResponseEntity<List<DominiRespostaFilaDto>> get(
			@PathVariable("dominiId") String dominiId,
			@RequestParam(required = false) Map<String, Object> parametres) {

		String processInstanceId = null;
		String dominiCodi = null;
		if (parametres != null) {
			processInstanceId = (String)parametres.get("processInstanceId");
			dominiCodi = (String)parametres.get("dominiCodi");
			parametres.remove("processInstanceId");
			parametres.remove("dominiCodi");
		}

		return new ResponseEntity<>(
				workflowBridgeService.dominiConsultar(
						processInstanceId,
						dominiCodi,
						dominiId,
						parametres),
				HttpStatus.OK);
	}

	private static final Log logger = LogFactory.getLog(DominiRestController.class);
}
