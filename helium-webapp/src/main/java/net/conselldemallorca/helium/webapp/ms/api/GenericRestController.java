package net.conselldemallorca.helium.webapp.ms.api;

import lombok.Data;
import net.conselldemallorca.helium.core.api.WorkflowBridgeService;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/api/generic")
public class GenericRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

	@RequestMapping(value="/properties/{propertyName}", method = RequestMethod.GET)
	@ResponseBody
	public String getProperty(
			HttpServletRequest request,
			@RequestParam(value = "propertyName", required = true) String propertyName) {

		return workflowBridgeService.getHeliumProperty(propertyName);
	}

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
			String fromAddress,
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text,
			List<ArxiuDto> attachments);
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
