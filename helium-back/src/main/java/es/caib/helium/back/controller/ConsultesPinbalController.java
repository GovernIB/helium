package es.caib.helium.back.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.helium.back.command.ConsultesPinbalFiltreCommand;
import es.caib.helium.back.helper.ConversioTipus;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.back.helper.UsuariActualHelper;
import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.ExpedientTipusDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.ParellaCodiValorDto;
import es.caib.helium.commons.dto.PeticioPinbalDto;
import es.caib.helium.commons.dto.PeticioPinbalEstatEnum;
import es.caib.helium.commons.dto.PeticioPinbalFiltreDto;
import es.caib.helium.commons.dto.ScspRespostaPinbal;
import es.caib.helium.logic.intf.service.ConsultaPinbalService;

@Controller
@RequestMapping("/consultesPinbal")
public class ConsultesPinbalController extends BaseExpedientController {

	@Autowired private ConsultaPinbalService consultesPinbalService;

	private static final String SESSION_ATTRIBUTE_FILTRE = "ConsultesPinbalController.session.filtre";

	@RequestMapping(method = RequestMethod.GET)
	public String llistat(HttpServletRequest request, Model model) {

		ConsultesPinbalFiltreCommand filtreCommand = getFiltreCommand(request);
		List<ExpedientTipusDto> expedientTipusDtoAccessibles = null;

		ExpedientTipusDto expedientTipusActual = SessionHelper.getSessionManager(request).getExpedientTipusActual();
		if (expedientTipusActual != null) {
			filtreCommand.setTipusId(expedientTipusActual.getId());
		}

		//Si ets admin, no filtra per entorn actual.
		//ELs tipus de expedient del filtre son tots els accessibles
		if (UsuariActualHelper.isAdministrador(SecurityContextHolder.getContext().getAuthentication())) {

			expedientTipusDtoAccessibles = SessionHelper.getSessionManager(request).getExpedientTipusAccessibles();

		} else {

			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			if (entornActual != null) {
				filtreCommand.setEntornId(entornActual.getId());
				expedientTipusDtoAccessibles = expedientTipusService.findAmbEntornPermisAdmin(entornActual.getId());
			}

			if (expedientTipusDtoAccessibles==null || expedientTipusDtoAccessibles.size()==0) {
				MissatgesHelper.error(request, "No teniu permís d'administració sobre cap tipus d'expedient dins l'entorn actual.");
				return "redirect:/";
			}
		}

		model.addAttribute(filtreCommand);
		modelExpedientsTipus(expedientTipusDtoAccessibles, model);
		modelEstats(model);
		return "consultesPinbalLlistat";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {
		ConsultesPinbalFiltreCommand filtreCommand = getFiltreCommand(request);
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		filtreCommand.setEntornId(entornActual.getId());
		PaginaDto<PeticioPinbalDto> resultat = consultesPinbalService.findAmbFiltrePaginat(
				paginacioParams,
				ConversioTipus.convertir(filtreCommand, PeticioPinbalFiltreDto.class));
		return DatatablesHelper.getDatatableResponse(request, null, resultat);
	}

	@RequestMapping(value = "/{peticioPinbalId}/actualitzarEstat", method = RequestMethod.GET)
	@ResponseBody
	public ScspRespostaPinbal actualitzarEstat(
			HttpServletRequest request,
			@PathVariable Long peticioPinbalId) {
		return consultaPinbalService.tractamentPeticioAsincronaPendentPinbal(peticioPinbalId);
	}

	@RequestMapping(value = "/{peticioPinbalId}/info", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request,
			@PathVariable Long peticioPinbalId,
			Model model) {
		model.addAttribute("peticioPinbalDto", consultesPinbalService.findById(peticioPinbalId));
		return "consultesPinbalInfo";
	}

	@RequestMapping(value = "/infoByDocument/{expedientId}/{documentStoreId}", method = RequestMethod.GET)
	public String infoByDocument(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			Model model) {
		return info(request, consultesPinbalService.findByExpedientAndDocumentStore(expedientId, documentStoreId).getId(), model);
	}

	private ConsultesPinbalFiltreCommand getFiltreCommand(HttpServletRequest request) {
		ConsultesPinbalFiltreCommand filtreCommand = (ConsultesPinbalFiltreCommand) SessionHelper.getAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ConsultesPinbalFiltreCommand();
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return filtreCommand;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ConsultesPinbalFiltreCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return "redirect:consultesPinbal";
	}

	private void modelExpedientsTipus(List<ExpedientTipusDto> expedientTipusDtoAccessibles, Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
			for(ExpedientTipusDto expedientTipus : expedientTipusDtoAccessibles)
				opcions.add(new ParellaCodiValorDto(expedientTipus.getId().toString(), expedientTipus.getNom()));

		model.addAttribute("expedientsTipus", opcions);
	}

	private void modelEstats(Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
		for(PeticioPinbalEstatEnum estat : PeticioPinbalEstatEnum.values())
			opcions.add(new ParellaCodiValorDto(
					estat.name(),
					MessageHelper.getInstance().getMessage("enum.pinbal.estat." + estat.name())));

		model.addAttribute("estats", opcions);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
}
