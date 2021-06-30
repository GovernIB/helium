package net.conselldemallorca.helium.webapp.ms.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.helium.logic.intf.WorkflowBridgeService;
import es.caib.helium.logic.intf.dto.InteressatDto;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/bridge/api/interessats")
public class InteressatRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public void crear(@RequestBody InteressatDto interessat) {
		workflowBridgeService.interessatCrear(interessat);
	}

	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public void modificar(@RequestBody InteressatDto interessat) {
		workflowBridgeService.interessatModificar(interessat);
	}

	@RequestMapping(value="/{interessatCodi}/expedient/{expedientId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void eliminar(
			@PathVariable("interessatCodi") String interessatCodi,
			@PathVariable("expedientId") Long expedientId) {
		workflowBridgeService.interessatEliminar(interessatCodi, expedientId);
	}

	private static final Log logger = LogFactory.getLog(InteressatRestController.class);
}
