/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.caib.notib.domini.NotificacioCanviClient;
import net.conselldemallorca.helium.core.helper.MonitorIntegracioHelper;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
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
	private AdminService adminService;

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

		List<IntegracioParametreDto> parametres = new ArrayList<IntegracioParametreDto>();
		parametres.add(new IntegracioParametreDto("identificador", notificacioCanvi.getIdentificador()));
		parametres.add(new IntegracioParametreDto("referenciaEnviament", notificacioCanvi.getReferenciaEnviament()));
		
		long t0 = System.currentTimeMillis();
		String accio = "Notificació de canvi d'estat";
		try {
			// Processa el canvi d'estat
			expedientDocumentService.notificacioActualitzarEstat(
					notificacioCanvi.getIdentificador(), 
					notificacioCanvi.getReferenciaEnviament());

			adminService.monitorAddAccio(
					MonitorIntegracioHelper.INTCODI_NOTIB, 
					accio, 
					IntegracioAccioTipusEnumDto.RECEPCIO, 
					IntegracioAccioEstatEnumDto.OK, 
					System.currentTimeMillis() - t0,
					null, 
					null, 
					parametres);
		} catch (Exception e) {
			String errMsg = "Error processant la notificació de canvi d'estat: " + e.getMessage();
			logger.error(errMsg, e);
			adminService.monitorAddAccio(
					MonitorIntegracioHelper.INTCODI_NOTIB, 
					accio, 
					IntegracioAccioTipusEnumDto.RECEPCIO, 
					IntegracioAccioEstatEnumDto.ERROR, 
					System.currentTimeMillis() - t0,
					errMsg, 
					new Exception(), 
					parametres);			
		}
	}
	private static final Log logger = LogFactory.getLog(NotibWsController.class);
}
