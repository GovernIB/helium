package net.conselldemallorca.helium.webapp.ms.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.helium.logic.intf.WorkflowBridgeService;
import es.caib.helium.logic.intf.dto.EstatDto;

import javax.servlet.http.HttpServletRequest;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/bridge/api/estats")
public class EstatRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

	@RequestMapping(value="/{entornId}", method = RequestMethod.GET)
	@ResponseBody
	public EstatDto getEstat(
			HttpServletRequest request,
			@PathVariable(value = "entornId") Long entornId,
			@RequestParam(value = "expedientTipusCodi", required = true) String expedientTipusCodi,
			@RequestParam(value = "estatCodi", required = true) String estatCodi) {

		return workflowBridgeService.findEstatAmbEntornIExpedientTipusICodi(
				entornId,
				expedientTipusCodi,
				estatCodi);
	}

	private static final Log logger = LogFactory.getLog(EstatRestController.class);
}
