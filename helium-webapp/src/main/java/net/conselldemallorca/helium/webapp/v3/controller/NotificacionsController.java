package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.helper.NotificacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnviamentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;

import net.conselldemallorca.helium.v3.core.api.service.NotificacioService;

import net.conselldemallorca.helium.webapp.v3.command.NotificacioFiltreCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NtiHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per visualitzar la llista notificacions enviades a NOTIB.
 */
@Controller
@RequestMapping("/v3/notificacionsNotib")
public class NotificacionsController extends BaseExpedientController {
	
	@Autowired
	private NotificacioService notificacioService;

	private static final String SESSION_ATTRIBUTE_FILTRE = "NotificacionsController.session.filtre";
	
	/** Accés al llistat de notificacions des del menú Consultar a la capçalera. */
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		NotificacioFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(filtreCommand);
		this.modelEstats(model);
		this.modelTipusEnviament(model);
		List<ExpedientTipusDto> expedientTipusDtoAccessibles = (List<ExpedientTipusDto>)SessionHelper.getAttribute(
				request,
				SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES);
		this.modelExpedientsTipus(expedientTipusDtoAccessibles, model);
		return "v3/notificacionsNotibLlistat";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {
		NotificacioFiltreCommand filtreCommand = getFiltreCommand(request);
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		List<ExpedientTipusDto> expedientTipusDtoAccessibles = (List<ExpedientTipusDto>)SessionHelper.getAttribute(
				request,
				SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES);		
		PaginaDto<DocumentNotificacioDto> resultat = notificacioService.findAmbFiltrePaginat(
				entornActual.getId(),
				expedientTipusDtoAccessibles,
				ConversioTipusHelper.convertir(filtreCommand, DocumentNotificacioDto.class),
				DatatablesHelper.getPaginacioDtoFromRequest(request));
		return DatatablesHelper.getDatatableResponse(request, null, resultat);
	}
	
	/** Mètode quan s'envia el formulari del filtre. Actualitza el filtre en sessió. */
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid NotificacioFiltreCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return "redirect:notificacionsNotib";
	}
	
	
	/** Posa els expedients tipus al model als quals l'usuari té permís per consultar a l'entorn
	 * @param entornActual */
	private void modelExpedientsTipus(List<ExpedientTipusDto> expedientTipusDtoAccessibles, Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
			for(ExpedientTipusDto expedientTipus : expedientTipusDtoAccessibles)
				if (expedientTipus.isDistribucioActiu())
					opcions.add(new ParellaCodiValorDto(
							expedientTipus.getId().toString(),
							String.format("%s - %s", expedientTipus.getCodi(), expedientTipus.getNom())));		
		
		model.addAttribute("expedientsTipus", opcions);
	}
	
	
	/** Mètode per obtenir o inicialitzar el filtre del formulari de cerca.
	 * @param request
	 * @return
	 */
	private NotificacioFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		NotificacioFiltreCommand filtreCommand = (NotificacioFiltreCommand) SessionHelper.getAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new NotificacioFiltreCommand();
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return filtreCommand;
	}
	
	/** Posa els valors de l'enumeració estats en el model */
	private void modelEstats(Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
		for(NotificacioEstatEnumDto estat : NotificacioEstatEnumDto.values())
			opcions.add(new ParellaCodiValorDto(
					estat.name(),
					MessageHelper.getInstance().getMessage("notificacio.enviament.estat.enum." + estat.name())));		

		model.addAttribute("estats", opcions);
	}
	
	/** Posa els valors de l'enumeració dels tipus d'enviament en el model */
	private void modelTipusEnviament(Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
		for(EnviamentTipusEnumDto estat : EnviamentTipusEnumDto.values())
			opcions.add(new ParellaCodiValorDto(
					estat.name(),
					MessageHelper.getInstance().getMessage("notifica.enviament.tipus.enum." + estat.name())));		

		model.addAttribute("tipusEnviament", opcions);
	}
	
	/** Mètode pel suggest d'expedients inicial
	 * 
	 * @param text
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/suggest/expedient/inici/{expedientId}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public Map<String, String> suggestExpedientInici(
			@PathVariable String expedientId,
			Model model) {
		Map<String, String> resultat = null;
		ExpedientDto expedient = null;
		if (expedientId != null) {
			expedient = expedientService.findAmbId(Long.valueOf(expedientId));
			if (expedient != null) {
				resultat = new HashMap<String, String>();
				resultat.put("codi", String.valueOf(expedient.getId()));
				resultat.put("nom", expedient.getIdentificadorLimitat());
			}
		}
		return resultat;
	}

	/** Mètode per cercar un expedient per número o títol per a un control de tipus suggest
	 * 
	 * @param text
	 * 			Tetxt per filtrar.
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/suggest/expedient/llista/{expedientTipusId}/**", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public List<Map<String, String>> suggestExpedientLlista(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		List<Map<String, String>> resultat = new ArrayList<Map<String,String>>();
		Map<String, String> item;
		List<ExpedientDto> llista = new ArrayList<ExpedientDto>();
		if (expedientTipusId != null) {
			String requestURL = request.getRequestURL().toString();
			String[] text = requestURL.split("/suggest/expedient/llista/" + expedientTipusId + "/");
			if (text.length > 1) {
				llista = expedientService.findPerSuggest(expedientTipusId, text[1]);
			}
		}
		for (ExpedientDto expedientDto: llista) {
			item = new HashMap<String, String>();
			item.put("codi", String.valueOf(expedientDto.getId()));
			item.put("nom", expedientDto.getIdentificadorLimitat());
			resultat.add(item);
		}
		return resultat;

	}
	
	
	
	private static final Log logger = LogFactory.getLog(NotificacionsController.class);
}
