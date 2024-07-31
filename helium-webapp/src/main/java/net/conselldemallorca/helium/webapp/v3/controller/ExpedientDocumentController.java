package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.util.PdfUtils;
import net.conselldemallorca.helium.core.util.StringUtilsHelium;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalTipus;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalViaTipus;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentInfoDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentStoreDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTipusFirmaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnviamentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.FirmaResultatDto;
import net.conselldemallorca.helium.v3.core.api.dto.IdiomaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesIniciFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesPrioritatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesSimpleTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.dto.ServeiTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.StatusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.TokenDto;
import net.conselldemallorca.helium.v3.core.api.dto.document.DocumentDetallDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientInteressatService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTokenService;
import net.conselldemallorca.helium.v3.core.api.service.PortafirmesFluxService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Create;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Update;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientEnviarPortasignaturesCommand;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientEnviarPortasignaturesCommand.EnviarPortasignatures;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientFirmaPassarelaCommand;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientFirmaPassarelaCommand.FirmaPassarela;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientNotificarZipCommand;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientNotificarZipCommand.NotificarZip;
import net.conselldemallorca.helium.webapp.v3.command.DocumentNotificacioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.EnumHelper;
import net.conselldemallorca.helium.webapp.v3.helper.JsonResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ModalHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NtiHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.UrlHelper;

