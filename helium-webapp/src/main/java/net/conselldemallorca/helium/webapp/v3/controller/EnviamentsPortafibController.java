package net.conselldemallorca.helium.webapp.v3.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.Authentication;
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

import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPlugin;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultesPortafibFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesEstatEnum;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.service.PortasignaturesService;
import net.conselldemallorca.helium.webapp.v3.command.ConsultesPortafibFiltreCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per visualitzar la llista de peticions enviades des d'Helium al PortaFib.
 */
@Controller
@RequestMapping("/v3/enviamentsPortafib")
public class EnviamentsPortafibController extends BaseExpedientController {
	
	private PersonesPlugin personesPlugin;
	@Autowired private PortasignaturesService portasignaturesService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		
		ConsultesPortafibFiltreCommand filtreCommand = getFiltreCommand(request);
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
	
			if (!SessionHelper.getSessionManager(request).getPotAdministrarEntorn()) {
				MissatgesHelper.error(request, "No teniu permís d'administració sobre l'entorn actual.");
				return "redirect:/";
			}
	
			if (expedientTipusDtoAccessibles==null || expedientTipusDtoAccessibles.size()==0) {
				MissatgesHelper.error(request, "No teniu permís d'administració sobre cap tipus d'expedient dins l'entorn actual.");
				return "redirect:/";
			}
		}
		
		model.addAttribute(filtreCommand);
		modelExpedientsTipus(expedientTipusDtoAccessibles, model);
		modelEstats(model);
		return "v3/consultesPortafibLlistat";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {
		ConsultesPortafibFiltreCommand filtreCommand = getFiltreCommand(request);
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		filtreCommand.setEntornId(entornActual.getId());
		PaginaDto<PortasignaturesDto> resultat = portasignaturesService.findAmbFiltrePaginat(
				paginacioParams,
				ConversioTipusHelper.convertir(filtreCommand, ConsultesPortafibFiltreDto.class));
		return DatatablesHelper.getDatatableResponse(request, null, resultat);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ConsultesPortafibFiltreCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return "redirect:enviamentsPortafib";
	}
	
	@RequestMapping(value = "/{peticioPortafibId}/info", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request,
			@PathVariable Long peticioPortafibId,
			Model model) {
		model.addAttribute("dto", portasignaturesService.findById(peticioPortafibId));
		return "v3/consultesPortafibInfo";
	}
	
	private ConsultesPortafibFiltreCommand getFiltreCommand(HttpServletRequest request) {
		ConsultesPortafibFiltreCommand filtreCommand = (ConsultesPortafibFiltreCommand) SessionHelper.getAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ConsultesPortafibFiltreCommand();
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return filtreCommand;
	}
	
	private void modelExpedientsTipus(List<ExpedientTipusDto> expedientTipusDtoAccessibles, Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
			for(ExpedientTipusDto expedientTipus : expedientTipusDtoAccessibles)
				opcions.add(new ParellaCodiValorDto(expedientTipus.getId().toString(), expedientTipus.getNom()));		
		
		model.addAttribute("expedientsTipus", opcions);
	}
	
	private void modelEstats(Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
		for(PortafirmesEstatEnum estat : PortafirmesEstatEnum.values())
			if (!estat.equals(PortafirmesEstatEnum.BLOQUEJAT))
				opcions.add(new ParellaCodiValorDto(
						estat.name(),
						MessageHelper.getInstance().getMessage("enum.portafirmes.estat." + estat.name())));

		model.addAttribute("estats", opcions);
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}	
	
	private void modelPersones(HttpServletRequest request, Model model) {
		
		try {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.persones.plugin.class");
			if (pluginClass != null) {
				personesPlugin = (PersonesPlugin)(Class.forName(pluginClass).newInstance());
				model.addAttribute("persones", personesPlugin.findAll());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final Log logger = LogFactory.getLog(EnviamentsPortafibController.class);
	private static final String SESSION_ATTRIBUTE_FILTRE = "EnviamentsPortafibController.session.filtre";
}
