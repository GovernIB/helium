/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientPipellesController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
	
	@Autowired
	private PluginService pluginService;

	@RequestMapping(value = "/{expedientId}", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {
		return mostrarInformacioExpedientPerPipella(request, expedientId, model, null, expedientService);
	}

	@RequestMapping(value = "/{expedientId}/imatgeProces", method = RequestMethod.GET)
	public String imatgeProces(
			HttpServletRequest request,
			@PathVariable(value = "expedientId") Long expedientId, 
			Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potConsultarExpedient(expedient)) {
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(expedient.getProcessInstanceId());
				if (instanciaProces.isImatgeDisponible()) {
					String resourceName = "processimage.jpg";
					model.addAttribute(
							ArxiuView.MODEL_ATTRIBUTE_FILENAME,
							resourceName);
					model.addAttribute(
							ArxiuView.MODEL_ATTRIBUTE_DATA,
							dissenyService.getDeploymentResource(
									instanciaProces.getDefinicioProces().getId(),"processimage.jpg"));
					return "arxiuView";
				} else {
					MissatgesHelper.error(request, getMessage(request, "error.info.expedient.imatgeproces"));
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.consultar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}		
		return "v3/expedientPipelles";
	}

}
