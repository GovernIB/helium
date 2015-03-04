package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controlador per a la pàgina d'accions de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientAccionsController extends BaseExpedientController {

	@RequestMapping(value = "/{expedientId}/accions", method = RequestMethod.GET)
	public String documents(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		return null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientAccionsController.class);
}
