package net.conselldemallorca.helium.webapp.ms.api;

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
import es.caib.helium.logic.intf.dto.CampTascaDto;
import es.caib.helium.logic.intf.dto.DocumentTascaDto;

import java.util.List;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/bridge/api/tasques")
public class TascaRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

	@RequestMapping(value="/{processInstanceId}/task/{taskName}/camps", method = RequestMethod.GET)
	@ResponseBody
	public List<CampTascaDto> findCampsPerTaskInstance(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestParam(value = "processDefinitionId") String processDefinitionId,
			@PathVariable("taskName") String taskName) {
		return workflowBridgeService.findCampsPerTaskInstance(
				processInstanceId,
				processDefinitionId,
				taskName);
	}

	@RequestMapping(value="/{processInstanceId}/task/{taskName}/documents", method = RequestMethod.GET)
	@ResponseBody
	public List<DocumentTascaDto> findDocumentsPerTaskInstance(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestParam(value = "processDefinitionId") String processDefinitionId,
			@PathVariable("taskName") String taskName) {
		return workflowBridgeService.findDocumentsPerTaskInstance(
				processInstanceId,
				processDefinitionId,
				taskName);
	}

	@RequestMapping(value="/{processInstanceId}/dades/{varCodi}", method = RequestMethod.GET)
	@ResponseBody
	public String getDadaPerTaskInstance(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestParam(value = "taskInstanceId") String taskInstanceId,
			@PathVariable("varCodi") String varCodi) {
		return workflowBridgeService.getDadaPerTaskInstance(
				processInstanceId,
				taskInstanceId,
				varCodi);
	}

	@RequestMapping(value="/{processInstanceId}/campTasca/{varCodi}", method = RequestMethod.GET)
	@ResponseBody
	public CampTascaDto getDadaPerTaskInstance(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestParam(value = "taskName") String taskName,
			@RequestParam(value = "processDefinitionId") String processDefinitionId,
			@PathVariable("varCodi") String varCodi) {
		return workflowBridgeService.getCampTascaPerInstanciaTasca(
				taskName,
				processDefinitionId,
				processInstanceId,
				varCodi);
	}

//	@RequestMapping(value="/{taskId}/isSegonPla", method = RequestMethod.GET)
//	@ResponseBody
//	public boolean isTascaEnSegonPla(@PathVariable("taskId") Long taskId) {
//		return workflowBridgeService.isTascaEnSegonPla(taskId);
//	}

	@RequestMapping(value="/{taskId}/missatge", method = RequestMethod.POST)
	@ResponseBody
	public void addMissatgeExecucioTascaSegonPla(
			@PathVariable("taskId") Long taskId,
			@RequestBody String[] message) {
		workflowBridgeService.addMissatgeExecucioTascaSegonPla(taskId, message);
	}

	@RequestMapping(value="/{taskId}/error", method = RequestMethod.POST)
	@ResponseBody
	public void setErrorTascaSegonPla(
			@PathVariable("taskId") Long taskId,
			@RequestBody String error ) {
		workflowBridgeService.setErrorTascaSegonPla(taskId, error);
	}

	private static final Log logger = LogFactory.getLog(TascaRestController.class);
}
