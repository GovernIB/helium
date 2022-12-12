package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.*;
import net.conselldemallorca.helium.v3.core.api.dto.document.*;
import net.conselldemallorca.helium.v3.core.api.service.AnotacioService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.JsonResponse;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.util.PdfUtils;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalTipus;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalViaTipus;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientInteressatService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Create;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Update;
import net.conselldemallorca.helium.webapp.v3.command.DocumentNotificacioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.EnumHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NtiHelper;

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
	@Autowired
	private DocumentService documentService;
	// TODO: eliminar la referencia al core 2.6 i passar el mètode processarDocumentPendentPortasignatures al pluginHelper
	@Autowired
	private PluginService pluginService;
	@Autowired
	private NtiHelper ntiHelper;
	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	@Autowired
	private AnotacioService anotacioService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	/** comparador per ordenar documents per codi de document primer i per títol de l'adjunt si són adjunts després.
	 *
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

	@RequestMapping(value = "/{expedientId}/document/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse documentDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {

		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientDocumentService.findDocumentsExpedient(expedientId, paginacioParams),
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
		return documentSelectionTipus(request, expedientId, null, ids, method, model);
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
					if (!seleccio.contains(idu)) {
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

		List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		Map<InstanciaProcesDto, List<ExpedientDocumentDto>> documents = new LinkedHashMap<InstanciaProcesDto, List<ExpedientDocumentDto>>();
		List<PortasignaturesDto> portasignaturesPendent = expedientDocumentService.portasignaturesFindPendents(
				expedientId,
				expedient.getProcessInstanceId());
		// Per a cada instància de procés ordenem les dades per agrupació  
		// (si no tenen agrupació les primeres) i per ordre alfabètic de la etiqueta
		for (InstanciaProcesDto instanciaProces: arbreProcessos) {
			List<ExpedientDocumentDto> documentsInstancia = null;
			if (instanciaProces.getId().equals(expedient.getProcessInstanceId())) {
				documentsInstancia = expedientDocumentService.findAmbInstanciaProces(
						expedientId,
						instanciaProces.getId());
				// Ordena els documents per codi de document i si són annexos i no en tenen llavors van darrera ordenats per nom
				Collections.sort(documentsInstancia, new ExpedientDocumentDtoComparador(sort));	
			}
			
			documents.put(instanciaProces, documentsInstancia);
		}
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

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document", method = RequestMethod.GET)
	public String documentProces(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@RequestParam(defaultValue = "default") String sort,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);

		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId);
		List<ExpedientDocumentDto> documentsProces = expedientDocumentService.findAmbInstanciaProces(
				expedientId,
				processInstanceId);
		// Ordena els documents per codi de document i si són annexos i no en tenen llavors van darrera ordenats per nom
		Collections.sort(documentsProces, new ExpedientDocumentDtoComparador(sort));
		Map<InstanciaProcesDto, List<ExpedientDocumentDto>> documents = new LinkedHashMap<InstanciaProcesDto, List<ExpedientDocumentDto>>();
		List<PortasignaturesDto> portasignaturesPendent = expedientDocumentService.portasignaturesFindPendents(
				expedientId,
				processInstanceId);
		documents.put(instanciaProces, documentsProces);
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
		}
		DocumentExpedientCommand command = new DocumentExpedientCommand();
		command.setExpedientId(expedientId);
		command.setData(new Date());
		command.setValidarArxius(true);
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
		if (processInstanceId == null) {
			processInstanceId = expedient.getProcessInstanceId();
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
							command.getNtiIdOrigen());
					
					MissatgesHelper.success(request, getMessage(request, "info.document.guardat") );
					return modalUrlTancar(false);
				} catch(Exception e) {
					String errMsg = getMessage(request, "info.document.guardat.error", new Object[] {e.getMessage()});
					logger.error(errMsg, e);
					MissatgesHelper.error(request, errMsg);
				}
			}
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
//	@ResponseBody
	public String getDetall(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			Model model) {

		DocumentDetallDto documentDetall = expedientDocumentService.getDocumentDetalls(expedientId, documentStoreId);
		model.addAttribute("detall", documentDetall);
		return "v3/expedientDocumentDetall";
//		// Expedient
//		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
//		// Document
//		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
//				expedientId,
//				expedient.getProcessInstanceId(),
//				documentStoreId);
//
//		// Detalls del document
//		DocumentDetallDto.DocumentDetallDtoBuilder documentDetallBuilder = DocumentDetallDto.builder()
//				.documentNom(document.getDocumentNom())
//				.arxiuNom(document.getArxiuNom())
//				.adjunt(document.isAdjunt())
//				.adjuntTitol(document.getAdjuntTitol())
//				.dataCreacio(document.getDataCreacio())
//				.dataModificacio(document.getDataModificacio())
//				.dataDocument(document.getDataDocument())
//				.notificable(document.isNotificable())
//				.arxiuUuid(document.getArxiuUuid())
//				.ntiCsv(document.getNtiCsv())
//				.nti(expedient.isNtiActiu() && expedient.getArxiuUuid() == null)
//				.arxiu(expedient.isNtiActiu() && expedient.getArxiuUuid() != null)
//				.signat(document.isSignat())
//				.registrat(document.isRegistrat())
//				.deAnotacio(document.getAnotacioId() != null)
//				.notificat(document.isNotificat());
//
//		// Registre
//		if (document.isRegistrat()) {
//			documentDetallBuilder.registreDetall(RegistreDetallDto.builder()
//					.registreOficinaNom(document.getRegistreOficinaNom())
//					.registreData(document.getRegistreData())
//					.registreEntrada(document.isRegistreEntrada())
//					.registreNumero(document.getRegistreNumero())
//					.build());
//		}
//
//		// NTI
//		if (expedient.isArxiuActiu()) {
//			if (!StringUtils.isEmpty(document.getArxiuUuid())) {
//				documentDetallBuilder.arxiuDetall(expedientDocumentService.getArxiuDetall(
//						expedientId,
//						expedient.getProcessInstanceId(),
//						documentStoreId));
//			} else {
//				documentDetallBuilder.errorArxiuNoUuid(true);
//			}
//			if (expedient.getNtiOrgano() == null) {
//				documentDetallBuilder.errorMetadadesNti(true);
//			}
//		}
//
//		// Signatura / Url de verificació
//		if (document.isSignat()) {
//			SignaturaValidacioDetallDto.SignaturaValidacioDetallDtoBuilder signaturaValidacioDetallBuilder = SignaturaValidacioDetallDto.builder().signat(true);
//			if (expedient.isArxiuActiu()) {
//				if (document.getNtiCsv() != null) {
//					signaturaValidacioDetallBuilder.urlVerificacio(document.getSignaturaUrlVerificacio());
//				} else {
//					// La crida a arxiuDetall ha actualitzar el CSV al documnet. Per tant el tornam a consultar
//					ExpedientDocumentDto expedientDocument = expedientDocumentService.findOneAmbInstanciaProces(
//							expedientId,
//							expedient.getProcessInstanceId(),
//							documentStoreId);
//					signaturaValidacioDetallBuilder.urlVerificacio("redirect:" + expedientDocument.getSignaturaUrlVerificacio());
//				}
//			} else {
//				if (!StringUtils.isEmpty(document.getSignaturaUrlVerificacio())) {
//					signaturaValidacioDetallBuilder.urlVerificacio(document.getSignaturaUrlVerificacio());
//				} else {
//					DocumentDto signatura = expedientDocumentService.findDocumentAmbId(documentStoreId);
//					signaturaValidacioDetallBuilder.tokenSignatura(signatura.getTokenSignatura());
//
//					List<RespostaValidacioSignaturaDto> signatures = expedientService.verificarSignatura(documentStoreId);
//					List<SignaturaDetallDto> signaturaValidacioDetall = new ArrayList<SignaturaDetallDto>();
//					for (RespostaValidacioSignaturaDto sign : signatures) {
//						String nomResponsable = null;
//						String nifResponsable = null;
//						if (sign.getDadesCertificat() != null && !sign.getDadesCertificat().isEmpty()) {
//							nomResponsable = sign.getDadesCertificat().get(0).getNombreCompletoResponsable();
//							nifResponsable = sign.getDadesCertificat().get(0).getNifResponsable();
//						}
//						signaturaValidacioDetall.add(SignaturaDetallDto.builder()
//								.estatOk(sign.isEstatOk())
//								.nomResponsable(nomResponsable)
//								.nifResponsable(nifResponsable)
//								.build());
//					}
//					signaturaValidacioDetallBuilder.signatures(signaturaValidacioDetall);
//				}
//			}
//			documentDetallBuilder.signaturaValidacioDetall(signaturaValidacioDetallBuilder.build());
//		}
//
//		// Psigna
//		PortasignaturesDto portasignatures = expedientDocumentService.getPortasignaturesByProcessInstanceAndDocumentStoreId(
//				expedient.getProcessInstanceId(),
//				documentStoreId);
//		if (portasignatures != null) {
//			documentDetallBuilder
//					.psignaPendent(!"PROCESSAT".equals(portasignatures.getEstat()))
//					.psignaDetall(PsignaDetallDto.builder()
//							.documentId(portasignatures.getDocumentId())
//							.dataEnviat(portasignatures.getDataEnviat())
//							.estat(portasignatures.getEstat())
//							.error(portasignatures.isError())
//							.errorProcessant(portasignatures.getErrorProcessant())
//							.motiuRebuig(portasignatures.getMotiuRebuig())
//							.dataProcessamentPrimer(portasignatures.getDataProcessamentPrimer())
//							.dataProcessamentDarrer(portasignatures.getDataProcessamentDarrer())
//							.build());
//		} else {
//			documentDetallBuilder.psignaPendent(false);
//		}
//
//
//		// Anotacio
//		if (document.getAnotacioId() != null) {
//			documentDetallBuilder.deAnotacio(anotacioService.findAmbId(document.getAnotacioId()));
//		}
//
//		// Notificacio
//		if (document.isNotificat()) {
//			// List<DocumentNotificacio> enviaments = documentNotificacioRepository.findByExpedientAndDocumentId(expedient, documentStoreId);
//		}
//		return documentDetallBuilder.build();
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
		caducitat.add(Calendar.DATE, 1);
		command.setCaducitat(caducitat.getTime());
		command.setEnviamentTipus(EnviamentTipusEnumDto.NOTIFICACIO);
		command.setIdioma(IdiomaEnumDto.CA);
		model.addAttribute("documentNotificacioCommand", command);
		
		ExpedientDocumentDto document = this.emplenarModelNotificacioDocument(expedientId, processInstanceId, documentStoreId, model);
		
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
			DadesNotificacioDto dadesNotificacioDto = conversioTipusHelper.convertir(
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
						dadesNotificacioDto,
						interessatId,
						documentNotificacioCommand.getRepresentantId());
				// Missatge del resultat
				if (! dadesNotificacio.getError())
					MissatgesHelper.success(request, getMessage(request, "info.document.notificat", new Object[] {interessat.getFullInfo()}));
				else
					MissatgesHelper.warning(request, getMessage(request, "info.document.notificar.avis", new Object[] {interessat.getFullInfo(), dadesNotificacio.getErrorDescripcio()}));
			}
		} catch(Exception e) {
			String errMsg = getMessage(request, "info.document.notificar.error", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
			this.emplenarModelNotificacioDocument(expedientId, processInstanceId, documentStoreId, model);
			return "v3/expedientDocumentNotificar";
		}
		return modalUrlTancar(false);
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
		try {
			if (processInstanceId == null) {
				ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
				processInstanceId = expedient.getProcessInstanceId();
			}
			ArxiuDto arxiu = expedientDocumentService.arxiuFindAmbDocument(
					expedientId,
					processInstanceId,
					documentStoreId);
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
			MissatgesHelper.error(request, e.getPublicMessage());
			model.addAttribute("pipellaActiva", "documents");
			return new JsonResponse(true, e.getMessage());
		}
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

	/*
	 * retorna un JSON de amb la info del document
	 */
	@RequestMapping(
			value="/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/psignainfo", 
			method = RequestMethod.GET)
	@ResponseBody
	public Object psignaInfo(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId) {
		Object psignaInfo = expedientDocumentService.findPortasignaturesInfo(expedientId, processInstanceId, documentStoreId);

		return psignaInfo;
	}

	/** Mètode per reintentar el processament del document que s'envia al porta signatures. */
	@RequestMapping(value = "/{expedientId}/document/{documentStoreId}/psignaReintentar/{psignaDocId}", method = RequestMethod.GET)
	public String documentPsignaReintentarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			@PathVariable Integer psignaDocId,
			ModelMap model) throws ServletException {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			model.addAttribute("expedientId", expedientId);
			model.addAttribute("portasignatures", expedientDocumentService.getPortasignaturesByDocumentId(psignaDocId));
			model.addAttribute("document", expedientDocumentService.findOneAmbInstanciaProces(expedientId, expedient.getProcessInstanceId(), documentStoreId));
			return "v3/expedientDocumentPsignaReprocessar";
		} catch(Exception ex) {
			logger.error("Error al verificar la signatura", ex);
			throw new ServletException(ex);
		}
	}
	@RequestMapping(value="/{expedientId}/document/{documentStoreId}/psignaReintentar/{psignaId}", method = RequestMethod.POST)
	@ResponseBody
	public boolean documentPsignaReintentar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Integer psignaId,
			Model model) {
		boolean response = false;
		if (pluginService.processarDocumentPendentPortasignatures(psignaId)) {
			MissatgesHelper.success(request, getMessage(request, "expedient.psigna.reintentar.ok"));
			return true;
		} else {
			MissatgesHelper.error(request, getMessage(request, "expedient.psigna.reintentar.error"));
		}
		return response;
	}
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
		if (pluginService.processarDocumentPendentPortasignatures(psignaId))
			MissatgesHelper.success(request, getMessage(request, "expedient.psigna.reintentar.ok"));
		else
			MissatgesHelper.error(request, getMessage(request, "expedient.psigna.reintentar.error"));

		return "redirect:/v3/expedient/" + expedientId + "?pipellaActiva=documents";
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

	private List<DocumentDto> getDocumentsNoUtilitzats(Long expedientId, String procesId) {
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesId);
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
				expedient.getTipus().getId(),
				instanciaProces.getDefinicioProces().getId(),
				true);	// amb herència
		List<ExpedientDocumentDto> documentsInstancia = expedientDocumentService.findAmbInstanciaProces(expedientId, procesId);
		if (documentsInstancia != null && documentsInstancia.size() > 0) {
			List<DocumentDto> documentsNoUtilitzats = new ArrayList<DocumentDto>();
			// Posa els codis dels documents utilitzats en un Set
			Set<String> codisDocumentsExistents = new HashSet<String>();
			for (ExpedientDocumentDto documentExpedient : documentsInstancia)
				codisDocumentsExistents.add(documentExpedient.getDocumentCodi());
			// Mira quins documents no s'han utilitzat i els retorna
			for(DocumentDto document: documents) 
				if (!codisDocumentsExistents.contains(document.getCodi()))
					documentsNoUtilitzats.add(document);
			return documentsNoUtilitzats;
		} else {
			return documents;
		}
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

	private static final Log logger = LogFactory.getLog(ExpedientDocumentController.class);

}
