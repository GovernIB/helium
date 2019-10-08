/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientNotificacioController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ExpedientDocumentService expedientDocumentService;
	
	@RequestMapping(value = "/{expedientId}/notificacio/{notificacioId}/info", method = RequestMethod.GET)
	public String notificacioInfo(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long notificacioId,
			Model model) {		
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		NotificacioDto notificacio = expedientService.findNotificacioPerId(notificacioId);
		
		model.addAttribute("expedient",expedient);
		model.addAttribute("notificacio",notificacio);
		
		return "v3/notificacioInfo";
	}
	
	@RequestMapping(value = "/{expedientId}/notificacio/{notificacioId}/error", method = RequestMethod.GET)
	public String notificacioError(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long notificacioId,
			Model model) {		
		expedientService.findAmbIdAmbPermis(expedientId);
		NotificacioDto notificacio = expedientService.findNotificacioPerId(notificacioId);
		
		model.addAttribute("notificacio",notificacio);
		
		return "v3/notificacioError";
	}
	
	@RequestMapping(value = "/{expedientId}/notificacio/{notificacioId}/processar", method = RequestMethod.GET)
	public String notificacioProcessar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long notificacioId,
			Model model) {		
		expedientService.findAmbIdAmbPermis(expedientId);
		expedientService.notificacioReprocessar(notificacioId);
		MissatgesHelper.success(
				request,
				getMessage(
						request,
						"expedient.notificacio.reprocessada"));
		
		model.addAttribute("pipellaActiva", "notificacions");
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value="/{expedientId}/notificacio/{notificacioId}/proces/{processInstanceId}/document/{documentStoreId}/descarregar")
	public String desacarregar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long notificacioId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		try {
			ArxiuDto arxiu = expedientDocumentService.arxiuFindAmbDocument(
					expedientId,
					processInstanceId,
					documentStoreId);
			if (arxiu != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
			}
		} catch (SistemaExternException e) {
			MissatgesHelper.error(request, e.getPublicMessage());
 			modalUrlTancar(true);
		}
		return "arxiuView";
	}
	
	// Notificacions NOTIB
	
	@RequestMapping(value = "/{expedientId}/notificacions", method = RequestMethod.GET)
	public String getNotificacions(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {		
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		List<DadesNotificacioDto> notificacions = expedientService.findNotificacionsNotibPerExpedientId(expedient.getId());
		
		model.addAttribute("expedient",expedient);
		model.addAttribute("notificacions",notificacions);
		
		return "v3/expedientNotificacioNotib";
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				byte[].class,
				new ByteArrayMultipartFileEditor());
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Long.class,
				new CustomNumberEditor(Long.class, true));
		binder.registerCustomEditor(
				Double.class,
				new CustomNumberEditor(Double.class, true));
		binder.registerCustomEditor(
				BigDecimal.class,
				new CustomNumberEditor(
						BigDecimal.class,
						new DecimalFormat("#,##0.00"),
						true));
		binder.registerCustomEditor(
				Boolean.class,
				new CustomBooleanEditor(false));
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}

}
