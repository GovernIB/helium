/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.logic.intf.service.AdminService;
import es.caib.helium.logic.intf.service.ExpedientDocumentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

	//TODO DANIEL resoldre dependència
//	@RequestMapping(value = "/notificaCanvi", method = RequestMethod.POST)
//	@ResponseStatus(value = HttpStatus.OK)
//	public void enviarContingutPost(
//			@RequestBody NotificacioCanviClient notificacioCanvi) {
//
//		List<IntegracioParametreDto> parametres = new ArrayList<IntegracioParametreDto>();
//		parametres.add(new IntegracioParametreDto("identificador", notificacioCanvi.getIdentificador()));
//		parametres.add(new IntegracioParametreDto("referenciaEnviament", notificacioCanvi.getReferenciaEnviament()));
//		
//		long t0 = System.currentTimeMillis();
//		String accio = "Notificació de canvi d'estat";
//		adminService.monitorAddAccio(
//				MonitorIntegracioHelper.INTCODI_NOTIB, 
//				accio, 
//				IntegracioAccioTipusEnumDto.RECEPCIO, 
//				IntegracioAccioEstatEnumDto.OK, 
//				System.currentTimeMillis() - t0,
//				null, 
//				null, 
//				parametres);
//
//		try {
//			// Processa el canvi d'estat
//			expedientDocumentService.notificacioActualitzarEstat(
//					notificacioCanvi.getIdentificador(), 
//					notificacioCanvi.getReferenciaEnviament());
//		} catch (Exception e) {
//			String errMsg = "Error processant la notificació de canvi d'estat: " + e.getMessage();
//			logger.error(errMsg, e);
//		}
//	}
	private static final Log logger = LogFactory.getLog(NotibWsController.class);
}