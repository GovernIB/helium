package es.caib.helium.back.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.helium.back.command.ConsultesPinbalFiltreCommand;
import es.caib.helium.back.helper.ConversioTipus;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.PeticioPinbalDto;
import es.caib.helium.commons.dto.PeticioPinbalFiltreDto;
import es.caib.helium.logic.intf.service.ConsultaPinbalService;

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
				ConversioTipus.convertir(filtreCommand, PeticioPinbalFiltreDto.class));
		return DatatablesHelper.getDatatableResponse(request, null, resultat);
	}
}
