package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalEstatEnum;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalFiltreDto;
import net.conselldemallorca.helium.v3.core.api.service.ConsultaPinbalService;
import net.conselldemallorca.helium.webapp.v3.command.ConsultesPinbalFiltreCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;

@Controller
@RequestMapping("/v3/expedient/{expedientId}/pinbal")
public class ExpedientPinbalController extends BaseExpedientController {

	@Autowired private ConsultaPinbalService consultesPinbalService;
	
	@RequestMapping(value="", method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("expedient",expedientService.findAmbId(expedientId));
		model.addAttribute("expedientId", expedientId);
		return "v3/expedientPinbalLlistat";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientId) {
		ConsultesPinbalFiltreCommand filtreCommand = new ConsultesPinbalFiltreCommand();
		filtreCommand.setExpedientId(expedientId);
		filtreCommand.setFromExpedient(true);
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		filtreCommand.setEntornId(entornActual.getId());
		PaginaDto<PeticioPinbalDto> resultat = consultesPinbalService.findAmbFiltrePaginat(
				paginacioParams,
				ConversioTipusHelper.convertir(filtreCommand, PeticioPinbalFiltreDto.class));
		return DatatablesHelper.getDatatableResponse(request, null, resultat);
	}
}
