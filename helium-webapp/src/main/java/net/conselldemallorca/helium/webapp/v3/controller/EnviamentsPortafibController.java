package net.conselldemallorca.helium.webapp.v3.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



/**
 * Controlador per visualitzar la llista de peticions enviades des d'Helium al PortaFib.
 */
@Controller
@RequestMapping("/v3/enviamentsPortafib")
public class EnviamentsPortafibController extends BaseExpedientController {
	
	private static final Log logger = LogFactory.getLog(EnviamentsPortafibController.class);
}
