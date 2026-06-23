package es.caib.helium.api.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.helium.logic.intf.service.ExpedientDocumentService;
import es.caib.notib.client.domini.NotificacioCanviClient;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rest/notib")
@Tag(
	name = "Notib",
	description = "API REST d'integració amb Notib.")
public class NotibWsController {

	@Autowired
	private ExpedientDocumentService expedientDocumentService;

	@GetMapping
	public String get() {
		return "restNotib";
	}

	@PostMapping("/notificaCanvi")
	public void enviarContingutPost(@RequestBody NotificacioCanviClient notificacioCanvi) {

		try {
			// Processa el canvi d'estat
			expedientDocumentService.notificacioActualitzarEstat(
				notificacioCanvi.getIdentificador(),
				notificacioCanvi.getReferenciaEnviament());
		} catch (Exception e) {
			String errMsg = "Error processant la notificació de canvi d'estat: " + e.getMessage();
			log.error(errMsg, e);
		}
	}
}
