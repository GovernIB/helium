package es.caib.helium.back.controller.rest;

import es.caib.helium.logic.intf.dto.EnumeracioValorDto;
import es.caib.helium.logic.intf.service.WorkflowBridgeService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * API REST de terminis.
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/bridge/api/enumeracions")
public class EnumeracioRestController {
	
	private final WorkflowBridgeService workflowBridgeService;

	@GetMapping(value="/{enumeracioCodi}")
	public ResponseEntity<List<EnumeracioValorDto>> get(
			HttpServletRequest request,
			@PathVariable(value = "enumeracioCodi") String enumeracioCodi,
			@RequestParam(value = "processInstanceId", required = true) String processInstanceId) {

		return new ResponseEntity<>(
				workflowBridgeService.enumeracioConsultar(
						processInstanceId,
						enumeracioCodi),
				HttpStatus.OK);
	}

	@PostMapping(value="/{enumeracioCodi}/enumeracio")
	public ResponseEntity<Void> set(
			HttpServletRequest request,
			@PathVariable(value = "enumeracioCodi") String enumeracioCodi,
			@RequestBody Enumeracio enumeracio) {

		workflowBridgeService.enumeracioSetValor(
				enumeracio.getProcessInstanceId(),
				enumeracioCodi,
				enumeracio.getCodi(),
				enumeracio.getValor());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Data
	public class Enumeracio {
		private String processInstanceId;
		private String enumeracioCodi;
		private String codi;
		private String valor;
	}

	private static final Log logger = LogFactory.getLog(EnumeracioRestController.class);
}
