/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentStoreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ModalHelper;
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
	@Autowired
	private DocumentService documentService;
	
	@RequestMapping(value = "/{expedientId}/notificacio/{notificacioId}/info", method = RequestMethod.GET)
	public String notificacioInfo(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long notificacioId,
			Model model) {		
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		NotificacioDto notificacio = expedientService.findNotificacioPerId(notificacioId, expedient.isArxiuActiu());
		
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
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		NotificacioDto notificacio = expedientService.findNotificacioPerId(notificacioId, expedient.isArxiuActiu());
		
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

		model.addAttribute("expedient", expedient);
		model.addAttribute("notificacions", notificacions);
		modelAddDocumentsNoms(expedient, notificacions, model);
		
		return "v3/expedientNotificacioNotib";
	}

	/** Afegeix al model un Map<documentCodi documentNom> amb els noms dels documents notificats, ja que dels documents notificats només en tenim el codi.
	 * @param expedient 
	 * 
	 * @param notificacions
	 * @param model
	 */
	private void modelAddDocumentsNoms(ExpedientDto expedient, List<DadesNotificacioDto> notificacions, Model model) {
	
		Map<String, String> nomsDocuments = new HashMap<String, String>();
		for (DadesNotificacioDto notificacio : notificacions) {
			if (notificacio.getDocumentsDinsZip() != null) {
				for (DocumentStoreDto ds : notificacio.getDocumentsDinsZip()) {
					if (!ds.isAdjunt() 
							&& ds.getCodiDocument() != null 
							&& !nomsDocuments.containsKey(ds.getCodiDocument())) {
						// Consulta el nom pel codi del document
						DocumentDto document = documentService.findAmbCodi(expedient.getTipus().getId(), null, ds.getCodiDocument(), true);
						nomsDocuments.put(ds.getCodiDocument(), document != null ? document.getNom() : ds.getCodiDocument());
					}
				}
			}
		}
		model.addAttribute("nomsDocuments", nomsDocuments);
		
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
		
		String ret;
		if ( ModalHelper.isRefererUriModal(request)){
			ret = "redirect:" + request.getHeader("referer");
		} else {
			ret = "redirect:/v3/expedient/" + expedientId +"?pipellaActiva=notificacions";
		}
		return ret;
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
