package net.conselldemallorca.helium.webapp.ms.api;

import lombok.Data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.helium.logic.intf.WorkflowBridgeService;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.FestiuDto;
import es.caib.helium.logic.intf.dto.ReassignacioDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/bridge/api/generic")
public class GenericRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

//	@RequestMapping(value="/properties/{propertyName}", method = RequestMethod.GET)
//	@ResponseBody
//	public String getProperty(
//			HttpServletRequest request,
//			@RequestParam(value = "propertyName", required = true) String propertyName) {
//
//		return workflowBridgeService.getHeliumProperty(propertyName);
//	}

	@RequestMapping(value="/usuariActual", method = RequestMethod.GET)
	@ResponseBody
	public String getUsuariActual() {

		return workflowBridgeService.getUsuariCodiActual();
	}

	@RequestMapping(value="/expedientIniciant", method = RequestMethod.GET)
	@ResponseBody
	public ExpedientDto getExpedientIniciant() {

		return workflowBridgeService.getExpedientIniciant();
	}

	@RequestMapping(value="/email", method = RequestMethod.POST)
	@ResponseBody
	public void emailSend(
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
	}

	@RequestMapping(value="/festius", method = RequestMethod.GET)
	@ResponseBody
	public List<FestiuDto> getFestius() {
		return workflowBridgeService.getFestiusAll();
	}

	@RequestMapping(value="/reassignacio/{usuariCodi}", method = RequestMethod.GET)
	@ResponseBody
	public ReassignacioDto get(
			HttpServletRequest request,
			@PathVariable(value = "usuariCodi") String usuariCodi,
			@RequestParam(value = "processInstanceId", required = true) String processInstanceId) {

		return workflowBridgeService.findReassignacioActivaPerUsuariOrigen(
				processInstanceId,
				usuariCodi);
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
