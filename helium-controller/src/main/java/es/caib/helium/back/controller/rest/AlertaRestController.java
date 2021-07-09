package es.caib.helium.back.controller.rest;

import es.caib.helium.logic.intf.service.WorkflowBridgeService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * API REST de terminis.
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/bridge/api/alertes")
public class AlertaRestController {
	
	private final WorkflowBridgeService workflowBridgeService;

	@PostMapping
	public ResponseEntity<Void> crear(
			HttpServletRequest request,
			@RequestBody Alerta alerta) {

		workflowBridgeService.alertaCrear(
				alerta.getEntornId(),
				alerta.getExpedientId(),
				alerta.getData(),
				alerta.getUsuariCodi(),
				alerta.getText());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value="/{taskInstanceId}")
	public ResponseEntity<Void> esborrar(@PathVariable("taskInstanceId") Long taskInstanceId) {
		workflowBridgeService.alertaEsborrarAmbTaskInstanceId(taskInstanceId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Data
	public class Alerta {
		private Long entornId;
		private Long expedientId;
		private Date data;
		private String usuariCodi;
		private String text;
	}

}
