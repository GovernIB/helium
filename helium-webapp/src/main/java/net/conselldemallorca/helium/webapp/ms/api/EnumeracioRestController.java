package net.conselldemallorca.helium.webapp.ms.api;

import lombok.Data;
import net.conselldemallorca.helium.core.api.WorkflowBridgeService;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioValorDto;
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
import java.util.List;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/api/enumeracions")
public class EnumeracioRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

	@RequestMapping(value="/{enumeracioCodi}", method = RequestMethod.GET)
	@ResponseBody
	public List<EnumeracioValorDto> get(
			HttpServletRequest request,
			@PathVariable(value = "enumeracioCodi") String enumeracioCodi,
			@RequestParam(value = "processInstanceId", required = true) String processInstanceId) {

		return workflowBridgeService.enumeracioConsultar(
				processInstanceId,
				enumeracioCodi);
	}

	@RequestMapping(value="/{enumeracioCodi}/enumeracio", method = RequestMethod.POST)
	@ResponseBody
	public void set(
			HttpServletRequest request,
			@PathVariable(value = "enumeracioCodi") String enumeracioCodi,
			@RequestBody Enumeracio enumeracio) {

		workflowBridgeService.enumeracioSetValor(
				enumeracio.getProcessInstanceId(),
				enumeracioCodi,
				enumeracio.getCodi(),
				enumeracio.getValor());
	}

	@Data
	public class Enumeracio {
		private String processInstanceId;
		private String enumeracioCodi;
		private String codi;
		private String valor;
	}

	private static final Log logger = LogFactory.getLog(EnumeracioRestController.class);
}
