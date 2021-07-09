package es.caib.helium.back.controller.rest;

import es.caib.helium.logic.intf.dto.InteressatDto;
import es.caib.helium.logic.intf.service.WorkflowBridgeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST de terminis.
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/bridge/api/interessats")
public class InteressatRestController {
	
	private final WorkflowBridgeService workflowBridgeService;

	@PostMapping
	public ResponseEntity<Void> crear(@RequestBody InteressatDto interessat) {
		workflowBridgeService.interessatCrear(interessat);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping
	public ResponseEntity<Void> modificar(@RequestBody InteressatDto interessat) {
		workflowBridgeService.interessatModificar(interessat);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value="/{interessatCodi}/expedient/{expedientId}")
	public ResponseEntity<Void> eliminar(
			@PathVariable("interessatCodi") String interessatCodi,
			@PathVariable("expedientId") Long expedientId) {
		workflowBridgeService.interessatEliminar(interessatCodi, expedientId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private static final Log logger = LogFactory.getLog(InteressatRestController.class);
}