/**
 * Controlador per a la pàgina de documents de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientDocumentController extends BaseExpedientController {

	@Autowired
	private ExpedientDocumentService expedientDocumentService;
	// TODO: eliminar la referencia al core 2.6 i passar el mètode processarDocumentPendentPortasignatures al pluginHelper
	@Autowired
	private PluginService pluginService;
	@Autowired
	private NtiHelper ntiHelper;
	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	@Autowired
	private AplicacioService aplicacioService;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Autowired
	private PortafirmesFluxService portafirmesFluxService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	protected DocumentService documentService;
	@Autowired
	protected ExpedientTokenService expedientTokenService;
	@Autowired
	protected DefinicioProcesService definicioProcesService;

	/** 
	 * comparador per ordenar documents per codi de document primer i per títol de l'adjunt si són adjunts després.
	 */
	protected class  ExpedientDocumentDtoComparador implements Comparator<ExpedientDocumentDto>{
		
		/** Ordre per defecte, data o títol. */
		private String sort;

		public ExpedientDocumentDtoComparador(String sort) {
			this.sort = sort;
		}

		/** Compara primer per codi de document i si són annexs i no en tenen llavors per títol de l'annex. */
		@Override
		public int compare(ExpedientDocumentDto doc1, ExpedientDocumentDto doc2) {
			int ret;
			if ("data".equals(this.sort)) {
				// Ordena per data
				ret = doc1.getDataDocument().compareTo(doc2.getDataDocument());
			} else if ("titol".equals(this.sort)) {
				// Ordena per títol
				String titol1 = doc1.isAdjunt() ? doc1.getAdjuntTitol() : doc1.getDocumentNom();
				String titol2 = doc2.isAdjunt() ? doc2.getAdjuntTitol() : doc2.getDocumentNom();
				ret = titol1.toLowerCase().compareTo(titol2.toLowerCase());
			} else {
				// Compara per codi de document primer
				if (doc1.getDocumentCodi() != null && doc2.getDocumentCodi() != null)
					ret = doc1.getDocumentCodi().compareTo(doc2.getDocumentCodi());
				else if (doc1.getDocumentCodi() != null)
					ret = -1;
				else if (doc2.getDocumentCodi() != null)
					ret = 1;
				else 
					// els annexos van darrera ordenats per adjuntTitol contemplant que pugui ser null
					ret = (doc1.getAdjuntTitol() == null ? 
							(doc2.getAdjuntTitol() == null ? 0 : 1) 
							: (doc2.getAdjuntTitol() == null ? 
									-1 
									: doc1.getAdjuntTitol().compareTo(doc2.getAdjuntTitol())));
			}
			return ret;
		}
	}

	// Aquest mètode nomes es crida per expedients per estats, no per fluxe.
	@RequestMapping(value = "/{expedientId}/document/datatable", method = RequestMethod.POST)
	@ResponseBody
	public DatatablesResponse documentDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {

		Boolean tots = Boolean.parseBoolean(request.getParameter("tots"));
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientDocumentService.findDocumentsExpedient(expedientId, tots, paginacioParams),
				"id");
	}

	@RequestMapping(value = "/{expedientId}/document/selection", method = RequestMethod.POST)
	@ResponseBody
	public Set<Long> documentSelection(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(value = "ids[]", required = false) Long[] ids,
			@RequestParam(value = "method", required = false) String method,
			Model model) {
		return documentSelectionTipus(request, expedientId, "selection", ids, method, model);
	}

	@RequestMapping(value = "/{expedientId}/document/selection/{tipus}", method = RequestMethod.POST)
	@ResponseBody
	public Set<Long> documentSelectionTipus(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tipus,
			@RequestParam(value = "ids[]", required = false) Long[] ids,
			@RequestParam(value = "method", required = false) String method,
			Model model) {

		SessionHelper.SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioDocuments(expedientId);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioDocuments(expedientId,seleccio);
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
			seleccio.addAll(expedientDocumentService.findIdsDocumentsByExpedient(expedientId));
		}

		sessionManager.setSeleccioDocuments(expedientId, seleccio);
		return seleccio;
	}

	@RequestMapping(value = "/{expedientId}/document", method = RequestMethod.GET)
	public String document(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(defaultValue = "default") String sort,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		if (ExpedientTipusTipusEnumDto.ESTAT.equals(expedient.getTipus().getTipus())) {
			model.addAttribute("expedient", expedient);
			model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
			if (!NodecoHelper.isNodeco(request)) {
				return mostrarInformacioExpedientPerPipella(
						request,
						expedientId,
						model,
						"documents");
			}
			return "v3/expedientDocumentList";
		}

		// Obté l'arbre de processos però retorna un map amb tots els processos consultant només el principal
		List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		Map<InstanciaProcesDto, List<ExpedientDocumentDto>> documents  = new LinkedHashMap<InstanciaProcesDto, List<ExpedientDocumentDto>> ();
		for (InstanciaProcesDto instanciaProces : arbreProcessos) {
			List<ExpedientDocumentDto> documentsInstancia = null;
			if (instanciaProces.getId().equals(expedient.getProcessInstanceId())) {
				documentsInstancia = this.getDocumentsPerProces(expedient, instanciaProces, sort);
			}
			documents.put(instanciaProces, documentsInstancia);
		}
		
		List<PortasignaturesDto> portasignaturesPendent = expedientDocumentService.portasignaturesFindPendents(
				expedientId,
				expedient.getProcessInstanceId());

		model.addAttribute("expedient", expedient);
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("documents", documents);
		model.addAttribute("portasignaturesPendent", portasignaturesPendent);
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientPerPipella(
					request,
					expedientId,
					model,
					"documents");
		}
		return "v3/expedientDocument";
	}

	/** Obté un Map<InstanciaProcesDto, List<ExpedientDocumentDto>> amb els documetns per instància de procés
	 * 
	 * @param expedient
	 * @param proces
	 * @param sort 
	 * @return
	 */
	private Map<InstanciaProcesDto, List<ExpedientDocumentDto>> getDocumentsArbreProcessos(
			ExpedientDto expedient, 
			String sort) {
		List<InstanciaProcesDto> processos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		Map<InstanciaProcesDto, List<ExpedientDocumentDto>> documents = new LinkedHashMap<InstanciaProcesDto, List<ExpedientDocumentDto>>();
		// Per a cada instància de procés ordenem les dades per agrupació  
		// (si no tenen agrupació les primeres) i per ordre alfabètic de la etiqueta
		for (InstanciaProcesDto instanciaProces: processos) {
			List<ExpedientDocumentDto> documentsInstancia = this.getDocumentsPerProces(expedient, instanciaProces, sort);
			documents.put(instanciaProces, documentsInstancia);
		}
		return documents;
	}

	private List<ExpedientDocumentDto> getDocumentsPerProces(
			ExpedientDto expedient, 
			InstanciaProcesDto instanciaProces, 
			String sort) {
		// Per a cada instància de procés ordenem les dades per agrupació  
		// (si no tenen agrupació les primeres) i per ordre alfabètic de la etiqueta
		List<ExpedientDocumentDto> documentsInstancia = expedientDocumentService.findAmbInstanciaProces(
				expedient.getId(),
				instanciaProces.getId());
		// Ordena els documents per codi de document i si són annexos i no en tenen llavors van darrera ordenats per nom
		Collections.sort(documentsInstancia, new ExpedientDocumentDtoComparador(sort));	
		return documentsInstancia;
	}

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document", method = RequestMethod.GET)
	public String documentProces(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@RequestParam(defaultValue = "default") String sort,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);

		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId);
		Map<InstanciaProcesDto, List<ExpedientDocumentDto>> documents  = new HashMap<InstanciaProcesDto, List<ExpedientDocumentDto>>();
		documents.put(instanciaProces, this.getDocumentsPerProces(expedient, instanciaProces, sort));
		List<PortasignaturesDto> portasignaturesPendent = expedientDocumentService.portasignaturesFindPendents(
				expedientId,
				processInstanceId);
		model.addAttribute("expedient", expedient);
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("documents",documents);
		model.addAttribute("portasignaturesPendent", portasignaturesPendent);
		return "v3/procesDocuments";
	}

	@RequestMapping(value = "/{expedientId}/document/{documentCodi}/new", method = RequestMethod.GET)
	public String nouDocCodiGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String documentCodi,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		DocumentDto document = documentService.findAmbCodi(expedient.getTipus().getId(), null, documentCodi, false);
		DocumentExpedientCommand command = DocumentExpedientCommand.builder()
				.expedientId(expedientId)
				.data(new Date())
				.validarArxius(true)
				.codi(documentCodi)
				.nom(document.getNom())
				.ntiActiu(expedient.isNtiActiu())
				.ntiOrigen(document.getNtiOrigen())
				.ntiEstadoElaboracion(document.getNtiEstadoElaboracion())
				.ntiTipoDocumental(document.getNtiTipoDocumental())
				.generarPlantilla(document.isPlantilla())
				.build();
		model.addAttribute("processInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("documentExpedientCommand", command);
		emplenarModelNti(expedientId, model);
		model.addAttribute(
				"tipusFirmaOptions",
				EnumHelper.getOptionsForEnum(
						DocumentTipusFirmaEnumDto.class,
						"enum.document.tipus.firma."));
		model.addAttribute("ambDocument", true);
		return "v3/expedientDocumentForm";
	}
	
	@RequestMapping(value = "/{expedientId}/documentPinbal/{documentId}/info", method = RequestMethod.GET)
	@ResponseBody
	public ExpedientDocumentPinbalDto documentPinbalInfo(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentId,
			Model model) {
		return dissenyService.findDocumentPinbalByExpedient(expedientId, documentId);
	}
	
	@RequestMapping(value = "/{expedientId}/documentPinbal/new", method = RequestMethod.GET)
	public String documentPinbalNouGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		return nouDocumentPinbalGet(request, expedientId, null, model);
	}
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/documentPinbal/new", method = RequestMethod.GET)
	public String nouDocumentPinbalGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			Model model) {
		ExpedientDocumentPinbalDto command = new ExpedientDocumentPinbalDto();
		command.setExpedientId(expedientId);
		command.setProcessInstanceId(processInstanceId);
		omplirModelFormDocumentPinbal(model, command, false);
		return "v3/expedientDocumentPinbalForm";
	}
	
	private void omplirModelFormDocumentPinbal(
			Model model,
			ExpedientDocumentPinbalDto command,
			boolean intentGuardar) {
		model.addAttribute("processInstanceId", command.getProcessInstanceId());
		model.addAttribute("expedientDocumentPinbalDto", command);
		model.addAttribute("documentsPinbal", getDocumentsPinbal(command.getExpedientId(), command.getProcessInstanceId()));
		model.addAttribute("interessats", expedientInteressatService.findByExpedient(command.getExpedientId()));
		ntiHelper.omplirConsentiment(model);
		ntiHelper.omplirTipusPassaport(model);
		ntiHelper.omplirSexe(model);
		List<ParellaCodiValorDto> tdlist = new ArrayList<ParellaCodiValorDto>();
		model.addAttribute("comunitats", tdlist);
		model.addAttribute("provincies", tdlist);
		model.addAttribute("municipis", tdlist);
		model.addAttribute("paisos", tdlist);
		command.setCommandValidat(intentGuardar);
	}
	
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/documentPinbal/new", method = RequestMethod.POST)
	public String nouDocumentPinbalPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@ModelAttribute ExpedientDocumentPinbalDto command,
			BindingResult bindingResult,
			Model model) {
		
		switch (command.getCodiServei()) {
		case SVDDELSEXWS01:
			if (command.getDataNaixement()!=null) {
				bindingResult.rejectValue("dataNaixement", "NotEmpty");
			}
			if (command.getPaisNaixament().equals("724") && StringUtils.isEmpty(command.getMunicipiNaixamentSVDDELSEXWS01())) {
				bindingResult.rejectValue("municipiNaixament", "NotEmpty");
			}
			if (!command.getPaisNaixament().equals("724") && StringUtils.isEmpty(command.getPoblacioNaixament())) {
				bindingResult.rejectValue("poblacioNaixament", "NotEmpty");
			}			
			if (command.getCodiNacionalitat().equals("724") && StringUtils.isEmpty(command.getNomPare()) && StringUtils.isEmpty(command.getNomMare())) {
				bindingResult.rejectValue("nomPare", "NotEmpty");
			}
			break;
		case NIVRENTI:
			if (command.getExercici() == null) {
				bindingResult.rejectValue("exercici", "NotEmpty");
			}
			break;
		case SVDDGPRESIDENCIALEGALDOCWS01:
			if (command.getTipusPassaport() == null && StringUtilsHelium.isEmpty(command.getNumeroSoporte())) {
				bindingResult.reject("contingut.pinbal.form.camp.tipus.numero.soporte.passaport.comment");
			}
			if (command.getTipusPassaport() != null && command.getDataCaducidad() == null) {
				bindingResult.rejectValue("dataCaducidad", "NotEmpty");
			}
			if (command.getTipusPassaport() != null && StringUtilsHelium.isEmpty(command.getCodiNacionalitat())) {
				bindingResult.rejectValue("codiNacionalitat", "NotEmpty");
			}
			break;
		case SVDRRCCNACIMIENTOWS01:
			if (StringUtilsHelium.isEmpty(command.getRegistreCivil())) {
				bindingResult.rejectValue("registreCivil", "NotEmpty");
			}
			if (StringUtilsHelium.isEmpty(command.getPagina())) {
				bindingResult.rejectValue("pagina", "NotEmpty");
			}
			if (StringUtilsHelium.isEmpty(command.getTom())) {
				bindingResult.rejectValue("tom", "NotEmpty");
			}
			if (command.getDataRegistre() == null) {
				bindingResult.rejectValue("dataRegistre", "NotEmpty");
			}
			break;
		case SVDBECAWS01:
			if (command.getCurs() == null) {
				bindingResult.rejectValue("curs", "NotEmpty");
			}
			break;
		default:
			break;
		}
		
		if (bindingResult.hasErrors()) {
			omplirModelFormDocumentPinbal(model, command, true);
			return "v3/expedientDocumentPinbalForm";
		} else {
			try {
				String resultat = documentService.createDocumentPinbal(command);
				if (resultat.contains("resultat.ok")) {
					MissatgesHelper.success(request, 
							getMessage(
									request,
									resultat,
									new Object[] {command.getDocumentNom(), command.getCodiServei()}));
				} else {
					MissatgesHelper.error(request, resultat);
					omplirModelFormDocumentPinbal(model, command, true);
					return "v3/expedientDocumentPinbalForm";
				}
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(
						request,
						"consultes.pinbal.resultat.ko",
						new Object[] {command.getDocumentNom(), command.getCodiServei(), ex.getMessage()}));
				omplirModelFormDocumentPinbal(model, command, true);
				return "v3/expedientDocumentPinbalForm";
			}
		}
		
		return modalUrlTancar();
	}
	
	@RequestMapping(value = "/{expedientId}/document/new", method = RequestMethod.GET)
	public String documentNouGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		return nouGet(request, expedientId, null, model);
	}
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/new", method = RequestMethod.GET)
	public String nouGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			Model model) {
		if (processInstanceId == null) {
			processInstanceId = expedientService.findAmbId(expedientId).getProcessInstanceId();
			model.addAttribute("documentsNoUtilitzats", expedientDocumentService.getDocumentsNoUtilitzatsPerEstats(expedientId));
		} else {
			model.addAttribute("documentsNoUtilitzats", getDocumentsNoUtilitzats(expedientId, processInstanceId));
		}
		DocumentExpedientCommand command = new DocumentExpedientCommand();
		command.setExpedientId(expedientId);
		command.setData(new Date());
		command.setValidarArxius(true);
		model.addAttribute("processInstanceId", processInstanceId);
		model.addAttribute("documentExpedientCommand", command);
		emplenarModelNti(expedientId, model);
		model.addAttribute(
				"tipusFirmaOptions",
				EnumHelper.getOptionsForEnum(
						DocumentTipusFirmaEnumDto.class,
						"enum.document.tipus.firma."));
		return "v3/expedientDocumentForm";
	}

	@RequestMapping(value = "/{expedientId}/document/{documentCodi}/new", method = RequestMethod.POST)
	public String nouDocCodiPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String documentCodi,
			@Validated(Create.class) @ModelAttribute DocumentExpedientCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		command.setDocumentCodi(documentCodi);
		model.addAttribute("ambDocument", true);
		return nouPost(request, expedientId, null, command, bindingResult, model);
	}
	@RequestMapping(value="/{expedientId}/document/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@Validated(Create.class) @ModelAttribute DocumentExpedientCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		return nouPost(request, expedientId, null, command, bindingResult, model);
	}
	@RequestMapping(value="/{expedientId}/proces/{processInstanceId}/document/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@Validated(Create.class) @ModelAttribute DocumentExpedientCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		boolean perEstats = false;
		if (processInstanceId == null) {
			processInstanceId = expedient.getProcessInstanceId();
			perEstats = true;
		}
		command.setNtiActiu(expedient.isNtiActiu());
		if (!bindingResult.hasErrors()) {
			String documentCodi = null;
			if (!DocumentExpedientCommand.ADJUNTAR_ARXIU_CODI.equalsIgnoreCase(command.getDocumentCodi())) {
				documentCodi = command.getDocumentCodi();
			}
			String arxiuNom = "";
			byte[] arxiuContingut = null;
			String arxiuContentType = "";
			byte[] firmaContingut = null;
			if (command.isGenerarPlantilla()) {
				try {
					ArxiuDto generat = expedientDocumentService.generarAmbPlantilla(
							expedientId,
							processInstanceId,
							documentCodi);
					arxiuNom = generat.getNom();
					arxiuContingut = generat.getContingut();
					arxiuContentType = generat.getTipusMime();
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.generar.document.info", new Object[] {ex.getMessage()}));
					logger.error("Error generant el document: " + documentCodi, ex);
				} 
			} else {
				arxiuContingut = command.getArxiu().getBytes();
				arxiuContentType = command.getArxiu().getContentType();
				if (command.getFirma() != null && command.getFirma().getSize() > 0) {
					firmaContingut = command.getFirma().getBytes();
				}
				arxiuNom = command.getArxiu().getOriginalFilename();
			}
			if (arxiuContingut != null) {
				try {
					expedientDocumentService.create(
							expedientId,
							processInstanceId,
							documentCodi, // null en el cas dels adjunts
							command.getData(),
							command.getNom(), // Títol en el cas dels adjunts
							arxiuNom,
							arxiuContingut,
							arxiuContentType,
							command.isAmbFirma(),
							DocumentTipusFirmaEnumDto.SEPARAT.equals(command.getTipusFirma()),
							firmaContingut,
							command.getNtiOrigen(),
							command.getNtiEstadoElaboracion(),
							command.getNtiTipoDocumental(),
							command.getNtiIdOrigen(),
							null);
					
					MissatgesHelper.success(request, getMessage(request, "info.document.guardat") );
					return modalUrlTancar(false);
				} catch(Exception e) {
					String errMsg = getMessage(request, "info.document.guardat.error", new Object[] {e.getMessage()});
					logger.error(errMsg, e);
					MissatgesHelper.error(request, errMsg);
				}
			}
		}
		if (perEstats) {
			model.addAttribute("documentsNoUtilitzats", expedientDocumentService.getDocumentsNoUtilitzatsPerEstats(expedientId));
		} else {
    	model.addAttribute("documentsNoUtilitzats", getDocumentsNoUtilitzats(expedientId, processInstanceId));
		}
    	model.addAttribute("documentsNoUtilitzats", getDocumentsNoUtilitzats(expedientId, processInstanceId));
		model.addAttribute("processInstanceId", processInstanceId);
		model.addAttribute("documentExpedientCommand", command);
		emplenarModelNti(expedientId, model);
		model.addAttribute(
				"tipusFirmaOptions",
				EnumHelper.getOptionsForEnum(
						DocumentTipusFirmaEnumDto.class,
						"enum.document.tipus.firma."));
    	return "v3/expedientDocumentForm";
	}

	@RequestMapping(value = "/{expedientId}/document/{documentStoreId}/update", method = RequestMethod.GET)
	public String updateDocGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			Model model) {
		return updateGet(request, expedientId, null, documentStoreId, model);
	}
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/update", method = RequestMethod.GET)
	public String updateGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {

		if (processInstanceId == null) {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			processInstanceId = expedient.getProcessInstanceId();
		}

		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);
		DocumentExpedientCommand command = new DocumentExpedientCommand();
		command.setExpedientId(expedientId);
		command.setDocId(document.getDocumentId());
		command.setValidarArxius(false);
		if (document.isAdjunt()) {
			command.setNom(document.getAdjuntTitol());
		} else {
			command.setNom(document.getDocumentNom());
		}
		command.setCodi(document.getDocumentCodi());
		command.setData(document.getDataDocument());
		model.addAttribute("document", document);	
		model.addAttribute("expedientId", expedientId);
		ExpedientDto expedient = emplenarModelNti(expedientId, model);
		if (expedient.isNtiActiu()) {
			command.setNtiOrigen(document.getNtiOrigen());
			command.setNtiEstadoElaboracion(document.getNtiEstadoElaboracion());
			command.setNtiTipoDocumental(document.getNtiTipoDocumental());
			command.setNtiIdOrigen(document.getNtiIdOrigen());
		}
		model.addAttribute("documentExpedientCommand", command);
		model.addAttribute(
				"tipusFirmaOptions",
				EnumHelper.getOptionsForEnum(
						DocumentTipusFirmaEnumDto.class,
						"enum.document.tipus.firma."));
		return "v3/expedientDocumentForm";
	}

	@RequestMapping(value="/{expedientId}/document/{documentStoreId}/update", method = RequestMethod.POST)
	public String updateDocPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			@Validated(Update.class) @ModelAttribute DocumentExpedientCommand command,
			BindingResult result,
			Model model) throws IOException {
		return updatePost(request, expedientId, null, documentStoreId, command, result, model);
	}
	@RequestMapping(value="/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/update", method = RequestMethod.POST)
	public String updatePost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@Validated(Update.class) @ModelAttribute DocumentExpedientCommand command,
			BindingResult result,
			Model model) throws IOException {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		if (processInstanceId == null) {
			processInstanceId = expedient.getProcessInstanceId();
		}
		command.setNtiActiu(expedient.isNtiActiu());
		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
    			expedientId,
    			processInstanceId,
    			documentStoreId);
		if (!result.hasErrors()) {
			String arxiuNom = "";
			byte[] arxiuContingut = null;
			String arxiuContentType = "";
			byte[] firmaContingut = null;
			if (command.isGenerarPlantilla()) {
				try {
					ArxiuDto generat = expedientDocumentService.generarAmbPlantilla(
							expedientId,
							processInstanceId,
							document.getDocumentCodi());
					arxiuNom = generat.getNom();
					arxiuContingut = generat.getContingut();
					arxiuContentType = generat.getTipusMime();
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.generar.document.info", new Object[] {ex.getMessage()}));
					logger.error("Error generant el document: " + document.getDocumentCodi(), ex);
				} 				
			} else {
					arxiuContingut = command.getArxiu().getBytes();
					arxiuNom = command.getArxiu().getOriginalFilename();
					arxiuContentType = command.getArxiu().getContentType();
					if(arxiuContingut == null || arxiuContingut.length == 0) {
						ArxiuDto arxiu = expedientDocumentService.arxiuFindAmbDocument(expedientId, processInstanceId, documentStoreId);
						arxiuContingut = arxiu.getContingut();
						arxiuNom = arxiu.getNom();
					}
					firmaContingut = null;
					if (command.getFirma() != null && command.getFirma().getSize() > 0) {
						firmaContingut = command.getFirma().getBytes();
					}
				}

			if (arxiuContingut != null) {
				try {
					expedientDocumentService.update(
							expedientId,
							processInstanceId,
							documentStoreId,
							command.getData(),
							document.isAdjunt() ? // Títol en el cas dels adjunts 
									command.getNom() 
									: null, 
							arxiuNom,
							(arxiuContingut.length != 0) ? arxiuContingut : null,
							arxiuContentType,
							command.isAmbFirma(),
							DocumentTipusFirmaEnumDto.SEPARAT.equals(command.getTipusFirma()),
							firmaContingut,
							command.getNtiOrigen(),
							command.getNtiEstadoElaboracion(),
							command.getNtiTipoDocumental(),
							command.getNtiIdOrigen());
					
					MissatgesHelper.success(request, getMessage(request, "info.document.guardat"));
					return modalUrlTancar(false);				
				} catch(Exception e) {
					String errMsg = getMessage(request, "info.document.guardat.error", new Object[] {e.getMessage()});
					logger.error(errMsg, e);
					MissatgesHelper.error(request, errMsg);				
				}
			}
		}
		// Retorna al formulari per mostrar errors
		model.addAttribute("processInstanceId", processInstanceId);
		model.addAttribute("document", document);
		emplenarModelNti(expedientId, model);
		model.addAttribute(
				"tipusFirmaOptions",
				EnumHelper.getOptionsForEnum(
						DocumentTipusFirmaEnumDto.class,
						"enum.document.tipus.firma."));
    	return "v3/expedientDocumentForm";
	}

	
	@RequestMapping(value = "/{expedientId}/document/{documentStoreId}/detall", method = RequestMethod.GET)
	public String getDetall(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			Model model) throws Exception {
	
		try {
			DocumentDetallDto documentDetall = expedientDocumentService.getDocumentDetalls(expedientId, documentStoreId);
			model.addAttribute("detall", documentDetall);
			model.addAttribute("expedientId", expedientId);
		} catch(Exception e) {
			String errMsg = getMessage(request, "expedient.document.detall.consulta.error", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			throw new Exception(errMsg, e);
		}
		return "v3/expedientDocumentDetall";
	}

	@RequestMapping(value = "/{expedientId}/document/{documentStoreId}/notificar", method = RequestMethod.GET)
	public String notificarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			Model model) {
		return notificarGet(request, expedientId, null, documentStoreId, model);
	}
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/notificar", method = RequestMethod.GET)
	public String notificarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {

		if (processInstanceId == null) {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			processInstanceId = expedient.getProcessInstanceId();
		}

		DocumentNotificacioCommand command = new DocumentNotificacioCommand();
		Calendar caducitat = new GregorianCalendar();
		caducitat.setTime(new Date());
		caducitat.add(Calendar.DATE, 10);
		command.setCaducitat(caducitat.getTime());
		command.setEnviamentTipus(EnviamentTipusEnumDto.NOTIFICACIO);
		command.setIdioma(IdiomaEnumDto.CA);
		model.addAttribute("documentNotificacioCommand", command);
		
		ExpedientDocumentDto document = this.emplenarModelNotificacioDocument(expedientId, processInstanceId, documentStoreId, model);
		//Validar que tingui els permissos de gestió documental o administrar
		command.setConcepte(document.getArxiuNom());
		ExpedientDto expedientDto = expedientService.findAmbIdAmbPermis(expedientId);
		if (!expedientDto.isPermisDocManagement() && !expedientDto.isPermisAdministration()) {
				MissatgesHelper.error(request, 
					getMessage(request, 
							"expedient.document.notificar.validacio.no.permisos"));
				return modalUrlTancar(false);
			}

		// Validar que l'arxiu sigui convertible a pdf o zip
		if (!PdfUtils.isArxiuConvertiblePdf(document.getArxiuNom())
				&& !"zip".equals((document.getArxiuExtensio() != null ? document.getArxiuExtensio().toLowerCase() :  ""))) {
			MissatgesHelper.error(request, 
					getMessage(request, 
							"expedient.document.notificar.validacio.no.notificable",
							new Object[] {PdfUtils.getExtensionsConvertiblesPdf()}));
			return modalUrlTancar(false);
		}
		return "v3/expedientDocumentNotificar";
	}
	
	@RequestMapping(value="/checkMidaCampsNotificacio", method = RequestMethod.GET)
	@ResponseBody
	public String checkMidaCampsNotificacio(
			HttpServletRequest request,
			@RequestParam List<Long> nifs,
			Model model) throws IOException {
		List<String> resultatCheck = expedientInteressatService.checkMidaCampsNotificacio(nifs);
		if (resultatCheck!=null && resultatCheck.size()>0) {
			
			String message = getMessage(request, "expedient.notifica.dades.retallar");
			
			for (String aux: resultatCheck) {
				message = message + "<br/>" + aux;
			}
			
			MissatgesHelper.warning(request, message);
			
			return "KO";
		}
		
		return "OK";
	}
	
	@RequestMapping(value="/{expedientId}/document/{documentStoreId}/notificar", method = RequestMethod.POST)
	public String notificarDocPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			@Validated DocumentNotificacioCommand documentNotificacioCommand,
			BindingResult result,
			Model model) throws IOException {
		return notificarPost(request, expedientId, null, documentStoreId, documentNotificacioCommand, result, model);
	}
	@RequestMapping(value="/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/notificar", method = RequestMethod.POST)
	public String notificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@Validated DocumentNotificacioCommand documentNotificacioCommand,
			BindingResult result,
			Model model) throws IOException {

		if (processInstanceId == null) {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			processInstanceId = expedient.getProcessInstanceId();
		}

		if (result.hasErrors()) {
			this.emplenarModelNotificacioDocument(expedientId, processInstanceId, documentStoreId, model);
	    	return "v3/expedientDocumentNotificar";
		}
		try {
			DadesNotificacioDto dadesNotificacioDto = ConversioTipusHelper.convertir(
					documentNotificacioCommand, 
					DadesNotificacioDto.class);		
			
			// Dona d'alta una notificació per interessat
			DadesNotificacioDto dadesNotificacio;
			InteressatDto interessat;
			for (Long interessatId : documentNotificacioCommand.getInteressatsIds()) {
				interessat = expedientInteressatService.findOne(interessatId);
				// Alta de la notificació
				dadesNotificacio = expedientDocumentService.notificarDocument(
						expedientId,
						documentStoreId,
						dadesNotificacioDto.getDocumentsDinsZip(),
						dadesNotificacioDto,
						interessatId,
						documentNotificacioCommand.getRepresentantId());
				
				// Missatge del resultat
				if (! dadesNotificacio.getError())
					MissatgesHelper.success(request, getMessage(request, "info.document.notificat", new Object[] {interessat.getFullInfo()}));
				else
					MissatgesHelper.warning(request, getMessage(request, "info.document.notificar.avis", new Object[] {interessat.getFullInfo(), dadesNotificacio.getErrorDescripcio()}));
			}
			try {
				//Si estem notificant un zip, un cop notificat firmen en servidor
				if(dadesNotificacioDto!=null && dadesNotificacioDto.getDocumentArxiuContingut()!=null) {
					Tika tika = new Tika();
					if(tika.detect(dadesNotificacioDto.getDocumentArxiuContingut()).contains("zip")) {	
						ArxiuDto arxiu = expedientDocumentService.arxiuFindAmbDocument(
								expedientId,
								processInstanceId,
								documentStoreId);
						documentHelper.firmaServidor(processInstanceId, documentStoreId, "notificació de zip", arxiu.getContingut());
					}
				}
			} catch (Exception e) {
				String errMsg = getMessage(request, "info.signatura.doc.processat.error", new Object[] {e.getMessage()});
				logger.error(errMsg, e);
				MissatgesHelper.error(request, errMsg);
				this.emplenarModelNotificacioDocument(expedientId, processInstanceId, documentStoreId, model);
				return "v3/expedientDocumentNotificar";
			}
		} catch(Exception e) {
			String errMsg = getMessage(request, "info.document.notificar.error", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
			this.emplenarModelNotificacioDocument(expedientId, processInstanceId, documentStoreId, model);
			return "v3/expedientDocumentNotificar";
		}
		return modalUrlTancar(true);
	}
	
	private ExpedientDocumentDto emplenarModelNotificacioDocument(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			Model model) {
		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);
		List<InteressatDto> interessats = expedientInteressatService.findByExpedient(
				expedientId);
		model.addAttribute("interessats", interessats);
		model.addAttribute("document", document);	
		model.addAttribute("expedientId", expedientId);
		
		// entregaPostalViaTipusEstats
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
		for(EntregaPostalViaTipus entregaPostalViaTipus : EntregaPostalViaTipus.values())
			opcions.add(new ParellaCodiValorDto(
					entregaPostalViaTipus.name(),
					MessageHelper.getInstance().getMessage("notifica.entregaPostal.via.tipus.enum." + entregaPostalViaTipus.name())));		

		model.addAttribute("entregaPostalViaTipusEstats", opcions);

		// entregaPostalTipusEstats
		opcions = new ArrayList<ParellaCodiValorDto>();
		for(EntregaPostalTipus entregaPostalTipus : EntregaPostalTipus.values())
			opcions.add(new ParellaCodiValorDto(
					entregaPostalTipus.name(),
					MessageHelper.getInstance().getMessage("notifica.entregaPostal.enum." + entregaPostalTipus.name())));		
		model.addAttribute("entregaPostalTipusEstats", opcions);

		// serveiTipusEstats
		opcions = new ArrayList<ParellaCodiValorDto>();
		for(ServeiTipusEnumDto serveiTipus : ServeiTipusEnumDto.values())
			opcions.add(new ParellaCodiValorDto(
					serveiTipus.name(),
					MessageHelper.getInstance().getMessage("notifica.servei.tipus.enum." + serveiTipus.name())));		
		model.addAttribute("serveiTipusEstats", opcions);
		
		// enviamentTipusEstats
		opcions = new ArrayList<ParellaCodiValorDto>();
		for(EnviamentTipusEnumDto enviamentTipus : EnviamentTipusEnumDto.values())
			opcions.add(new ParellaCodiValorDto(
					enviamentTipus.name(),
					MessageHelper.getInstance().getMessage("notifica.enviament.tipus.enum." + enviamentTipus.name())));		
		model.addAttribute("enviamentTipusEstats", opcions);

		// idiomes
		opcions = new ArrayList<ParellaCodiValorDto>();
		for(IdiomaEnumDto idioma : IdiomaEnumDto.values())
			opcions.add(new ParellaCodiValorDto(
					idioma.name(),
					MessageHelper.getInstance().getMessage("enum.idioma." + idioma.name())));		
		model.addAttribute("idiomes", opcions);
		
		return document;
	}

	@RequestMapping(value="/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/descarregar")
	public String descarregar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		String ret;
		ArxiuDto arxiu = null;
		Exception exception = null;
		// Prova de descarregar el document
		try {
			if (processInstanceId == null) {
				ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
				processInstanceId = expedient.getProcessInstanceId();
			}
			arxiu = expedientDocumentService.arxiuFindAmbDocument(
					expedientId,
					processInstanceId,
					documentStoreId);
		} catch (SistemaExternException e) {
			exception = e;
		}
		if (exception != null) {
			// Si està integrat amb l'Arxiu prova d'obtenir el contingut original
			try {
				ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
				if (expedient.isArxiuActiu()) {
					// Si està integrat amb l'Arxiu prova de descarregar l'original
					arxiu = expedientDocumentService.arxiuFindOriginal(expedientId, documentStoreId);
					exception = null;
				}
			} catch (Exception e) {
				exception = e;
			}
		}

		// Missatge d'error
		if (exception != null) {
			logger.error("Error obtenint el document", exception);
			MissatgesHelper.error(request, exception.getMessage());
			model.addAttribute("pipellaActiva", "documents");
			ret = "redirect:/v3/expedient/" + expedientId;
		} else {
			if (arxiu != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
			}			
			ret = "arxiuView";
		}
		return ret;
	}
	
	@RequestMapping(value="/{expedientId}/document/{documentStoreId}/descarregar")
	public String docDescarregar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			Model model) {
		return descarregar(request, expedientId, null, documentStoreId, model);
	}

	@RequestMapping(value="/{expedientId}/document/{documentStoreId}/returnFitxer", method = RequestMethod.GET)
	@ResponseBody
	public JsonResponse docPrevisualitzacio(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			Model model) {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			ArxiuDto arxiuPdf = expedientDocumentService.arxiuPdfFindAmbDocument(
					expedientId,
					expedient.getProcessInstanceId(),
					documentStoreId);
			return new JsonResponse(arxiuPdf);

		} catch (SistemaExternException e) {
			logger.error("Error descarregant i convertint fitxer", e);
//			MissatgesHelper.error(request, e.getPublicMessage());
			model.addAttribute("pipellaActiva", "documents");
			return new JsonResponse(true, e.getMessage());
		}
	}

	@RequestMapping(value="/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/descarregar/versio/{versioId}/{expedientTancat}")
	public String descarregarVersio(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@PathVariable String versioId,
			@PathVariable boolean expedientTancat,
			Model model) {
		try {
			ArxiuDto arxiu = expedientDocumentService.arxiuFindAmbDocumentVersio(
					expedientId,
					processInstanceId,
					documentStoreId,
					expedientTancat == true ? null : versioId);
			if (arxiu != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
			}
		} catch (SistemaExternException e) {
			logger.error("Error descarregant fitxer", e);
			MissatgesHelper.error(request, e.getPublicMessage());
			model.addAttribute("pipellaActiva", "documents");
			return "redirect:/v3/expedient/" + expedientId;
		}
		return "arxiuView";
	}

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/metadadesNti", method = RequestMethod.GET)
	public String metadadesNti(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			ExpedientDocumentDto expedientDocument = expedientDocumentService.findOneAmbInstanciaProces(
					expedientId,
					processInstanceId,
					documentStoreId);
			model.addAttribute("expedientDocument", expedientDocument);
			model.addAttribute("expedientId", expedientId);
			if (expedient.isArxiuActiu()) {
				if (!StringUtils.isEmpty(expedientDocument.getArxiuUuid())) {
					model.addAttribute(
							"arxiuDetall",
							expedientDocumentService.getArxiuDetall(
									expedientId,
									processInstanceId,
									documentStoreId));
				} else {
					model.addAttribute("errorArxiuNoUuid", Boolean.TRUE);
				}
				if (expedient.getNtiOrgano() == null) {
					// La migració d'expedients no NTI provocava errors
					model.addAttribute("expedient", expedient);
					model.addAttribute("errorMetadadesNti", Boolean.TRUE);
				}
			}
		} catch(Exception e) {
			String errMsg = "Error consultant les dades de l'Arxiu del document: " + e.getMessage(); 
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}
		return "v3/expedientDocumentMetadadesNti";
	}

	/** Mètode per incoporar el document a l'Arxiu en el cas que l'expedient estigui integrat però el document no. Acció des
	 * de la modal de metadades NTI del document.
	 * @return Retorna cap a la pàgina de metadades nti del document.
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/incoporarArxiu", method = RequestMethod.POST)
	public String incoporarArxiu(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		try {
			expedientDocumentService.migrarArxiu(expedientId, documentStoreId);
			MissatgesHelper.success(request, getMessage(request, "expedient.document.arxiu.migrar.success"));
		} catch(Exception e) {
			String errMsg = "Error incorporant el document a l'Arxiu: " + e.getMessage(); 
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}
		return "redirect:" + request.getHeader("Referer");
	}
	
	/** Mètode per descarregar una firma dettached des de la modal de dades de l'arxiu d'un document. */
	@RequestMapping(value = "/{expedientId}/document/{documentStoreId}/firma/{firmaIndex}/descarregar", method = RequestMethod.GET)
	public String descarregarDocFirma(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			@PathVariable int firmaIndex,
			Model model) {
		return descarregarFirma(request, expedientId, null, documentStoreId, firmaIndex, model);
	}
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/firma/{firmaIndex}/descarregar", method = RequestMethod.GET)
	public String descarregarFirma(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@PathVariable int firmaIndex,
			Model model) {
		try {
			ArxiuFirmaDto arxiuFirma = expedientDocumentService.getArxiuFirma(
					expedientId,
					documentStoreId,
					firmaIndex);
			if (arxiuFirma != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiuFirma.getFitxerNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiuFirma.getContingut());
			}
		} catch (SistemaExternException e) {
			logger.error("Error descarregant l'arxiu de firma", e);
			MissatgesHelper.error(request, e.getPublicMessage());
			model.addAttribute("pipellaActiva", "documents");
			return "redirect:/v3/expedient/" + expedientId;
		}
		return "arxiuView";

	}

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/esborrar")
	@ResponseBody
	public boolean esborrar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,	
			ModelMap model) throws ServletException {
		boolean response = false; 
		try {
			expedientDocumentService.delete(
					expedientId,
					processInstanceId,
					documentStoreId);
			MissatgesHelper.success(request, getMessage(request, "info.doc.proces.esborrat"));
			response = true; 
		} catch (Exception ex) {
			logger.error(getMessage(request, "error.esborrar.doc.proces"), ex);
			MissatgesHelper.error(request, getMessage(request, "error.esborrar.doc.proces") + ": " + ex.getLocalizedMessage());
		}
		return response;
	}

	/** Aquest mètode genera el document a partir de la plantilla i el descarrega.
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentCodi}/generar", method = RequestMethod.GET)
	public String generar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable String documentCodi,
			Model model) {
		try {
			ArxiuDto generat = expedientDocumentService.generarAmbPlantilla(
					expedientId,
					processInstanceId,
					documentCodi);
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, generat.getNom());
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, generat.getContingut());
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.generar.document.info", new Object[] {ex.getMessage()}));
			logger.error("Error generant el document: " + documentCodi, ex);
			ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
					expedientId,
					processInstanceId,
					documentCodi);
			if (document != null) {
				return "redirect:/modal/v3/expedient/" + expedientId + "/proces/" + processInstanceId + "/document/" + document.getId() + "/update";
			} else {
				return "redirect:/modal/v3/expedient/" + expedientId + "/proces/" + processInstanceId + "/document/new";
			}
		} 
		return "arxiuView";
	}

	@RequestMapping(value = "/{expedientId}/document/{documentStoreId}/signatura/verificar", method = RequestMethod.GET)
	public String verificarDocSignatura(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			@RequestParam(value = "urlVerificacioCustodia", required = false) final String urlVerificacioCustodia,
			ModelMap model) throws ServletException {
		return verificarSignatura(request, expedientId, null, documentStoreId, urlVerificacioCustodia, model);
	}
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/signatura/verificar", method = RequestMethod.GET)
	public String verificarSignatura(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@RequestParam(value = "urlVerificacioCustodia", required = false) final String urlVerificacioCustodia,
			ModelMap model) throws ServletException {
		try {
			if (urlVerificacioCustodia != null && !urlVerificacioCustodia.isEmpty()) {
				model.addAttribute("tittle", getMessage(request, "expedient.document.verif_signatures"));
				model.addAttribute("url", urlVerificacioCustodia);
				return "v3/utils/modalIframe";
			}			
			// Recupera la informació del document i la afegeix al model
			if (processInstanceId == null) {
				ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
				processInstanceId = expedient.getProcessInstanceId();
			}
			ExpedientDocumentDto ed = expedientDocumentService.findOneAmbInstanciaProces(
					expedientId,
					processInstanceId,
					documentStoreId);
			if (ed != null) {
				model.addAttribute(
						"signatura",
						expedientDocumentService.findDocumentAmbId(documentStoreId));
			}			
			model.addAttribute("signatures", expedientService.verificarSignatura(documentStoreId));
			return "v3/expedientTascaTramitacioSignarVerificar";
		} catch(Exception ex) {
			logger.error("Error al verificar la signatura", ex);
			throw new ServletException(ex);
	    }
	}
	
	/** Mètode per redireccionar cal a la pàgina de verificació del CSV. Pot ser que el document no el tingui
	 * ben informat i s'hagi de consultar a l'Arxiu. Si no es pot consultar s'ha de retornar un error.
	 */
	@RequestMapping(value = "/{expedientId}/document/{documentStoreId}/signatura/verificarCsv", method = RequestMethod.GET)
	public String verificarDocCsv(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			@RequestParam(value = "csv", required = false) final String csv,
			ModelMap model) throws ServletException {
		return verificarCsv(request, expedientId, null, documentStoreId, csv, model);
	}
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/signatura/verificarCsv", method = RequestMethod.GET)
	public String verificarCsv(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@RequestParam(value = "csv", required = false) final String csv,
			ModelMap model) throws ServletException {
		String ret = "redirect:/v3/expedient/";
		try {
			if (processInstanceId == null) {
				ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
				processInstanceId = expedient.getProcessInstanceId();
			}
			// La pròpia consulta actualtiza el CSV al documnet
			expedientDocumentService.getArxiuDetall(
					expedientId,
					processInstanceId,
					documentStoreId);
			ExpedientDocumentDto expedientDocument = expedientDocumentService.findOneAmbInstanciaProces(
						expedientId,
						processInstanceId,
						documentStoreId);
			 ret = "redirect:" + expedientDocument.getSignaturaUrlVerificacio();
		} catch(Exception ex) {
			String errMsg = getMessage(request, "expedient.document.verificar.csv.error", new Object[] {ex.getClass(), ex.getMessage()});
			logger.error(errMsg, ex);
			MissatgesHelper.error(request, errMsg);
	    }
		return ret;
	}


	@RequestMapping(value = "/{expedientId}/document/{documentStoreId}/signatura/esborrar", method = RequestMethod.GET)
	@ResponseBody
	public boolean signaturaDocEsborrar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			ModelMap model) throws ServletException {
		return signaturaEsborrar(request, expedientId, null, documentStoreId, model);
	}
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/signatura/esborrar", method = RequestMethod.GET)
	@ResponseBody
	public boolean signaturaEsborrar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,	
			ModelMap model) throws ServletException {
		boolean response = false; 
		try {
			expedientService.deleteSignatura(expedientId, documentStoreId);
			MissatgesHelper.success(request, getMessage(request, "info.signatura.esborrat"));
			response = true; 
		} catch (Exception ex) {
			logger.error(getMessage(request, "error.esborrar.signatura"), ex);
			MissatgesHelper.error(request, getMessage(request, "error.esborrar.signatura") + ": " + ex.getLocalizedMessage());
		}
		return response;
	}

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/registre/verificar", method = RequestMethod.GET)
	public String verificarRegistre(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			ModelMap model) throws ServletException {
		try {
			model.addAttribute(
					"document",
					expedientDocumentService.findOneAmbInstanciaProces(
							expedientId,
							processInstanceId,
							documentStoreId));
			return "v3/expedientDocumentRegistreVerificar";
		} catch(Exception ex) {
			logger.error("Error al verificar la signatura", ex);
			throw new ServletException(ex);
	    }
	}

	/** Mètode per reintentar el processament del document que s'envia al porta signatures des de la modal del
	 * detall de l'enviament. */
	@RequestMapping(
			value="/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/psignaReintentar", 
			method = RequestMethod.GET)
	public String documentPsignaReintentar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@RequestParam(value = "psignaId", required = true) Integer psignaId,
			Model model) {
		// TODO: eliminar la referencia al core 2.6 i passar el mètode processarDocumentPendentPortasignatures al pluginHelper
		if (pluginService.processarDocumentPendentPortasignatures(psignaId)) {
			MissatgesHelper.success(request, getMessage(request, "expedient.psigna.reintentar.ok"));
			return modalUrlTancar(false);
		} else {
			MissatgesHelper.error(request, getMessage(request, "expedient.psigna.reintentar.error"));
			return "redirect:" + request.getHeader("referer");
		}
	}

	@RequestMapping(value = "/document/arxiuMostrar")
	public String arxiuMostrar(
		HttpServletRequest request,
		@RequestParam(value = "token", required = true) String token,
		ModelMap model) {
		ArxiuDto arxiu = null;
		if (token != null) {
			arxiu = expedientDocumentService.findArxiuAmbTokenPerMostrar(token);
		}
		if (arxiu != null) {
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
		}
		return "arxiuView";
	}

	@RequestMapping(value = "/document/arxiuPerSignar")
	public String arxiuPerSignar(
		HttpServletRequest request,
		@RequestParam(value = "token", required = true) String token,
		ModelMap model) throws Exception {
		ArxiuDto arxiu = null;
		if (token != null)
			try {
				arxiu = expedientDocumentService.findArxiuAmbTokenPerSignar(token);
			} catch (SistemaExternException ex) {
				logger.error("Error al obtenir el document a partir del token '" + token + "'", ex);
				MissatgesHelper.error(request,ex.getPublicMessage());
			} catch (Exception ex) {
				logger.error("Error al obtenir el document a partir del token '" + token + "'", ex);
				MissatgesHelper.error(request,ex.getMessage());
			}	
		if (arxiu != null) {
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
		}
		return "arxiuView";
	}
	
	/** Recupera el contingut de tots els documents i crea un comprimit per a la descàrrega.
	 * 
	 */
	@RequestMapping(value = "/{expedientId}/document/descarregarZip", method = RequestMethod.GET)
	public String descarregarZipDocumentacio(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model)  {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, expedient.getIdentificador() + ".zip");
			model.addAttribute(
					ArxiuView.MODEL_ATTRIBUTE_DATA,
					expedientService.getZipDocumentacio(expedientId));
		} catch(Exception e) {
			MissatgesHelper.error(request, getMessage(request, "expedient.document.descarregar.zip.error", new Object[]{ e.getMessage() } ));
		}
		return "arxiuView";
	}

	@RequestMapping(value = "/{expedientId}/document/descarregar", method = RequestMethod.GET)
	public String descarregarDocumentacio(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model)  {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			SessionHelper.SessionManager sessionManager = SessionHelper.getSessionManager(request);
			Set<Long> seleccio = sessionManager.getSeleccioDocuments(expedientId);
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, expedient.getIdentificador() + ".zip");
			model.addAttribute(
					ArxiuView.MODEL_ATTRIBUTE_DATA,
					expedientService.getZipDocumentacio(expedientId, seleccio));
		} catch(Exception e) {
			MissatgesHelper.error(request, getMessage(request, "expedient.document.descarregar.zip.error", new Object[]{ e.getMessage() } ));
		}
		return "arxiuView";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
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
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

	private List<DocumentInfoDto> getDocumentsNoUtilitzats(Long expedientId, String procesId) {
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesId);
		List<DocumentInfoDto> documentsNoUtilitzats = new ArrayList<DocumentInfoDto>();
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
				expedient.getTipus().getId(),
				instanciaProces.getDefinicioProces().getId(),
				true);	// amb herència
		List<ExpedientDocumentDto> documentsInstancia = expedientDocumentService.findAmbInstanciaProces(expedientId, procesId);
		if (documentsInstancia != null && documentsInstancia.size() > 0) {
			// Posa els codis dels documents utilitzats en un Set
			Set<String> codisDocumentsExistents = new HashSet<String>();
			for (ExpedientDocumentDto documentExpedient : documentsInstancia)
				codisDocumentsExistents.add(documentExpedient.getDocumentCodi());
			// Mira quins documents no s'han utilitzat i els retorna
			for(DocumentDto document: documents) 
				if (!codisDocumentsExistents.contains(document.getCodi()) && !document.isPinbalActiu())
					documentsNoUtilitzats.add(toDocumentInfo(document));
		} else {
			for(DocumentDto document: documents)
				if (!document.isPinbalActiu())
					documentsNoUtilitzats.add(toDocumentInfo(document));
		}
		
		return documentsNoUtilitzats;
	}
	
	private List<DocumentInfoDto> getDocumentsPinbal(Long expedientId, String procesId) {
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesId);
		List<DocumentInfoDto> documentsPinbal = new ArrayList<DocumentInfoDto>();
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
				expedient.getTipus().getId(),
				instanciaProces.getDefinicioProces().getId(),
				true);	// amb herència

		for(DocumentDto document: documents)
			if (document.isPinbalActiu())
				documentsPinbal.add(toDocumentInfo(document));
		
		return documentsPinbal;
	}

	private DocumentInfoDto toDocumentInfo(DocumentDto document) {
		DocumentInfoDto resultat = DocumentInfoDto.builder()
				.codi(document.getCodi())
				.documentNom(document.getDocumentNom())
				.plantilla(document.isPlantilla())
				.ntiOrigen(document.getNtiOrigen())
				.ntiEstadoElaboracion(document.getNtiEstadoElaboracion())
				.ntiTipoDocumental(document.getNtiTipoDocumental())
				.generarNomesTasca(document.isGenerarNomesTasca())
				.build();
		resultat.setId(document.getId());
		return resultat;
	}

	private ExpedientDto emplenarModelNti(
			Long expedientId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		model.addAttribute("expedient", expedient);
		if (expedient.isNtiActiu()) {
			ntiHelper.omplirOrigen(model);
			ntiHelper.omplirEstadoElaboracion(model);
			ntiHelper.omplirTipoDocumental(model);
			ntiHelper.omplirTipoFirma(model);
		}
		return expedient;
	}

	/** Obre el formulari per iniciar la firma en passsarel·la web
	 * 
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/firmaPassarela", method = RequestMethod.GET)
	public String firmaPassarelaGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {		
		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);
		boolean potFirmar = true;
		if (document.getArxiuUuid() == null && document.getCustodiaCodi() == null) {
			MissatgesHelper.error(request, getMessage(request, "expedient.document.firmaPassarela.validacio.custodia.codi"));
			potFirmar = false;
		} 
		DocumentExpedientFirmaPassarelaCommand command = new DocumentExpedientFirmaPassarelaCommand();
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		command.setMotiu(getMessage(request, "expedient.document.firmaPassarela.camp.motiu.default", new Object[] {expedient.getNumero()}));
		model.addAttribute("document", document);	
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("documentExpedientFirmaPassarelaCommand", command);
		model.addAttribute("potFirmar", potFirmar);
		return "v3/expedientDocumentFirmaPassarelaForm";
	}
	/** 
	 * Inicia el procés de firma del document i si tot va bé redirigeix cap a la URL retornada pel
	 * portasignatures.
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/firmaPassarela", method = RequestMethod.POST)
	public String firmaPassarelaPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@Validated(FirmaPassarela.class) DocumentExpedientFirmaPassarelaCommand command,
			BindingResult bindingResult,
			Model model) {
		
		if (bindingResult.hasErrors()) {
			ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
					expedientId,
					processInstanceId,
					documentStoreId);
			model.addAttribute("document", document);	
			model.addAttribute("expedientId", expedientId);
			model.addAttribute("documentExpedientFirmaPassarelaCommand", command);
			model.addAttribute("potFirmar", true);
			return "v3/expedientDocumentFirmaPassarelaForm";
		}
		String ret = "v3/expedientDocumentFirmaPassarelaForm";
		try {
			
			ArxiuDto arxiuPerFirmar = expedientDocumentService.arxiuFindAmbDocument(
					expedientId,
					processInstanceId,
					documentStoreId);
			
			String urlReturnToHelium = ((ModalHelper.isRefererUriModal(request)) ? "/modal" : "") 
											+ "/v3/expedient/" + expedientId + "/proces/" + processInstanceId + "/document/" + documentStoreId + "/firmaPassarelaFinal";
			urlReturnToHelium = UrlHelper.getAbsoluteControllerBase(request,"").concat(urlReturnToHelium);

			String procesFirmaUrl = expedientDocumentService.firmaSimpleWebStart(
					aplicacioService.findPersonaActual(),
					arxiuPerFirmar,
					command.getMotiu(),
					command.getLloc() != null ? command.getLloc() : "Illes Balears (HELIUM)",
					urlReturnToHelium);
			
			ret = "redirect:" + procesFirmaUrl;
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"document.controller.firma.passarela.inici.error",
							new Object[] {e.getMessage()}));
		}
		return ret;
	}

	/** Mètode que es crida a la tornada de la firma per passarel·la en el Portasignatures. Si tot va bé es tanca la modal
	 * amb un missatge que ha anat bé.
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/firmaPassarelaFinal", method = RequestMethod.GET)
	public String firmaPassarelaFinal(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@RequestParam(value = "transactionID", required = true) String transactionID,
			Model model) {
				
		String ret = "redirect:/v3/expedient/" + expedientId + "/proces/" + processInstanceId + "/document/" + documentStoreId + "/firmaPassarela";
		try {
			
			FirmaResultatDto firmaResultat =  expedientDocumentService.firmaSimpleWebEnd(transactionID);
			
			if (firmaResultat.getStatus() == StatusEnumDto.OK) {
				
				if (firmaResultat.getFitxerFirmatContingut() == null) {
					MissatgesHelper.error(
							request,
							getMessage(
									request, 
									"document.controller.firma.passarela.final.ok.nofile"));
				} else {
					try {
						expedientDocumentService.processarFirmaClient(
								expedientId,
								processInstanceId,
								documentStoreId,
								firmaResultat.getFitxerFirmatNom(), 
								firmaResultat.getFitxerFirmatContingut());
						
						MissatgesHelper.success(
								request,
								getMessage(
										request, 
										"document.controller.firma.passarela.final.ok"));
						
						ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
								expedientId,
								processInstanceId,
								documentStoreId);
						model.addAttribute("document", document);	
						model.addAttribute("potFirmar", false);
						
						ret = "v3/expedientDocumentFirmaPassarelaForm";
						
					} catch (Exception e) {
						String errMsg = getMessage(
								request, 
								"document.controller.firma.passarela.final.error.validacio",
								new Object[] {ExceptionUtils.getRootCauseMessage(e)});
						logger.error("Error en la signatura del document. " + errMsg, e);
						MissatgesHelper.error(
								request,
								errMsg);
					}
				}
			} else if (firmaResultat.getStatus() == StatusEnumDto.WARNING) {
				MissatgesHelper.warning(
						request,
						getMessage(
								request, 
								"document.controller.firma.passarela.final.warning",
								new Object[] {
										firmaResultat.getMsg()
								}));
				
			} else if (firmaResultat.getStatus() == StatusEnumDto.ERROR) {
				MissatgesHelper.error(
						request,
						getMessage(
								request, 
								"document.controller.firma.passarela.final.error",
								new Object[] {
										firmaResultat.getMsg()
								}));
			}
		} catch(Exception e) {
			String errMsg = "Error no controlat en el procés de firma en passarel·la web: " + e.getMessage();
			MissatgesHelper.error(request, errMsg);
		}
		return ret;
	}
	
	@RequestMapping(value = "/{expedientId}/document/notificarZip", method = RequestMethod.GET)
	public String notificarZip(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) throws ParseException {
		DocumentExpedientNotificarZipCommand command = new DocumentExpedientNotificarZipCommand();	
		ExpedientDto expedientDto = expedientService.findAmbIdAmbPermis(expedientId);	
		String processInstanceId = expedientDto.getProcessInstanceId();
		// Possibles annexos
		String str = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
		command.setTitol("Notificació " + str);

		emplenarModelPossiblesAnnexosNotificacioZip(expedientId, processInstanceId, model);		
		emplenarModelNti(expedientId, model);
		command.setNtiOrigen(NtiOrigenEnumDto.ADMINISTRACIO);
		command.setNtiEstadoElaboracion(NtiEstadoElaboracionEnumDto.ORIGINAL);
		command.setNtiTipoDocumental(NtiTipoDocumentalEnumDto.NOTIFICACIO);
		model.addAttribute("documentExpedientNotificarZipCommand", command);
		return "v3/expedientDocumentNotificarZip";
	}
	
	/** Mètode per afegir al model de la modal de notificar diferents documents els possibles annexos. */
	private void emplenarModelPossiblesAnnexosNotificacioZip(Long expedientId, String processInstanceId, Model model) {

		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		model.addAttribute("expedient", expedient);

		List<ParellaCodiValorDto> annexosNoZip;
		if (ExpedientTipusTipusEnumDto.ESTAT.equals(expedient.getTipus().getTipus())) {
			
			// en el cas d'expedients per estat no hi ha documents en els subprocessos
			List<ExpedientDocumentDto> annexos = expedientDocumentService.findAmbInstanciaProces(expedientId, processInstanceId);		
			annexosNoZip = new ArrayList<ParellaCodiValorDto>();
			for(ExpedientDocumentDto ann : annexos) {
				if (!ann.getArxiuExtensio().contains("zip"))
					annexosNoZip.add(
							new ParellaCodiValorDto(
											String.valueOf(ann.getId()), 
											ann.isAdjunt() ? 
													ann.getAdjuntTitol() + " (adjunt)"
													: ann.getDocumentNom()));
			}
			model.addAttribute("annexos", annexosNoZip);
		} else {
			
			// en el cas d'expedients amb fluxe agrupa els documents per subprocessos
			Map<InstanciaProcesDto, List<ExpedientDocumentDto>> documentsProces = this.getDocumentsArbreProcessos(expedient, "default");
			Map<String, List<ParellaCodiValorDto>> annexosPerProces = new LinkedHashMap<String, List<ParellaCodiValorDto>>();
			List<InstanciaProcesDto> processos = new ArrayList<InstanciaProcesDto>();			
			for (InstanciaProcesDto instanciaProces: documentsProces.keySet()) {
				if (instanciaProces.getInstanciaProcesPareId() == null) {
					// Procés principal
					processos.add(0, instanciaProces);
				} else {
					// Subprocés
					processos.add(instanciaProces);
				}
				annexosNoZip = new ArrayList<ParellaCodiValorDto>();
				for(ExpedientDocumentDto ann : documentsProces.get(instanciaProces)) {
					if (!ann.getArxiuExtensio().contains("zip"))
						annexosNoZip.add(
								new ParellaCodiValorDto(
												String.valueOf(ann.getId()), 
												ann.isAdjunt() ? 
														ann.getAdjuntTitol() + " (adjunt)"
														: ann.getDocumentNom()));
				}
				
				annexosPerProces.put(instanciaProces.getId(), annexosNoZip);
			}
			model.addAttribute("processos", processos);
			model.addAttribute("annexosPerProces", annexosPerProces);
		}
	}

	@RequestMapping(value = "/{expedientId}/document/notificarZip", method = RequestMethod.POST)
	public String notificarZipPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@Validated(NotificarZip.class) DocumentExpedientNotificarZipCommand command,
			BindingResult bindingResult,
			Model model) {
		
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);	
		String processInstanceId = expedient.getProcessInstanceId();
		
		if (bindingResult.hasErrors()) {
			emplenarModelNti(expedientId, model);
			emplenarModelPossiblesAnnexosNotificacioZip(expedientId, processInstanceId, model);		
			return "v3/expedientDocumentNotificarZip";
		}
		try {
			Map<InstanciaProcesDto, List<ExpedientDocumentDto>> documentsProces = this.getDocumentsArbreProcessos(expedient, "default");
			List<Long> annexosId = command.getAnnexos();
			List<ExpedientDocumentDto> annexosPerNotificar = new ArrayList<ExpedientDocumentDto>();
			if (annexosId != null) {
				for (InstanciaProcesDto instanciaProces : documentsProces.keySet()) {
					for(ExpedientDocumentDto document: documentsProces.get(instanciaProces)) {
						if (annexosId.contains(document.getId()))
								annexosPerNotificar.add(document);
					}	
				}
			}
			Long documentStoreId = null;
			byte[] contingut = null;
			if(annexosPerNotificar!=null && !annexosPerNotificar.isEmpty()) {
				contingut = expedientService.getZipPerNotificar(expedientId, annexosPerNotificar);
				String documentCodi = command.getTitol();
				documentStoreId = expedientDocumentService.guardarDocumentProces(expedient.getProcessInstanceId(), null, new Date(), documentCodi+".zip", contingut, annexosPerNotificar);
			}
			return "redirect:/modal/v3/expedient/" + expedientId + "/proces/" + processInstanceId + "/document/" + documentStoreId + "/notificar";	
		} catch(Exception e) {
			String errMsg = getMessage(request, "expedient.document.notificat.zip.error", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}
		return null;
	}
	
	/** Modal per veure el llistat de notificacions relacioandes amb un document.
	 * 
	 * @param request
	 * @param documentId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/notificacions", method = RequestMethod.GET)
	public String notificacions(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		
		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);

		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		List<DadesNotificacioDto> notificacionsDocument = new ArrayList<DadesNotificacioDto>();
		for(DadesNotificacioDto notificacio: expedientService.findNotificacionsNotibPerExpedientId(expedient.getId())) {
			if (documentStoreId.equals(notificacio.getDocumentId())) {
				notificacionsDocument.add(notificacio); 
			} else {
				// Mira dins dels possibles documents continguts en el document zip notificat
				Iterator<DocumentStoreDto> contingutsI = notificacio.getDocumentsDinsZip().iterator();
				boolean enContinguts = false;
				while (!enContinguts && contingutsI.hasNext()) {
					DocumentStoreDto dsContingut = contingutsI.next();
					if (documentStoreId.equals(dsContingut.getId())) {
						notificacionsDocument.add(notificacio); 
						enContinguts = true;
					}
				}
			}
		}
		model.addAttribute("document", document);
		model.addAttribute("expedient", expedient);
		model.addAttribute("notificacions", notificacionsDocument);
		modelAddDocumentsNoms(expedient, notificacionsDocument, model);
		
		return "v3/expedientDocumentNotificacions";
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

	/** Obre el formulari de l'enviament al portafirmes
	 * 
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/enviarPortasignatures", method = RequestMethod.GET)
	public String portasigEnviarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		
		DocumentExpedientEnviarPortasignaturesCommand command = new DocumentExpedientEnviarPortasignaturesCommand();

//		// Abans d'entrar esborra les plantilles que hagi pogut crear anteriorment l'usuari
//		String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
//		this.esborrarPlantillesUsuari(expedientId, processInstanceId, usuari);
		
		this.emplenarModelPortasigEnviar(
				model,
				request, 
				command, 
				expedientId, 
				processInstanceId, 
				documentStoreId);
		
		return "v3/expedientDocumentEnviarPortasignaturesForm";
	}
	
	/** Consulta les plantilles que hagi pogut crear l'usuari i les esborra. */
