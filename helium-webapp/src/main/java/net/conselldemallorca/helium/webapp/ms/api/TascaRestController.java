package net.conselldemallorca.helium.webapp.ms.api;

import net.conselldemallorca.helium.core.api.WorkflowBridgeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/api/tasques")
public class TascaRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

	@RequestMapping(value="/{taskId}/segonpla", method = RequestMethod.GET)
	@ResponseBody
	public Boolean isSegonPla(@PathVariable("taskId") Long taskId) {
		return workflowBridgeService.isTascaEnSegonPla(taskId);
	}

	@RequestMapping(value="/{taskId}/missatges", method = RequestMethod.POST)
	@ResponseBody
	public void addMissatge(
			@PathVariable("taskId") Long taskId,
			@RequestBody List<String> message,
			@RequestBody Exception ex) {

		if (ex != null) {
			workflowBridgeService.setErrorTascaSegonPla(
			taskId,
			ex);
		} else {
			workflowBridgeService.addMissatgeExecucioTascaSegonPla(
					taskId,
					message.toArray(new String[0]));
		}
	}
	
// TODO: completar
//	@RequestMapping(value="/{taskId}/missatges", method = RequestMethod.POST)
//	@ResponseBody
//	public void setError(
//			@PathVariable("taskId") Long taskId,
//			@RequestBody Exception ex) {
//
//		workflowBridgeService.setErrorTascaSegonPla(
//				taskId,
//				ex);
//	}

	private static final Log logger = LogFactory.getLog(TascaRestController.class);
}
