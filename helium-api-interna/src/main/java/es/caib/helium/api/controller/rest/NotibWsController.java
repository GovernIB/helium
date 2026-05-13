package es.caib.helium.api.controller.rest;

import es.caib.helium.commons.dto.IntegracioAccioEstatEnumDto;
import es.caib.helium.commons.dto.IntegracioAccioTipusEnumDto;
import es.caib.helium.commons.dto.IntegracioParametreDto;
import es.caib.helium.logic.helper.MonitorIntegracioHelper;
import es.caib.helium.logic.intf.service.AdminService;
import es.caib.helium.logic.intf.service.ExpedientDocumentService;
import es.caib.notib.client.domini.NotificacioCanviClient;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rest/notib")
@Tag(
	name = "Notib",
	description = "API REST d'integració amb Notib.")
public class NotibWsController {

	@Autowired
	private AdminService adminService;
	@Autowired
	private ExpedientDocumentService expedientDocumentService;

	@GetMapping
	public String get() {
		return "restNotib";
	}

	@PostMapping("/notificaCanvi")
	public void enviarContingutPost(@RequestBody NotificacioCanviClient notificacioCanvi) {

		List<IntegracioParametreDto> parametres = new ArrayList<IntegracioParametreDto>();
		parametres.add(new IntegracioParametreDto("identificador", notificacioCanvi.getIdentificador()));
		parametres.add(new IntegracioParametreDto("referenciaEnviament", notificacioCanvi.getReferenciaEnviament()));

		long t0 = System.currentTimeMillis();
		String accio = "Notificació de canvi d'estat";
		adminService.monitorAddAccio(
			MonitorIntegracioHelper.INTCODI_NOTIB,
			accio,
			IntegracioAccioTipusEnumDto.RECEPCIO,
			IntegracioAccioEstatEnumDto.OK,
			System.currentTimeMillis() - t0,
			null,
			null,
			parametres);

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