//	private void esborrarPlantillesUsuari(Long expedientId, String processInstanceId, String usuari) {
//		try {
//			ExpedientDto expedientDto = expedientService.findAmbIdAmbPermis(expedientId);
//			Long expedientTipusId = expedientDto.getTipus().getId();
//			Long definicioProcesId = null;
//			if (!expedientDto.getTipus().isAmbInfoPropia()) {
//				DefinicioProcesDto definicioProcesDto = definicioProcesService.findAmbProcessInstanceId(processInstanceId);
//				definicioProcesId = definicioProcesDto.getId();
//				if (definicioProcesDto.getExpedientTipus() == null) {
//					// Definicio de procés global
//					expedientTipusId = null;
//				}
//			}
//			List<PortafirmesFluxRespostaDto> plantillesUsuari = portafirmesFluxService.recuperarPlantillesDisponibles(
//					expedientTipusId, 
//					definicioProcesId,
//					usuari);
//			for (PortafirmesFluxRespostaDto plantilla : plantillesUsuari) {
//				portafirmesFluxService.esborrarPlantilla(plantilla.getFluxId());				
//			}
//			
//		} catch(Exception e) {
//			logger.error("Error esborrant les plantilles de l'usuari " + usuari);
//		}
//	}

	private void emplenarModelPortasigEnviar(
			Model model,
			HttpServletRequest request,
			DocumentExpedientEnviarPortasignaturesCommand command, 
			Long expedientId,
			String processInstanceId, 
			Long documentStoreId) {

		ExpedientDocumentDto expedientDocumentDto = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);

		boolean potFirmar = true;
		if (expedientDocumentDto.getArxiuUuid() == null && expedientDocumentDto.getCustodiaCodi() == null) {
			MissatgesHelper.error(request, getMessage(request, "expedient.document.firmaPassarela.validacio.custodia.codi"));
			potFirmar = false;
		}
		
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		if(expedientDocumentDto.getDocumentCodi()!=null) {

			if (command != null) {
				DocumentDto documentDto = documentService.findAmbId(
						expedient.getTipus().getId(), 
						expedientDocumentDto.getDocumentId());
				
				command.setPortafirmesPrioritatTipus(PortafirmesPrioritatEnumDto.NORMAL);
				command.setNom(documentDto.getDocumentNom());
				command.setId(documentDto.getDocumentId());
				command.setPortafirmesActiu(documentDto.isPortafirmesActiu());
				if(documentDto.isPortafirmesActiu()) {
					command.setId(documentStoreId);
					command.setPortafirmesFluxTipus(documentDto.getPortafirmesFluxTipus());
					if(documentDto.getPortafirmesFluxTipus().equals(PortafirmesTipusEnumDto.FLUX)) {
						expedientDocumentDto.setPortafirmesFluxId(documentDto.getPortafirmesFluxId());
						command.setPortafirmesEnviarFluxId(documentDto.getPortafirmesFluxId());
						model.addAttribute("portafirmesFluxSeleccionat", documentDto.getPortafirmesFluxId());
						model.addAttribute("portafirmesFluxId", documentDto.getPortafirmesFluxId());
						model.addAttribute("nouFluxDeFirma", documentDto.getPortafirmesFluxId() == null);
					}
					else if(documentDto.getPortafirmesFluxTipus().equals(PortafirmesTipusEnumDto.SIMPLE)) {
						command.setPortafirmesResponsables(documentDto.getPortafirmesResponsables());
						command.setPortafirmesSequenciaTipus(documentDto.getPortafirmesSequenciaTipus());
					}
				}
				command.setMotiu(getMessage(request, "expedient.document.firmaPassarela.camp.motiu.default", new Object[] {expedient.getNumero()}));
				model.addAttribute("documentExpedientEnviarPortasignaturesCommand", command);
			}
		}
		model.addAttribute(
				"fluxtipEnumOptions",
				EnumHelper.getOptionsForEnum(
						PortafirmesTipusEnumDto.class
						,"enum.document.tipus.portafirmes."));
		model.addAttribute(
				"portafirmesSequenciaTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						PortafirmesSimpleTipusEnumDto.class
						,"enum.document.tipus.portafirmes.sequencia."));
		model.addAttribute(
				"portafirmesPrioritatEnumOptions",
				EnumHelper.getOptionsForEnum(
						PortafirmesPrioritatEnumDto.class
						,"enum.document.tipus.portafirmes.prioritat."));
				
		// Possibles annexos
		List<ExpedientDocumentDto> annexos = expedientDocumentService.findAmbInstanciaProces(expedientId, processInstanceId);
		// Treu adjunts, el propi document i els que ja estan al command
		Iterator<ExpedientDocumentDto> docI = annexos.iterator();
		while(docI.hasNext()) {
			ExpedientDocumentDto doc = docI.next();
			if (doc.getId() == documentStoreId
					|| doc.isAdjunt()
					|| (command != null && command.getAnnexos().contains(doc.getId()))) {
				docI.remove();
			}
		}
		model.addAttribute("annexos", annexos);
		model.addAttribute("document", expedientDocumentDto);	
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("potFirmar", potFirmar);
	}

	/** 
	 * Visualitza la modal amb la informació de l'enviament al portasignatures, donant la opció de cancelar-lo. 
	 * portasignatures.
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/pendentSignatura")
	public String portasigDetallPeticio(
			HttpServletRequest request,
			@PathVariable Long expedientId, 
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) throws UnsupportedEncodingException {
		
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		model.addAttribute("expedient", expedient);
		
		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);
		
		model.addAttribute("document", document);	
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("potFirmar", true);
			
		PortasignaturesDto psignaPendentActual = expedientDocumentService.getPortasignaturesByDocumentStoreId(processInstanceId, documentStoreId);
		model.addAttribute("psignaPendentActual", psignaPendentActual);
		if (psignaPendentActual.getTokenId() != null) {
			TokenDto token = expedientTokenService.findById(expedientId, processInstanceId, psignaPendentActual.getTokenId().toString());
			model.addAttribute("token", token);
		}
		if(psignaPendentActual.getDocumentId() != null) {
			String urlMostrarPlantilla = portafirmesFluxService.recuperarUrlViewEstatFluxDeFirmes(psignaPendentActual.getDocumentId());
			model.addAttribute("urlFluxFirmes", urlMostrarPlantilla);
		}
		
		return "v3/expedientDocumentPendentPortasignaturesForm";
	}
	/** 
	 * Inicia el procés de firma del document i si tot va bé redirigeix cap a la URL retornada pel
	 * portasignatures.
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/enviarPortasignatures", method = RequestMethod.POST)
	public String portasigEnviarPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@Validated(EnviarPortasignatures.class) DocumentExpedientEnviarPortasignaturesCommand command,
			BindingResult bindingResult,
			Model model) {
		
		if (bindingResult.hasErrors()) {
//			return "v3/expedientDocumentEnviarPortasignaturesForm";
			for(ObjectError e: bindingResult.getAllErrors()) {
       		 MissatgesHelper.error(
						request, 
						e.getDefaultMessage());
			}
	        MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"error.enviar.portasignatures.validacio"));
       	return "redirect:" + request.getHeader("referer");
		}
		
		if (!bindingResult.hasErrors()) {
			try {
				this.portasigEnviar(command, documentStoreId, expedientId,  processInstanceId);
				
				// En haver enviat esborra les plantilles de l'usuari
//				String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
//				this.esborrarPlantillesUsuari(expedientId, processInstanceId, usuari);

				MissatgesHelper.success(
						request, 
						getMessage(
								request,"expedient.document.enviar.portasignatures.ok"));
				return modalUrlTancar(false);
			} catch(Exception e) {
				String errMsg = getMessage(request, "expedient.document.enviar.portasignatures.error", new Object[] {e.getMessage()});
				logger.error(errMsg, e);
				MissatgesHelper.error(request, errMsg);
				return "redirect:" + request.getHeader("referer");
			}
		}
		this.emplenarModelPortasigEnviar(
				model,
				request, 
				null, // command 
				expedientId, 
				processInstanceId, 
				documentStoreId);
		return "v3/expedientDocumentEnviarPortasignaturesForm";
		
	}
	private void portasigEnviar(DocumentExpedientEnviarPortasignaturesCommand command, Long documentStoreId, Long expedientId, String processInstanceId) {
		
		DocumentStore documentStore = documentHelper.findById(documentStoreId);
		DocumentDto documentDto = expedientDocumentService.findDocumentAmbId(documentStoreId);
		ExpedientDto expedientDto = expedientService.findAmbIdAmbPermis(expedientId);
		// Valida que no sigui ja un document firmat
		if (documentStore.isSignat()) 
			throw new ValidacioException("No es pot enviar a firmar al Portasignatures un document que ja està signat");

		List<Long> annexosId = command.getAnnexos();
		List<DocumentDto> annexos = null;
		if (annexosId != null) {
			annexos = new ArrayList<DocumentDto>();
			for (Long docId: annexosId) {
				DocumentDto docDto = documentHelper.toDocumentDto(
						docId,
						false,
						false,
						true,
						false,
						false, // Per notificar
						false);
				if (docDto != null){
					annexos.add(docDto);
				}
			}
		}

		String portafirmesFluxId = command.getPortafirmesEnviarFluxId()  != null ? 
				command.getPortafirmesEnviarFluxId() 
				: command.getPortafirmesNouFluxId();
		expedientDocumentService.enviarPortasignatures(
				documentDto,
				annexos,
				expedientDto,
				command.getPortafirmesPrioritatTipus() != null ? command.getPortafirmesPrioritatTipus().toString() : PortafirmesPrioritatEnumDto.NORMAL.toString(),
				null, //dataLimit,
				null,//tokenId
				Long.valueOf(processInstanceId),
				null, //transicioOK,
				null, //transicioKO,
				command.getPortafirmesSequenciaTipus(),
				command.getPortafirmesResponsables()!=null ?  command.getPortafirmesResponsables().split(",") : null,
				portafirmesFluxId,
				command.getPortafirmesFluxTipus());
	}
	
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/portasignaturesCancelarEnviament/{documentId}")
	public String portasigCancelarEnviament(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@PathVariable Integer documentId,//${psignaPendentActual.documentId}
			DocumentExpedientCommand command,
			BindingResult bindingResult,
			Model model) {
		
		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("document", document);	
			model.addAttribute("expedientId", expedientId);
			model.addAttribute("documentExpedientCommand", command);
			model.addAttribute("potFirmar", true);
			return "v3/expedientDocumentForm";
		}
		
		try {
			PortasignaturesDto psignaPendentActual = expedientDocumentService.getPortasignaturesByDocumentId(documentId);

			if (psignaPendentActual.getTokenId() == null) {
				expedientDocumentService.portafirmesCancelar(
							psignaPendentActual.getDocumentId());
			} else {
				// TODO: Substituir la crida del core 2.6
				PersonaDto usuariActual = aplicacioService.findPersonaActual();
				String motiu = "Petició de firma cancel·lada des d'Helium per l'usuari " + usuariActual.getCodi() + " " + usuariActual.getNomSencer(); 
				pluginService.processarDocumentCallbackPortasignatures(
							psignaPendentActual.getDocumentId(), 
							true, 
							motiu);
			}
			MissatgesHelper.success(
					request, 
					getMessage(
							request,"expedient.document.portasignatures.enviament.cancelat"));
			return modalUrlTancar(false);
		} catch(Exception e) {
			String errMsg = getMessage(request, "expedient.document.portasignatures.enviament.cancelat.error", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}		
		return "redirect:" + request.getHeader("referer");
	}

	/** Mètode ajax per iniciar l'edició d'un flux del portafirmes des de la modal d'enviament del document al portafirmes amb l'opció de flux de firma. */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/portafirmesFlux/iniciarTransaccio", method = RequestMethod.GET)
	@ResponseBody
	public PortafirmesIniciFluxRespostaDto portasigIniciarTransaccio(
			HttpServletRequest request,
			@RequestParam(value = "nom", required = false) String nom,
			@PathVariable Long expedientId, 
			@PathVariable String processInstanceId, 
			@PathVariable Long documentStoreId,
			Model model) throws UnsupportedEncodingException {
		String urlReturn;
		PortafirmesIniciFluxRespostaDto transaccioResponse = null;
		try {
//			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
//			DefinicioProcesDto definicioProcesDto = definicioProcesService.findAmbProcessInstanceId(processInstanceId);
			urlReturn = UrlHelper.getAbsoluteControllerBase(
					request,
					(ModalHelper.isModal(request) ? "/modal" : "") + "/v3/expedient/" +expedientId+ "/proces/"+processInstanceId+"/document/"+documentStoreId+"/portafirmesFlux/returnurl/");
			String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
			transaccioResponse = portafirmesFluxService.iniciarFluxFirma(
					null, 
					null,
					usuari, 
					urlReturn, 
					true);
		} catch (Exception ex) {
			logger.error("Error al iniciar transacio", ex);
			transaccioResponse = new PortafirmesIniciFluxRespostaDto();
			transaccioResponse.setError(true);
			transaccioResponse.setErrorDescripcio(ex.getMessage());
		}
		return transaccioResponse;
	}
	

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/tancarTransaccio/{idTransaccio}", method = RequestMethod.GET)
	@ResponseBody
	public void portasignaturesTancarTransaccio(
			HttpServletRequest request, 
			@PathVariable String idTransaccio,
			@PathVariable Long expedientId, 
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		portafirmesFluxService.tancarTransaccio(idTransaccio);
	}
	

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/flux/plantilles", method = RequestMethod.GET)
	@ResponseBody
	public List<PortafirmesFluxRespostaDto> portasigPlantillesDisponibles(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		Long definicioProcesId = null;
		if (!expedient.getTipus().isAmbInfoPropia()) {
			DefinicioProcesDto definicioProces = definicioProcesService.findAmbProcessInstanceId(processInstanceId);
			definicioProcesId = definicioProces.getId();
		}
		
		List<PortafirmesFluxRespostaDto> resposta = new ArrayList<PortafirmesFluxRespostaDto>();

		resposta.addAll(portafirmesFluxService.recuperarPlantillesDisponibles(
				expedient.getTipus().getId(),
				definicioProcesId,
				null));

		resposta.addAll(portafirmesFluxService.recuperarPlantillesDisponibles(
				null,
				null,
				SecurityContextHolder.getContext().getAuthentication().getName()));

		return resposta;
	}
	

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/portafirmesFlux/returnurl/{transactionId}", method = RequestMethod.GET)
	public String portasigTransaccioEstat(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			@PathVariable Long processInstanceId, 
			@PathVariable Long documentStoreId, 
			@PathVariable String transactionId, 
			Model model) {
		PortafirmesFluxRespostaDto resposta = portafirmesFluxService.recuperarFluxFirma(transactionId);

		if (resposta.isError() && resposta.getEstat() != null) {
			model.addAttribute(
					"FluxError",
					getMessage(request, "expedient.tipus.document.form.camp.portafirmes.flux.enum." + resposta.getEstat()));
		} else {
			model.addAttribute(
					"FluxCreat",
					getMessage(request, "expedient.tipus.document.form.camp.portafirmes.flux.enum.FINAL_OK"));
			model.addAttribute("fluxId", resposta.getFluxId());
			model.addAttribute("FluxNom", resposta.getNom());
		}
		return "v3/portafirmesModalTancar";
	}


	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/portafirmesFlux/returnurl/", method = RequestMethod.GET)
	public String portasigTransaccioEstat(HttpServletRequest request, Model model) {
		model.addAttribute(
				"FluxCreat",
				getMessage(request, "expedient.tipus.document.form.camp.portafirmes.flux.edicio.enum.FINAL_OK"));
		return "v3/portafirmesModalTancar";
	}	
	
	/** Mètode AJAX per carregar la URL del Portafirmes per mostrar el flux de firma seleccionat des de la modal d'enviament del document per flux. */
	@RequestMapping(value = "/portafirmes/flux/mostrar", method = RequestMethod.GET)
	@ResponseBody
	public PortafirmesIniciFluxRespostaDto portasigMostrarFlux(
			HttpServletRequest request,
			@RequestParam(value = "plantillaId", required = false) String plantillaId,
			Model model) {
		PortafirmesIniciFluxRespostaDto transaccioResponse = null;
		try {
			if (plantillaId != null && !plantillaId.isEmpty()) {
				transaccioResponse = new PortafirmesIniciFluxRespostaDto();
				String urlEdicio = portafirmesFluxService.recuperarUrlMostrarPlantilla(plantillaId);
				transaccioResponse.setUrlRedireccio(urlEdicio);
			}
		} catch (Exception ex) {
			transaccioResponse = new PortafirmesIniciFluxRespostaDto();
			transaccioResponse.setError(true);
			transaccioResponse.setErrorDescripcio(ex.getMessage());
		}
		return transaccioResponse;
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientDocumentController.class);

}
