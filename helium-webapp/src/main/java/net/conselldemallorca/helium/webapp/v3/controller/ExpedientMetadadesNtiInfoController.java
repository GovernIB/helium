/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;

/**
 * Controlador per iniciar un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientMetadadesNtiInfoController extends BaseExpedientController {
	
	@Autowired
	private ExpedientService expedientService;
	
	
	@RequestMapping(value = "/{expedientId}/metadadesNti", method = RequestMethod.GET)
	public String info(
			@PathVariable Long expedientId,
			HttpServletRequest request,
			Model model) {
		
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient.getEstat() == null) {
			EstatDto estatDto = new EstatDto();
			if(expedient.getDataFi() == null) {
				estatDto.setNom(
						getMessage(request, "expedient.metadades.nti.estat.inicialitzat"));
			} else {
				estatDto.setNom(
						getMessage(request, "expedient.metadades.nti.estat.finalitzat"));
			}
			expedient.setEstat(estatDto);
		}
		
		model.addAttribute("expedient", expedient);
		
		return "v3/expedientMetadadesNtiInfo";
	}
	
}
