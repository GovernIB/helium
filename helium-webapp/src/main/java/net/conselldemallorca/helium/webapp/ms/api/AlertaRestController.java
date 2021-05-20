package net.conselldemallorca.helium.webapp.ms.api;

import lombok.Data;
import net.conselldemallorca.helium.core.api.WorkflowBridgeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/api/alertes")
public class AlertaRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public void crear(
			HttpServletRequest request,
			@RequestBody Alerta alerta) {

		workflowBridgeService.alertaCrear(
				alerta.getEntornId(),
				alerta.getExpedientId(),
				alerta.getData(),
				alerta.getUsuariCodi(),
				alerta.getText());
	}

	@Data
	public class Alerta {
		private Long entornId;
		private Long expedientId;
		private Date data;
		private String usuariCodi;
		private String text;
	}

	private static final Log logger = LogFactory.getLog(AlertaRestController.class);
}
