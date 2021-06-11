package net.conselldemallorca.helium.webapp.ms.api;

import net.conselldemallorca.helium.core.api.WorkflowBridgeService;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/bridge/api/definicionsProces")
public class DefinicioProcesRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;


	@RequestMapping(value="/{processInstanceId}/versio", method = RequestMethod.GET)
	@ResponseBody
	public Integer getDefinicioProcesVersioAmbJbpmKeyIProcessInstanceId(
			@RequestParam(value = "jbpmKey") String jbpmKey,
			@PathVariable("processInstanceId") String processInstanceId) {
		return workflowBridgeService.getDefinicioProcesVersioAmbJbpmKeyIProcessInstanceId(
				jbpmKey,
				processInstanceId);
	}

	@RequestMapping(value="/{processInstanceId}", method = RequestMethod.GET)
	@ResponseBody
	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(
			@PathVariable("processInstanceId") String processInstanceId) {
		return workflowBridgeService.getDefinicioProcesPerProcessInstanceId(processInstanceId);
	}

	@RequestMapping(value="/{processInstanceId}/id", method = RequestMethod.GET)
	@ResponseBody
	public Long getDefinicioProcesIdPerProcessInstanceId(
			@PathVariable("processInstanceId") String processInstanceId) {
		return workflowBridgeService.getDefinicioProcesIdPerProcessInstanceId(processInstanceId);
	}

	@RequestMapping(value="/byJbpmKeyAndVersio/entornId", method = RequestMethod.GET)
	@ResponseBody
	public Long getDefinicioProcesEntornAmbJbpmKeyIVersio(
			@RequestParam(value = "jbpmKey") String jbpmKey,
			@RequestParam(value = "version") Integer version) {
		return workflowBridgeService.getDefinicioProcesEntornAmbJbpmKeyIVersio(
				jbpmKey,
				version);
	}

	@RequestMapping(value="/byEntornAndJbpmKey/entornId", method = RequestMethod.GET)
	@ResponseBody
	public Long getDarreraVersioEntornAmbEntornIJbpmKey(
			@RequestParam(value = "entornId") Long entornId,
			@RequestParam(value = "jbpmKey") String jbpmKey) {
		return workflowBridgeService.getDarreraVersioEntornAmbEntornIJbpmKey(
				entornId,
				jbpmKey);
	}

	@RequestMapping(value="/initialize", method = RequestMethod.POST)
	@ResponseBody
	public void initializeDefinicionsProces() {
		workflowBridgeService.initializeDefinicionsProces();
	}

	@RequestMapping(value="/{processInstanceId}/byHerencia/id", method = RequestMethod.GET)
	@ResponseBody
	public String getProcessDefinitionIdHeretadaAmbPid(
			@PathVariable("processInstanceId") String processInstanceId) {
		return workflowBridgeService.getProcessDefinitionIdHeretadaAmbPid(processInstanceId);
	}

	private static final Log logger = LogFactory.getLog(DefinicioProcesRestController.class);
}
