package es.caib.helium.back.controller.rest;

import es.caib.helium.logic.intf.dto.EstatDto;
import es.caib.helium.logic.intf.service.WorkflowBridgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * API REST de terminis.
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/bridge/api/estats")
public class EstatRestController {
	
	private final WorkflowBridgeService workflowBridgeService;

	@GetMapping(value="/{entornId}")
	public ResponseEntity<EstatDto> getEstat(
			HttpServletRequest request,
			@PathVariable(value = "entornId") Long entornId,
			@RequestParam(value = "expedientTipusCodi", required = true) String expedientTipusCodi,
			@RequestParam(value = "estatCodi", required = true) String estatCodi) {

		return new ResponseEntity<>(
				workflowBridgeService.findEstatAmbEntornIExpedientTipusICodi(
						entornId,
						expedientTipusCodi,
						estatCodi),
				HttpStatus.OK);
	}

}
