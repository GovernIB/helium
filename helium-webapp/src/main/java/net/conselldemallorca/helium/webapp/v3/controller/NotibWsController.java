/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.caib.notib.domini.NotificacioCanviClient;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;

/**
 * Controlador per a les peticions al servei REST de Notib
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/rest/notib")
public class NotibWsController {

	@Autowired
	private ExpedientDocumentService expedientDocumentService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String get() {
		return "restNotib";
	}

	@RequestMapping(value = "/notificaCanvi", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void enviarContingutPost(
			@RequestBody NotificacioCanviClient notificacioCanvi) {
		expedientDocumentService.notificacioActualitzarEstat(
				notificacioCanvi.getIdentificador(), 
				notificacioCanvi.getReferenciaEnviament());
	}
}
