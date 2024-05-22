package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioAccioEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioListDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.service.AnotacioService;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.v3.core.api.service.NotificacioService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.command.AnotacioAcceptarCommand;
import net.conselldemallorca.helium.webapp.v3.command.AnotacioAcceptarCommand.CrearIncorporar;
import net.conselldemallorca.helium.webapp.v3.command.AnotacioFiltreCommand;
import net.conselldemallorca.helium.webapp.v3.command.AnotacioRebutjarCommand;
import net.conselldemallorca.helium.webapp.v3.command.NotificacioFiltreCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.EnumHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;

/**
 * Controlador per visualitzar la llista de peticions d'anotacions que han arribat a Helium
 * com a Backoffice de Distribució amb la possibilitat d'acceptar o rebutjar anotacions.
 *
 */
@Controller
@RequestMapping("/v3/notificacions")
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
		List<ExpedientTipusDto> expedientTipusDtoAccessiblesAnotacions = (List<ExpedientTipusDto>)SessionHelper.getAttribute(
				request,
				SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES);
		this.modelExpedientsTipus(expedientTipusDtoAccessiblesAnotacions, model);
		return "v3/notificacioLlistat";
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
		return "redirect:notificacions";
	}
	
	@RequestMapping(value="/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		NotificacioFiltreCommand filtreCommand = getFiltreCommand(request);
		List<ExpedientTipusDto> expedientTipusDtoAccessibles = (List<ExpedientTipusDto>)SessionHelper.getAttribute(
													request,
													SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES);
		return DatatablesHelper.getDatatableResponse(
					request,
					null,
					notificacioService.findAmbFiltrePaginat(
							entornActual.getId(),
							expedientTipusDtoAccessibles,
							ConversioTipusHelper.convertir(filtreCommand, NotificacioFiltreDto.class),
							DatatablesHelper.getPaginacioDtoFromRequest(request)),
					"id");		
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
			filtreCommand.setEstat(NotificacioEstatEnumDto.ENVIADA);
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
					MessageHelper.getInstance().getMessage("enum.notificacio.estat." + estat.name())));		

		model.addAttribute("estats", opcions);
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
