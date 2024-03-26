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
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.service.AnotacioService;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.command.AnotacioAcceptarCommand;
import net.conselldemallorca.helium.webapp.v3.command.AnotacioAcceptarCommand.CrearIncorporar;
import net.conselldemallorca.helium.webapp.v3.command.AnotacioFiltreCommand;
import net.conselldemallorca.helium.webapp.v3.command.AnotacioRebutjarCommand;
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
@RequestMapping("/v3/anotacio")
public class AnotacioController extends BaseExpedientController {
	
	@Autowired
	private AnotacioService anotacioService;
	
	private @Autowired ExpedientIniciController expedientIniciController;
	
	@Autowired
	private ExecucioMassivaService execucioMassivaService;

	@Autowired
	private ExpedientDocumentService expedientDocumentService;
	
	@Autowired
	private ExpedientTipusService expedientTipusService;

	private static final String SESSION_ATTRIBUTE_FILTRE = "AnotacioController.session.filtre";
	
	/** Accés al llistat d'anotacions des de l'opció a la capçalera per ususaris amb permís de relacionar expedients. */
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		AnotacioFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(filtreCommand);
		this.modelEstats(model);
		List<ExpedientTipusDto> expedientTipusDtoAccessiblesAnotacions = (List<ExpedientTipusDto>)SessionHelper.getAttribute(
				request,
				SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES_ANOTACIONS);
		this.modelExpedientsTipus(expedientTipusDtoAccessiblesAnotacions, model);
		model.addAttribute("maxConsultaIntents", this.getMaxConsultaIntents());
		return "v3/anotacioLlistat";
	}
	
	/** Mètode quan s'envia el formulari del filtre. Actualitza el filtre en sessió. */
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid AnotacioFiltreCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return "redirect:anotacio";
	}
	
	@RequestMapping(value="/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		AnotacioFiltreCommand filtreCommand = getFiltreCommand(request);
		List<ExpedientTipusDto> expedientTipusDtoAccessibles = (List<ExpedientTipusDto>)SessionHelper.getAttribute(
													request,
													SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES_ANOTACIONS);
		return DatatablesHelper.getDatatableResponse(
					request,
					null,
					anotacioService.findAmbFiltrePaginat(
							entornActual.getId(),
							expedientTipusDtoAccessibles,
							ConversioTipusHelper.convertir(filtreCommand, AnotacioFiltreDto.class),
							DatatablesHelper.getPaginacioDtoFromRequest(request)),
					"id");		
	}	
	
	/** Mètode per obtenir o inicialitzar el filtre del formulari de cerca.
	 * 
	 * @param request
	 * @return
	 */
	private AnotacioFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		AnotacioFiltreCommand filtreCommand = (AnotacioFiltreCommand) SessionHelper.getAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new AnotacioFiltreCommand();
			filtreCommand.setEstat(AnotacioEstatEnumDto.PENDENT);
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return filtreCommand;
	}
	
	
	@RequestMapping(value = "/seleccioTots", method = RequestMethod.GET)
	@ResponseBody
	public String seleccioTots(
			HttpServletRequest request,
			@RequestParam(value = "ids[]", required = false) Long[] ids,
			@RequestParam(value = "method", required = false) String method,
			Model model) {
		
		List<Long> llistaIds = selectionTipus(request, "all", null, "all", model);
		return String.valueOf(llistaIds.size());
	}

	@RequestMapping(value = "/seleccioNetejar", method = RequestMethod.GET)
	@ResponseBody
	public String seleccioNetejar(
			HttpServletRequest request,
			@RequestParam(value = "ids[]", required = false) Long[] ids,
			@RequestParam(value = "method", required = false) String method,
			Model model) {
		List<Long> llistaIds = selectionTipus(request, "clear", null, "clear", model);
		return String.valueOf(llistaIds.size());
	}
	
	@RequestMapping(value = "/selection", method = RequestMethod.POST)
	@ResponseBody
	public List<Long> selection(
			HttpServletRequest request,
			@RequestParam(value = "ids[]", required = false) Long[] ids,
			@RequestParam(value = "method", required = false) String method,
			Model model) {
		return selectionTipus(request, "selection", ids, method, model);
	}

	@RequestMapping(value = "/selection/{tipus}", method = RequestMethod.POST)
	@ResponseBody
	public List<Long> selectionTipus(
			HttpServletRequest request,
			@PathVariable String tipus,
			@RequestParam(value = "ids[]", required = false) Long[] ids,
			@RequestParam(value = "method", required = false) String method,
			Model model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		SessionHelper.SessionManager sessionManager = SessionHelper.getSessionManager(request);
		List<Long> seleccio = sessionManager.getSeleccioAnotacio();
		if (seleccio == null) {
			seleccio = new ArrayList<Long>();
			sessionManager.setSeleccioAnotacio(seleccio);
		}

		if ("selection".equalsIgnoreCase(tipus)) {
			if ("add".equalsIgnoreCase(method) && ids != null) {
				for (Long idu: ids) {
					if (idu != null && !seleccio.contains(idu)) {
						seleccio.add(idu);
					}
				}
			} else if ("remove".equalsIgnoreCase(method) && ids != null) {
				for (Long idu: ids) {
					if (seleccio.contains(idu)) {
						seleccio.remove(idu);
					}
				}
			}
		} else if ("clear".equalsIgnoreCase(tipus)) {
			seleccio.clear();
		} else if ("all".equalsIgnoreCase(tipus)) {
			seleccio.clear();
			List<ExpedientTipusDto> expedientTipusDtoAccessibles = (List<ExpedientTipusDto>)SessionHelper.getAttribute(
					request,
					SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES_ANOTACIONS);
			seleccio.addAll(anotacioService.findIdsAmbFiltre(
					entornActual.getId(), 
					expedientTipusDtoAccessibles,
					ConversioTipusHelper.convertir(this.getFiltreCommand(request), AnotacioFiltreDto.class),
					DatatablesHelper.getPaginacioDtoFromRequest(request)));
		}

		sessionManager.setSeleccioAnotacio(seleccio);
		return seleccio;
	}	
	
	/** Mètode per veure el detall d'una anotació provinent de Distribució
	 * 
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String detall(
			HttpServletRequest request, 
			@PathVariable Long id,
			@RequestParam(value = "annexId", required = false) Long annexId,
			Model model) {
		AnotacioDto anotacio = anotacioService.findAmbId(id);
		model.addAttribute("anotacio", anotacio);
		model.addAttribute("annexId", annexId);
		model.addAttribute("maxConsultaIntents", this.getMaxConsultaIntents());
		Map<Long, List<ExpedientDocumentDto>> documents = new HashMap<Long, List<ExpedientDocumentDto>>();
		if (anotacio != null && anotacio.getExpedient() != null) {
			if (anotacio.getExpedient() != null) {
				for (ExpedientDocumentDto d : expedientDocumentService.findAmbInstanciaProces(
						anotacio.getExpedient().getId(),
						anotacio.getExpedient().getProcessInstanceId())) {
					if (d.getAnotacioId() != null && d.getAnotacioId().equals(id)) {
						if (!documents.containsKey(d.getAnotacioAnnexId())) {
							documents.put(d.getAnotacioAnnexId(), new ArrayList<ExpedientDocumentDto>());
						}
						documents.get(d.getAnotacioAnnexId()).add(d);
					}
				}
			}			
		}		
		model.addAttribute("documents", documents);
		return "v3/anotacioDetall";
	}
	
	/** Mètode per obrir el formulari per acceptar la petició d'anotació i processar-la
	 * 
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{anotacioId}/acceptar", method = RequestMethod.GET)
	public String acceptar(
			HttpServletRequest request, 
			@PathVariable Long anotacioId,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		AnotacioDto anotacio = anotacioService.findAmbId(anotacioId);
		Long expedientTipusId = anotacio.getExpedientTipus() != null ? 
											anotacio.getExpedientTipus().getId() 
											: null;
		Long expedientId = expedientTipusId != null && anotacio.getExpedient() != null ? 
											anotacio.getExpedient().getId() 
											: null;
		AnotacioAcceptarCommand anotacioAcceptarCommand = new AnotacioAcceptarCommand();
		anotacioAcceptarCommand.setEntornId(EntornActual.getEntornId());
		anotacioAcceptarCommand.setId(anotacioId);
		anotacioAcceptarCommand.setExpedientTipusId(expedientTipusId);
		anotacioAcceptarCommand.setExpedientId(expedientId);
		anotacioAcceptarCommand.setAny(Calendar.getInstance().get(Calendar.YEAR));
		// Determina l'acció per defecte depenent de les dades de l'anotació
		if (expedientId != null)
			anotacioAcceptarCommand.setAccio(AnotacioAccioEnumDto.INCORPORAR);
		else if (expedientTipusId != null) {
			anotacioAcceptarCommand.setAccio(AnotacioAccioEnumDto.CREAR);
			anotacioAcceptarCommand.setNumero(expedientService.getNumeroExpedientActual(EntornActual.getEntornId(), expedientTipusId, anotacioAcceptarCommand.getAny()));
			if(anotacio.getExpedientTipus()!=null && anotacio.getExpedientTipus().isProcedimentComu()) {
				anotacioAcceptarCommand.setUnitatOrganitzativaCodi(anotacio.getDestiCodi());
			}
		}
		anotacioAcceptarCommand.setAssociarInteressats(true);
		model.addAttribute("anotacio", anotacio);
		model.addAttribute("anotacioAcceptarCommand", anotacioAcceptarCommand);
		
		this.modelAccions(model);
		List<ExpedientTipusDto> expedientTipusDtoAccessiblesAnotacions = (List<ExpedientTipusDto>)SessionHelper.getAttribute(
				request,
				SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES_ANOTACIONS);
		this.modelExpedientsTipus(expedientTipusDtoAccessiblesAnotacions, model);
		
		return "v3/anotacioAcceptar";
	}
	
	/** Mètode per tractar la petició post d'acceptar una petició d'anotació de registre de Distribucio.
	 * 
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{anotacioId}/acceptar", method = RequestMethod.POST)
	public String acceptarPost(
			HttpServletRequest request, 
			HttpServletResponse response, 
			@PathVariable Long anotacioId,
			@Validated(CrearIncorporar.class) AnotacioAcceptarCommand command,
			BindingResult bindingResult,
			Model model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		AnotacioDto anotacio = anotacioService.findAmbId(anotacioId);
		if (bindingResult.hasErrors()) {
			model.addAttribute("anotacio", anotacio);			
			this.modelAccions(model);
			List<ExpedientTipusDto> expedientTipusDtoAccessiblesAnotacions = (List<ExpedientTipusDto>)SessionHelper.getAttribute(
					request,
					SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES_ANOTACIONS);
			this.modelExpedientsTipus(expedientTipusDtoAccessiblesAnotacions, model);
			
			return "v3/anotacioAcceptar";
		}
		String ret = null;
		try {
			ExpedientTipusDto expedientTipusDto = null;
			
			if(command.getExpedientTipusId()!=null)
				expedientTipusDto = expedientTipusService.findAmbId((command.getExpedientTipusId())); 
			if(expedientTipusDto!=null && expedientTipusDto.isProcedimentComu()) {
				command.setUnitatOrganitzativaCodi(anotacio.getDestiCodi());
			}
			switch(command.getAccio()) {
			case GUARDAR:
				// Guarda le opcions de tipus d'expedient i expedient
				anotacioService.updateExpedient(
						command.getId(),
						command.getExpedientTipusId(),
						command.getExpedientId());
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"anotacio.form.acceptar.guardar.success",
								new Object[] {
										command.getExpedientId(),
										anotacio.getIdentificador()}));
				ret = this.modalUrlTancar(false); 
				break;
			case CREAR:
				// Afegeix la informació de l'anotació a la sessió i redirigeix cap al formulari de creació
				request.getSession().setAttribute(ExpedientIniciController.CLAU_SESSIO_UNITAT_ORGANITZATIVA_CODI, command.getUnitatOrganitzativaCodi());
				request.getSession().setAttribute(ExpedientIniciController.CLAU_SESSIO_ANOTACIO, command);
				request.getSession().setAttribute(ExpedientIniciController.CLAU_SESSIO_NUMERO, command.getNumero());
				request.getSession().setAttribute(ExpedientIniciController.CLAU_SESSIO_TITOL, command.getTitol());
				request.getSession().setAttribute(ExpedientIniciController.CLAU_SESSIO_ANY, command.getAny());

				ret = expedientIniciController.iniciarPost(
						request, 
						command.getExpedientTipusId(), 
						null, 
						command.getId(), 
						model);
				break;
			case INCORPORAR:
				// Incorpora la informació de l'anotació a l'expedient
				anotacio = anotacioService.incorporarReprocessarExpedient(
						command.getId(),
						command.getExpedientTipusId(),
						command.getExpedientId(),
						command.isAssociarInteressats(),
						true,
						true);
			
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"anotacio.form.acceptar.incorporar.success",
								new Object[] {
										anotacio.getIdentificador(),
										anotacio.getExpedient().getIdentificadorLimitat()}));
				// Comprova si hi ha cap annex amb error per advertir a l'usuari
				if (anotacio.isErrorAnnexos())
					MissatgesHelper.warning(
							request, 
							getMessage(	request, 
										"anotacio.form.acceptar.incorporar.errorAnnexos",
										new Object[] { anotacio.getIdentificador(), anotacio.getExpedient().getIdentificadorLimitat()}));
				ret = this.modalUrlTancar(false); 
				break;		
			default:
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"anotacio.form.acceptar.noaccio",
								new Object[] {
										command.getExpedientId(),
										anotacio.getIdentificador()}));
				ret = this.modalUrlTancar(false); 
			}
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"anotacio.form.acceptar.error",
							new Object[] {command.getAccio(), e.getMessage()}));
			ret = this.modalUrlTancar(false);
		}
		return ret;
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
		
	/** Mètode per obrir el formulari per rebutjar la petició d'anotació de registre de Distribució.
	 * 
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{anotacioId}/rebutjar", method = RequestMethod.GET)
	public String rebutjar(
			HttpServletRequest request, 
			@PathVariable Long anotacioId,
			Model model) {
		
		AnotacioDto anotacio = anotacioService.findAmbId(anotacioId);
		AnotacioRebutjarCommand anotacioRebutjarCommand = new AnotacioRebutjarCommand();
		anotacioRebutjarCommand.setAnotacioId(anotacioId);

		model.addAttribute("anotacio", anotacio);
		model.addAttribute("anotacioRebutjarCommand", anotacioRebutjarCommand);
				
		return "v3/anotacioRebutjar";
	}
	
	/** Mètode per tractar la petició de rebuig d'una petició d'anotació de registre.

	 * 
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{anotacioId}/rebutjar", method = RequestMethod.POST)
	public String rebutjarPost(
			HttpServletRequest request, 
			@PathVariable Long anotacioId,
			@Valid AnotacioRebutjarCommand command,
			BindingResult bindingResult,
			Model model) {
		
		AnotacioDto anotacio = anotacioService.findAmbId(anotacioId);
		if (bindingResult.hasErrors()) {
			model.addAttribute("anotacio", anotacio);			
			return "v3/anotacioRebutjar";
		}		
		String ret;
		try {
			anotacioService.rebutjar(
					anotacioId, 
					command.getObservacions());
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"anotacio.form.rebutjar.accio.rebutjar.success"));
			ret = this.modalUrlTancar(false);
		} catch(Exception e) {
			model.addAttribute("anotacio", anotacio);
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"anotacio.form.rebutjar.accio.rebutjar.error",
							new Object[] {e.getMessage()}));
			ret = "v3/anotacioRebutjar";
		}
		return ret; 
	}
	
	/** Mètode per esborrar una petició d'anotació pendent. Comprova que estigui pendent.
	 * 
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		try {
			AnotacioDto anotacio = anotacioService.findAmbId(id);
			anotacioService.delete(id);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"anotacio.llistat.accio.esborrar.success",
							new Object[] {anotacio.getIdentificador()}));
		} catch (Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"anotacio.llistat.accio.esborrar.error",
							new Object[] {id, e.getMessage()}));
		}
		return "redirect:/v3/anotacio";
	}
	
	/** Mètode per tornar a reprocessar anotacions en estat d'error de processament.
	 * 
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{id}/reprocessar", method = RequestMethod.GET)
	public String reprocessar(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		try {
			AnotacioDto anotacio = anotacioService.reprocessar(id);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"anotacio.llistat.accio.reprocessar.success",
							new Object[] {anotacio.getIdentificador()}));
		} catch (Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"anotacio.llistat.accio.reprocessar.error",
							new Object[] {id, e.getMessage()}));
		}
		return "redirect:/v3/anotacio";
	}
	
	/** Mètode per marcar com a pendent una anotació en estat de processament error.
	 * 
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{id}/marcarPendent", method = RequestMethod.GET)
	public String marcarPendent(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		try {
			AnotacioDto anotacio = anotacioService.marcarPendent(id);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"anotacio.llistat.accio.marcarPendent.success",
							new Object[] {anotacio.getIdentificador()}));
		} catch (Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"anotacio.llistat.accio.marcarPendent.error",
							new Object[] {id, e.getMessage()}));
		}
		return "redirect:/v3/anotacio";
	}

	/** Mètode per fixar el número d'intents a 0 i que es torni a consultar a Distribucio.
	 * 
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{id}/reintentarConsulta", method = RequestMethod.GET)
	public String reinentarConsulta(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		try {
			AnotacioDto anotacio = anotacioService.reintentarConsulta(id);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"anotacio.llistat.accio.reintentarConsulta.success",
							new Object[] {anotacio.getIdentificador()}));
		} catch (Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"anotacio.llistat.accio.reintentarConsulta.error",
							new Object[] {id, e.getMessage()}));
		}
		return "redirect:/v3/anotacio";
	}
	
	@RequestMapping(value = "/{anotacioId}/annex/{annexId}/descarregar/original", method = RequestMethod.GET)
	public String descarregarAnnexOriginal(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long anotacioId,
			@PathVariable Long annexId,
			Model model) throws IOException {
		boolean success = false;
		String error = null;
		Exception ex = null;
		try {
			ArxiuDto arxiu = anotacioService.getAnnexContingutVersioOriginal(annexId);
			if (arxiu != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
			}
			success = true;
		} catch (SistemaExternException e) {
			error = e.getPublicMessage();	
			ex = e;
		} catch (Exception e) {
			error = e.getMessage();
			ex = e;
		}
		if (success)
			return "arxiuView";
		else {
			String errMsg = getMessage(
					request, 
					"anotacio.annex.descarregar.error",
					new Object[] {error});
			logger.error(errMsg, ex);
			MissatgesHelper.error(
					request, 
					errMsg);
			return "redirect:" + request.getHeader("referer");
		}
	}
	
	@RequestMapping(value = "/{anotacioId}/annex/{annexId}/descarregar/imprimible", method = RequestMethod.GET)
	public String descarregarAnnexImprimible(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long anotacioId,
			@PathVariable Long annexId,
			Model model) throws IOException {
		boolean success = false;
		String error = null;
		Exception ex = null;
		try {
			ArxiuDto arxiu = anotacioService.getAnnexContingutVersioImprimible(annexId);
			if (arxiu != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
			}
			success = true;
		} catch (SistemaExternException e) {
			error = e.getPublicMessage();	
			ex = e;
		} catch (Exception e) {
			error = e.getMessage();
			ex = e;
		}
		if (success)
			return "arxiuView";
		else {
			String errMsg = getMessage(
					request, 
					"anotacio.annex.descarregar.error",
					new Object[] {error});
			logger.error(errMsg, ex);
			MissatgesHelper.error(
					request, 
					errMsg);
			return "redirect:" + request.getHeader("referer");
		}
	}
	
	@RequestMapping(value = "/{anotacioId}/annex/{annexId}/firmaInfo", method = RequestMethod.GET)
	public String firmaInfo(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long anotacioId,
			@PathVariable Long annexId,
			Model model) throws IOException {
		try {
			List<ArxiuFirmaDto> firmes = anotacioService.getAnnexFirmes(annexId); 
			
			model.addAttribute("annexId",annexId);
			model.addAttribute("firmes", firmes);
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Error obtenint les firmes: " + e.getMessage());
		}
		return "v3/anotacioAnnexFirmes";
	}

	/** Mètode per reintentar el processament de l'annex per guardar-lo a Helium dins l'arxiu o la BBDD 
	 * des de la taula d'annexos de l'expedient. */
	@RequestMapping(value = "/{anotacioId}/annex/{annexId}/reintentar", method = RequestMethod.GET)
	public String reintentarAnnex(
			HttpServletRequest request,
			@PathVariable Long anotacioId,
			@PathVariable Long annexId) {
		try {
			this.anotacioService.reintentarAnnex(anotacioId, annexId);
			MissatgesHelper.success(
					request, 
					getMessage(request, "anotacio.annex.reintentar.success"));
		} catch (Exception e) {
			String errMsg = getMessage(request, "anotacio.annex.reintentar.error", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(
					request, 
					errMsg);
		}
		return "redirect:/modal/v3/anotacio/" + anotacioId;
	}	
	

	/** Posa els valors de l'enumeració estats en el model */
	private void modelEstats(Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
		for(AnotacioEstatEnumDto estat : AnotacioEstatEnumDto.values())
			opcions.add(new ParellaCodiValorDto(
					estat.name(),
					MessageHelper.getInstance().getMessage("enum.anotacio.estat." + estat.name())));		

		model.addAttribute("estats", opcions);
	}

	/** Posa els valors de l'enumeració accions en el model */
	private void modelAccions(Model model) {
		model.addAttribute(
				"accions",
				EnumHelper.getOptionsForEnum(
						AnotacioAccioEnumDto.class,
						"enum.anotacio.accio."));
		model.addAttribute(
				"anysSeleccionables", 
				BaseExpedientIniciController.getAnysSeleccionables());
	}

	/** Posa els expedients tipus al model als quals l'usuari té permís per consultar a l'entorn i estan configurats amb integració amb Distribucio
	 * @param entornActual */
	private void modelExpedientsTipus(List<ExpedientTipusDto> expedientTipusDtoAccessiblesAnotacions, Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
			for(ExpedientTipusDto expedientTipus : expedientTipusDtoAccessiblesAnotacions)
				if (expedientTipus.isDistribucioActiu())
					opcions.add(new ParellaCodiValorDto(
							expedientTipus.getId().toString(),
							String.format("%s - %s", expedientTipus.getCodi(), expedientTipus.getNom())));		
		
		model.addAttribute("expedientsTipus", opcions);
	}
	
	
	private String getMaxConsultaIntents() {
		String maxConsultaIntents = GlobalProperties.getInstance().getProperty("app.anotacions.pendents.comprovar.intents", "5");
		if (maxConsultaIntents == null || "".equals(maxConsultaIntents.trim())) {
			maxConsultaIntents = "5";
		}
		return maxConsultaIntents;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	    
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
				new CustomBooleanEditor(true));
	}
	
	@RequestMapping(value = "/excel", method = RequestMethod.GET)
	public void excel(
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session,
			Model model) 
					throws IllegalAccessException, InvocationTargetException, NoSuchMethodException  {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		AnotacioFiltreCommand filtreCommand = getFiltreCommand(request);
		
		List<AnotacioListDto> anotacions = this.anotacionsList(entornActual, filtreCommand, request);

		generarExcel(
				request,
				response,
				anotacions);
	}
	
	private List<AnotacioListDto> anotacionsList (EntornDto entornActual, AnotacioFiltreCommand filtreCommand, HttpServletRequest request){
		List<AnotacioListDto> anotacions = new ArrayList<AnotacioListDto>();
		int nPagina = 0;
		int grandariaPagina = 100;
		
		PaginaDto<AnotacioListDto> paginaDto;
		PaginacioParamsDto paginacio = new PaginacioParamsDto();
		paginacio.setPaginaTamany(grandariaPagina);
		paginacio.afegirOrdre(
				"data",
				OrdreDireccioDto.DESCENDENT);
		
		do {
			paginacio.setPaginaNum(nPagina++);
			List<ExpedientTipusDto> expedientTipusDtoAccessiblesAnotacions = (List<ExpedientTipusDto>)SessionHelper.getAttribute(
					request,
					SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES_ANOTACIONS);
			paginaDto = anotacioService.findAmbFiltrePaginat(
					entornActual.getId(),
					expedientTipusDtoAccessiblesAnotacions,
					ConversioTipusHelper.convertir(filtreCommand, AnotacioFiltreDto.class),
					paginacio);	
			anotacions.addAll(paginaDto.getContingut());			
		} while(!paginaDto.isDarrera());
		return anotacions;
	}
	
	private void generarExcel(
			HttpServletRequest request,
			HttpServletResponse response,
			List<AnotacioListDto> anotacions) {
	
		
		HSSFWorkbook wb;
		HSSFCellStyle cellStyle;
		HSSFCellStyle dStyle;
		HSSFFont bold;
		HSSFCellStyle cellGreyStyle;
		HSSFCellStyle greyStyle;
		HSSFCellStyle dGreyStyle;
		HSSFFont greyFont;
		wb = new HSSFWorkbook();
	
		bold = wb.createFont();
		bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		bold.setColor(HSSFColor.WHITE.index);
		
		greyFont = wb.createFont();
		greyFont.setColor(HSSFColor.GREY_25_PERCENT.index);
		greyFont.setCharSet(HSSFFont.ANSI_CHARSET);
		
		cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
		cellStyle.setWrapText(true);
		
		cellGreyStyle = wb.createCellStyle();
		cellGreyStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
		cellGreyStyle.setWrapText(true);
		cellGreyStyle.setFont(greyFont);
		
		greyStyle = wb.createCellStyle();
		greyStyle.setFont(greyFont);
	
		DataFormat format = wb.createDataFormat();
		dStyle = wb.createCellStyle();
		dStyle.setDataFormat(format.getFormat("0.00"));
	
		dGreyStyle = wb.createCellStyle();
		dGreyStyle.setFont(greyFont);
		dGreyStyle.setDataFormat(format.getFormat("0.00"));
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		if (!anotacions.isEmpty())
			createHeader(
					wb,
					sheet,
					anotacions,
					request);
		int rowNum = 1;
		int colNum = 0;
		for (AnotacioListDto  anotacioDto : anotacions) {
			try {
				HSSFRow xlsRow = sheet.createRow(rowNum++);
				colNum = 0;	
				HSSFCell cell = xlsRow.createCell(colNum);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(anotacioDto.getData());
				cell.setCellStyle(cellStyle);//format de data
						
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(anotacioDto.getIdentificador());//Número de registre
				cell.setCellStyle(dStyle);
						
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(anotacioDto.getExtracte());
				cell.setCellStyle(dStyle);
						
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(anotacioDto.getProcedimentCodi());
				cell.setCellStyle(dStyle);
						
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(anotacioDto.getExpedientNumero());
				cell.setCellStyle(dStyle);
						
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(anotacioDto.getDataRecepcio());
				cell.setCellStyle(cellStyle);//format de data
						
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(anotacioDto.getExpedientTipus() != null ? anotacioDto.getExpedientTipus().getCodi() : "");
				cell.setCellStyle(dStyle);
					
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(anotacioDto.getExpedient() != null ? anotacioDto.getExpedient().getNumeroIdentificador() : "");
				cell.setCellStyle(dStyle);
								
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(anotacioDto.getEstat().name());
				cell.setCellStyle(dStyle);
			} catch (Exception e) {
				logger.error("Export Excel: No s'ha pogut crear la línia: " + rowNum + " - amb ID: " + anotacioDto.getExpedient().getId(), e);
			}
	
		}
		for(int i=0; i<colNum; i++)
			sheet.autoSizeColumn(i);

		try {
			String fileName = "Anotacions.xls";
			response.setHeader("Pragma", "");
			response.setHeader("Expires", "");
			response.setHeader("Cache-Control", "");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
			wb.write( response.getOutputStream() );
		} catch (Exception e) {
			logger.error("Anotacions: No s'ha pogut realitzar la exportació.");
		}
	}

	
	private void createHeader(
			HSSFWorkbook wb,
			HSSFSheet sheet,
			List<AnotacioListDto> anotacions,
			HttpServletRequest request) {
		HSSFFont bold;
		bold = wb.createFont();
		bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		bold.setColor(HSSFColor.WHITE.index);
		HSSFCellStyle headerStyle;
		headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
		headerStyle.setFillBackgroundColor(HSSFColor.GREY_80_PERCENT.index);
		headerStyle.setFont(bold);
		int rowNum = 0;
		int colNum = 0;
		// Capçalera
		HSSFRow xlsRow = sheet.createRow(rowNum++);
		HSSFCell cell;
		cell = xlsRow.createCell(colNum);
		cell.setCellStyle(headerStyle);
		sheet.autoSizeColumn(colNum);
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage(
						request, 
						"anotacio.llistat.columna.data"))));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage(
					request, 
					"anotacio.llistat.columna.identificador"))));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage(
					request, 
					"anotacio.llistat.columna.extracte"))));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage(
					request, 
					"anotacio.llistat.columna.procedimentCodi"))));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage(
					request, 
					"anotacio.llistat.columna.expedientNumero"))));
		cell.setCellStyle(headerStyle);
			
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage(
					request, 
					"anotacio.llistat.columna.dataRecepcio"))));
		cell.setCellStyle(headerStyle);
			
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage(
					request, 
					"anotacio.llistat.columna.expedientTipus"))));
		cell.setCellStyle(headerStyle);
			
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage(
					request, 
					"anotacio.llistat.columna.expedient"))));
		cell.setCellStyle(headerStyle);
			
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage(
					request, 
					"anotacio.llistat.columna.estat"))));
		cell.setCellStyle(headerStyle);
	}
	
	
	
	/** Acció del menú desplegable d'Accions massives d'anotacions, per iniciar una tasca en segon pla per Reintentar consulta  de les
	 * anotacions seleccionades a le taula d'anotacions  (les que estan en consulta error es tornarien a consultar i potser processar)
	 */
	@RequestMapping(value = "/reintentarConsulta", method = RequestMethod.GET)
	public String reintentarConsulta(
			HttpServletRequest request,
			Model model) {
		// Programa la execució massiva
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		ExecucioMassivaDto dto = new ExecucioMassivaDto();
		dto.setTipus(ExecucioMassivaTipusDto.REINTENTAR_CONSULTA_ANOTACIONS);
		dto.setEnviarCorreu(false);
		List<Long> ids =  sessionManager.getSeleccioAnotacio();
		if (ids == null || ids.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.anotacio.selec"));
		} else {
			dto.setAuxIds(ids);
			try {
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.success(
						request,
						getMessage(
								request,
								"anotacio.llistat.accio.massiva.info.reintentar.consulta.success"));
			} catch(Exception e) {
				MissatgesHelper.error(
						request,
						getMessage(
								request,
								"anotacio.llistat.accio.massiva.info.reintentar.consulta.error",
								new Object[] {e.getMessage()}));
			}					
			// Neteja la selecció
			sessionManager.getSeleccioAnotacio().clear();
		}
		return "redirect:/v3/anotacio";
	}
	
	/** Acció del menú desplegable d'Accions massives d'anotacions, per iniciar una tasca en segon pla per esborrar  les
	 * anotacions seleccionades a le taula d'anotacions
	 */
	@RequestMapping(value = "/esborrarAnotacions", method = RequestMethod.GET)
	public String esborrarAnotacions(
			HttpServletRequest request,
			Model model) {
		// Programa la execució massiva
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		ExecucioMassivaDto dto = new ExecucioMassivaDto();
		dto.setTipus(ExecucioMassivaTipusDto.ESBORRAR_ANOTACIONS);
		dto.setEnviarCorreu(false);
		List<Long> ids =  sessionManager.getSeleccioAnotacio();
		if (ids == null || ids.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.anotacio.selec"));
		} else {
			dto.setAuxIds(ids);
			try {
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.success(
						request,
						getMessage(
								request,
								"anotacio.llistat.accio.massiva.info.esborrar.anotacions.success"));
			} catch(Exception e) {
				MissatgesHelper.error(
						request,
						getMessage(
								request,
								"anotacio.llistat.accio.massiva.info.esborrar.anotacions.error",
								new Object[] {e.getMessage()}));
			}					
			// Neteja la selecció
			sessionManager.getSeleccioAnotacio().clear();
		}
		return "redirect:/v3/anotacio";
	}
	
	
	/** Acció del menú desplegable d'Accions massives d'anotacions, per iniciar una tasca en segon pla per reintentar el processament de les
	 * anotacions seleccionades a le taula d'anotacions (les que tenen error de processament es tornarien a intentar processar i les anotacions
	 *	pendents d'accio manual es miraria si es pot processar amb algun tipus d'expedient)
	 */
	@RequestMapping(value = "/reintentarProcessament", method = RequestMethod.GET)
	public String reintentarProcessament(
			HttpServletRequest request,
			Model model) {
		// Programa la execució massiva
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		ExecucioMassivaDto dto = new ExecucioMassivaDto();
		dto.setTipus(ExecucioMassivaTipusDto.REINTENTAR_PROCESSAMENT_ANOTACIONS);
		dto.setEnviarCorreu(false);
		List<Long> ids =  sessionManager.getSeleccioAnotacio();
		if (ids == null || ids.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.anotacio.selec"));
		} else {
			dto.setAuxIds(ids);
			try {
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.success(
						request,
						getMessage(
								request,
								"anotacio.llistat.accio.massiva.info.reintentar.processament.success"));
			} catch(Exception e) {
				MissatgesHelper.error(
						request,
						getMessage(
								request,
								"anotacio.llistat.accio.massiva.info.reintentar.processament.error",
								new Object[] {e.getMessage()}));
			}					
			// Neteja la selecció
			sessionManager.getSeleccioAnotacio().clear();
		}
		return "redirect:/v3/anotacio";
	}
	
	/** Acció del menú desplegable d'Accions massives d'anotacions, per iniciar una tasca en segon pla per reprocessar el mapeig de les
	 * anotacions seleccionades a le taula d'anotacions (les que tenen un expedient associat es tornaria a aplicar el mapeig)
	 */
	@RequestMapping(value = "/reprocessarMapeig", method = RequestMethod.GET)
	public String reprocessarMapeig(
			HttpServletRequest request,
			Model model) {
		// Programa la execució massiva
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		ExecucioMassivaDto dto = new ExecucioMassivaDto();
		dto.setTipus(ExecucioMassivaTipusDto.REINTENTAR_MAPEIG_ANOTACIONS);
		dto.setEnviarCorreu(false);
		List<Long> ids =  sessionManager.getSeleccioAnotacio();
		if (ids == null || ids.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.anotacio.selec"));
		} else {
			dto.setAuxIds(ids);
			try {
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.success(
						request,
						getMessage(
								request,
								"anotacio.llistat.accio.massiva.info.reintentar.mapeig.success"));
			} catch(Exception e) {
				MissatgesHelper.error(
						request,
						getMessage(
								request,
								"anotacio.llistat.accio.massiva.info.reintentar.mapeig.error",
								new Object[] {e.getMessage()}));
			}					
			// Neteja la selecció
			sessionManager.getSeleccioAnotacio().clear();
		}
		return "redirect:/v3/anotacio";
	}
	
	/** Acció del menú desplegable d'Accions massives d'anotacions, per iniciar una tasca en segon pla per reintentar el processament dels annexos de les
	 *  anotacions, és a dir moure els diferents annexos que encara estiguin a l'expedient d'Arxiu de Distribucio cap a la carpeta de l'anotació dins d'expedient 
	 *  d'Arxiu d'Helium
	 */
	@RequestMapping(value = "/reintentarProcessamentNomesAnnexos", method = RequestMethod.GET)
	public String reintentarProcessamentNomesAnnexos(
			HttpServletRequest request,
			Model model) {
		// Programa la execució massiva
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		ExecucioMassivaDto dto = new ExecucioMassivaDto();
		dto.setTipus(ExecucioMassivaTipusDto.REINTENTAR_PROCESSAMENT_ANOTACIONS_NOMES_ANNEXOS);
		dto.setEnviarCorreu(false);
		List<Long> ids =  sessionManager.getSeleccioAnotacio();
		if (ids == null || ids.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.anotacio.selec"));
		} else {
			dto.setAuxIds(ids);
			try {
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.success(
						request,
						getMessage(
								request,
								"anotacio.llistat.accio.massiva.info.reintentar.processament.nomes.annexos.success"));
			} catch(Exception e) {
				MissatgesHelper.error(
						request,
						getMessage(
								request,
								"anotacio.llistat.accio.massiva.info.reintentar.processament.nomes.annexos.error",
								new Object[] {e.getMessage()}));
			}					
			sessionManager.getSeleccioAnotacio().clear();
		}
		return "redirect:/v3/anotacio";
	}
	
	private static final Log logger = LogFactory.getLog(AnotacioController.class);
}
