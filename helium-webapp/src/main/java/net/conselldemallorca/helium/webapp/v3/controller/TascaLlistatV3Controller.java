/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.command.TascaConsultaCommand;
import net.conselldemallorca.helium.webapp.v3.datatables.DatatablesPagina;
import net.conselldemallorca.helium.webapp.v3.helper.PaginacioHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controlador per al llistat d'tasques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/tasca")
public class TascaLlistatV3Controller extends BaseExpedientController {

	@Autowired
	private TascaService tascaService;
	@Autowired
	private DissenyService dissenyService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		TascaConsultaCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(filtreCommand);
		return "v3/tascaLlistat";
	}
	
	@ModelAttribute("prioritats")
	public List<ParellaCodiValorDto> populateEstats(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(getMessage(request, "txt.m_alta"), 2));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "txt.alta"), 1));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "txt.normal"), 0));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "txt.baixa"), -1));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "txt.m_baixa"), -2));
		return resposta;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid TascaConsultaCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		filtre(request, filtreCommand, bindingResult, accio);
		return "redirect:tasca";
	}
	@RequestMapping(value = "/filtre", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void filtre(
			HttpServletRequest request,
			@Valid TascaConsultaCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(
					request,
					SessionHelper.VARIABLE_FILTRE_CONSULTA_TASCA);
		} else {
			SessionHelper.setAttribute(
					request,
					SessionHelper.VARIABLE_FILTRE_CONSULTA_TASCA,
					filtreCommand);
		}
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesPagina<ExpedientTascaDto> datatable(
			HttpServletRequest request,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		TascaConsultaCommand filtreCommand = getFiltreCommand(request);
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_TASCA,
				filtreCommand);
		return PaginacioHelper.getPaginaPerDatatables(
				request,
				tascaService.findPerFiltrePaginat(
						entornActual.getId(),
						filtreCommand.getExpedientTipusId(),
						request.getUserPrincipal().getName(),
						filtreCommand.getTasca(),
						filtreCommand.getExpedient(),
						filtreCommand.getDataCreacioInicial(),
						filtreCommand.getDataCreacioFinal(),
						filtreCommand.getDataLimitInicial(),
						filtreCommand.getDataLimitFinal(),
						filtreCommand.getPrioritat(),
						filtreCommand.isMostrarTasquesPersonals(),
						filtreCommand.isMostrarTasquesGrup(),
						PaginacioHelper.getPaginacioDtoFromDatatable(request)));
	}

	@RequestMapping(value = "/filtre/netejar", method = RequestMethod.GET)
	public String filtreNetejar(HttpServletRequest request) {
		SessionHelper.removeAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_TASCA);
		return "redirect:../../tasca";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	private TascaConsultaCommand getFiltreCommand(
			HttpServletRequest request) {
		TascaConsultaCommand filtreCommand = SessionHelper.getSessionManager(request).getFiltreConsultaTasca();
		if (filtreCommand == null) {
			filtreCommand = new TascaConsultaCommand();
			filtreCommand.setConsultaRealitzada(true);
			SessionHelper.setAttribute(
					request,
					SessionHelper.VARIABLE_FILTRE_CONSULTA_TASCA,
					filtreCommand);
		}
		ExpedientTipusDto expedientTipusActual = SessionHelper.getSessionManager(request).getExpedientTipusActual();
		if (expedientTipusActual != null)
			filtreCommand.setExpedientTipusId(expedientTipusActual.getId());
		return filtreCommand;
	}

}
