package es.caib.helium.back.controller.rest;

import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.FestiuDto;
import es.caib.helium.logic.intf.dto.ReassignacioDto;
import es.caib.helium.logic.intf.service.WorkflowBridgeService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/bridge/api/generic")
public class GenericRestController {
	
	private final WorkflowBridgeService workflowBridgeService;

//	@RequestMapping(value="/properties/{propertyName}")
//	@ResponseBody
//	public String getProperty(
//			HttpServletRequest request,
//			@RequestParam(value = "propertyName", required = true) String propertyName) {
//
//		return workflowBridgeService.getHeliumProperty(propertyName);
//	}

	@GetMapping(value="/usuariActual")
	public ResponseEntity<String> getUsuariActual() {

		return new ResponseEntity<>(
				workflowBridgeService.getUsuariCodiActual(),
				HttpStatus.OK);
	}

	@GetMapping(value="/expedientIniciant")
	public ResponseEntity<ExpedientDto> getExpedientIniciant() {

		return new ResponseEntity<>(
				workflowBridgeService.getExpedientIniciant(),
				HttpStatus.OK);
	}

	@PostMapping(value="/email")
	public ResponseEntity<Void> emailSend(
			HttpServletRequest request,
			@RequestBody Email email) {

		workflowBridgeService.emailSend(
				email.getFromAddress(),
				email.getRecipients(),
				email.getCcRecipients(),
				email.getBccRecipients(),
				email.getSubject(),
				email.getText(),
				email.getAttachments());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value="/festius", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FestiuDto>> getFestius() {
		return new ResponseEntity<>(
				workflowBridgeService.getFestiusAll(),
				HttpStatus.OK);
	}

	@GetMapping(value="/reassignacio/{usuariCodi}")
	public ResponseEntity<ReassignacioDto> get(
			HttpServletRequest request,
			@PathVariable(value = "usuariCodi") String usuariCodi,
			@RequestParam(value = "processInstanceId", required = true) String processInstanceId) {

		return new ResponseEntity<>(
				workflowBridgeService.findReassignacioActivaPerUsuariOrigen(
						processInstanceId,
						usuariCodi),
				HttpStatus.OK);
	}

	@Data
	public class Email {
		private String fromAddress;
		private List<String> recipients;
		private List<String> ccRecipients;
		private List<String> bccRecipients;
		private String subject;
		private String text;
		private List<ArxiuDto> attachments;
	}

	private static final Log logger = LogFactory.getLog(GenericRestController.class);
}
