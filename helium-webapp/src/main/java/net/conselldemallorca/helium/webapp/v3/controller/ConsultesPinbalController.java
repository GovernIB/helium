package net.conselldemallorca.helium.webapp.v3.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
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

import net.conselldemallorca.helium.core.helper.ConsultaPinbalHelper;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPlugin;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalEstatEnum;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ScspRespostaPinbal;
import net.conselldemallorca.helium.v3.core.api.service.ConsultaPinbalService;
import net.conselldemallorca.helium.webapp.v3.command.ConsultesPinbalFiltreCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

@Controller
@RequestMapping("/v3/consultesPinbal")
public class ConsultesPinbalController extends BaseExpedientController {

	private PersonesPlugin personesPlugin;
	@Autowired private ConsultaPinbalService consultesPinbalService;
	@Resource  private ConsultaPinbalHelper consultaPinbalHelper;
	private static final String SESSION_ATTRIBUTE_FILTRE = "ConsultesPinbalController.session.filtre";
	
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		ConsultesPinbalFiltreCommand filtreCommand = getFiltreCommand(request);
		ExpedientTipusDto expedientTipusActual = SessionHelper.getSessionManager(request).getExpedientTipusActual();
		if (expedientTipusActual != null) {
			filtreCommand.setTipusId(expedientTipusActual.getId());
		}
		model.addAttribute(filtreCommand);
		List<ExpedientTipusDto> expedientTipusDtoAccessibles = (List<ExpedientTipusDto>)SessionHelper.getAttribute(
				request,
				SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES);
		modelExpedientsTipus(expedientTipusDtoAccessibles, model);
		modelEstats(model);
		modelPersones(request, model);
		return "v3/consultesPinbalLlistat";
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
				ConversioTipusHelper.convertir(filtreCommand, PeticioPinbalFiltreDto.class));
		return DatatablesHelper.getDatatableResponse(request, null, resultat);
	}
	
	@RequestMapping(value = "/{peticioPinbalId}/actualitzarEstat", method = RequestMethod.GET)
	@ResponseBody
	public ScspRespostaPinbal actualitzarEstat(
			HttpServletRequest request,
			@PathVariable Long peticioPinbalId) {
		return consultaPinbalHelper.tractamentPeticioAsincronaPendentPinbal(peticioPinbalId);
	}
	
	@RequestMapping(value = "/{peticioPinbalId}/info", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request,
			@PathVariable Long peticioPinbalId,
			Model model) {
		model.addAttribute("peticioPinbalDto", consultesPinbalService.findById(peticioPinbalId));
		return "v3/consultesPinbalInfo";
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
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
}
