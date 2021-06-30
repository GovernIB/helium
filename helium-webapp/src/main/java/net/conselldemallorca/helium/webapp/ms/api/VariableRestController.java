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
import es.caib.helium.logic.intf.dto.CampTipusIgnored;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/bridge/api/variables")
public class VariableRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

	@RequestMapping(value="/{varCodi}/campAndIgnored", method = RequestMethod.GET)
	@ResponseBody
	public CampTipusIgnored getCampAndIgnored(
			@PathVariable("varCodi") String varCodi,
			@RequestParam(value = "processDefinitionId") String processDefinitionId,
			@RequestParam("expedientId") Long expedientId) {
		return workflowBridgeService.getCampAndIgnored(
				processDefinitionId,
				expedientId,
				varCodi);
	}

	private static final Log logger = LogFactory.getLog(VariableRestController.class);
}
