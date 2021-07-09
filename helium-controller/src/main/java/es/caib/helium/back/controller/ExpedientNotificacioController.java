/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.ObjectTypeEditorHelper;
import es.caib.helium.back.view.ArxiuView;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.DadesNotificacioDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.NotificacioDto;
import es.caib.helium.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.service.ExpedientDocumentService;
import es.caib.helium.logic.intf.service.ExpedientService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

	@RequestMapping(value = "/{expedientId}/notificacions/{notificacioId}/consultarEstat", method = RequestMethod.GET)
	public String consultarEstat(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long notificacioId,
			Model model) {		
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		List<DadesNotificacioDto> notificacions = expedientService.findNotificacionsNotibPerExpedientId(expedient.getId());
		DadesNotificacioDto notificacio = null;
		for (DadesNotificacioDto n : notificacions) {
			if (notificacioId.equals(n.getId())) {
				notificacio = n;
				break;
			}
		}
		
		if (notificacio != null) {
			try {
				// Processa el canvi d'estat
				expedientDocumentService.notificacioActualitzarEstat(
						notificacio.getEnviamentIdentificador(), 
						notificacio.getEnviamentReferencia());
				MissatgesHelper.success(request, getMessage(request, "expedient.notificacio.consultar.estat.success"));
			} catch (Exception e) {				
				String errMsg = getMessage(request, "expedient.notificacio.consultar.estat.error", new Object[] {e.getMessage()});
				logger.error(errMsg, e);
				MissatgesHelper.error(request, errMsg);
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "expedient.notificacio.consultar.estat.not.found", new Object[] {notificacioId}));
		}
		return "redirect:/v3/expedient/" + expedientId +"?pipellaActiva=notificacions";
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

	private static final Log logger = LogFactory.getLog(ExpedientNotificacioController.class);
}
