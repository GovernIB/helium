package net.conselldemallorca.helium.webapp.ms.api;

import net.conselldemallorca.helium.core.api.WorkflowBridgeService;
import net.conselldemallorca.helium.v3.core.api.dto.DominiRespostaFilaDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/bridge/api/dominis")
public class DominiRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

	@RequestMapping(value="/{dominiId}/resultats", method = RequestMethod.GET)
	@ResponseBody
	public List<DominiRespostaFilaDto> get(
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

		return workflowBridgeService.dominiConsultar(
				processInstanceId,
				dominiCodi,
				dominiId,
				parametres);
	}

	private static final Log logger = LogFactory.getLog(DominiRestController.class);
}
