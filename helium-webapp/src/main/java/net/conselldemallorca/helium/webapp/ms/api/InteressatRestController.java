package net.conselldemallorca.helium.webapp.ms.api;

import net.conselldemallorca.helium.core.api.WorkflowBridgeService;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/api/interessats")
public class InteressatRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public void crear(@RequestBody InteressatDto interessat) {
		workflowBridgeService.interessatCrear(interessat);
	}

	@RequestMapping(method = RequestMethod.PATCH)
	@ResponseBody
	public void modificar(@RequestBody InteressatDto interessat) {
		workflowBridgeService.interessatModificar(interessat);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	public void eliminar(@RequestBody InteressatDto interessat) {
		workflowBridgeService.interessatEliminar(interessat);
	}

	private static final Log logger = LogFactory.getLog(InteressatRestController.class);
}
