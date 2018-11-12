package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
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
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NtiHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;

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



	@RequestMapping(value = "/{expedientId}/document", method = RequestMethod.GET)
	public String document(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
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
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);

		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId);
		List<ExpedientDocumentDto> documentsProces = expedientDocumentService.findAmbInstanciaProces(
				expedientId,
				processInstanceId);
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

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/new", method = RequestMethod.GET)
	public String nouGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			Model model) {
		DocumentExpedientCommand command = new DocumentExpedientCommand();
		command.setData(new Date());
		model.addAttribute("documentsNoUtilitzats", getDocumentsNoUtilitzats(expedientId, processInstanceId));
		model.addAttribute("documentExpedientCommand", command);
		model.addAttribute("processInstanceId", processInstanceId);
		model.addAttribute("documentExpedientCommand", command);
		emplenarModelNti(expedientId, model);
		return "v3/expedientDocumentNou";
	}
	@RequestMapping(value="/{expedientId}/proces/{processInstanceId}/document/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@Valid @ModelAttribute DocumentExpedientCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		command.setNtiActiu(expedient.isNtiActiu());
		new DocumentModificarValidator(true).validate(command, bindingResult);
		if (bindingResult.hasErrors()) {
        	model.addAttribute("documentsNoUtilitzats", getDocumentsNoUtilitzats(expedientId, processInstanceId));
    		model.addAttribute("processInstanceId", processInstanceId);
    		emplenarModelNti(expedientId, model);
        	return "v3/expedientDocumentNou";
        }
		byte[] arxiuContingut = IOUtils.toByteArray(
				command.getArxiu().getInputStream());
		String arxiuNom = command.getArxiu().getOriginalFilename();
		if ("##adjuntar_arxiu##".equalsIgnoreCase(command.getDocumentCodi())) {
			expedientDocumentService.createAdjunt(
					expedientId,
					processInstanceId,
					command.getData(),
					command.getNom(),
					arxiuNom,
					arxiuContingut,
					command.getNtiOrigen(),
					command.getNtiEstadoElaboracion(),
					command.getNtiTipoDocumental(),
					command.getNtiIdOrigen());
		} else {
			expedientDocumentService.create(
					expedientId,
					processInstanceId,
					command.getDocumentCodi(),
					command.getData(),
					arxiuNom,
					arxiuContingut,
					command.getNtiOrigen(),
					command.getNtiEstadoElaboracion(),
					command.getNtiTipoDocumental(),
					command.getNtiIdOrigen());
		}
		MissatgesHelper.success(request, getMessage(request, "info.document.guardat") );
		return modalUrlTancar(false);
	}

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/update", method = RequestMethod.GET)
	public String updateGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);
		DocumentExpedientCommand command = new DocumentExpedientCommand();
		command.setDocId(document.getDocumentId());
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
		return "v3/expedientDocumentModificar";
	}

	@RequestMapping(value="/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/update", method = RequestMethod.POST)
	public String updatePost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@Valid @ModelAttribute DocumentExpedientCommand command,
			BindingResult result,
			Model model) throws IOException {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		command.setNtiActiu(expedient.isNtiActiu());
		new DocumentModificarValidator(false).validate(command, result);
		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
    			expedientId,
    			processInstanceId,
    			documentStoreId);
		if (result.hasErrors()) {
			command.setArxiuNom(null);
    		model.addAttribute("processInstanceId", processInstanceId);
    		model.addAttribute("document", document);
    		emplenarModelNti(expedientId, model);
        	return "v3/expedientDocumentModificar";
        }
		byte[] arxiuContingut = IOUtils.toByteArray(
				command.getArxiu().getInputStream());
		String arxiuNom = command.getArxiu().getOriginalFilename();
//		boolean esAdjunt = command.getNom() == null || command.getNom().isEmpty();
		if (!document.isAdjunt()) {
			expedientDocumentService.update(
					expedientId,
					processInstanceId,
					documentStoreId,
					command.getData(),
					arxiuNom,
					(arxiuContingut.length != 0)?arxiuContingut:null,
					command.getNtiOrigen(),
					command.getNtiEstadoElaboracion(),
					command.getNtiTipoDocumental(),
					command.getNtiIdOrigen());
		} else {
			expedientDocumentService.updateAdjunt(
					expedientId,
					processInstanceId,
					documentStoreId,
					command.getData(),
					command.getNom(),
					arxiuNom,
					(arxiuContingut.length != 0)?arxiuContingut:null,
					command.getNtiOrigen(),
					command.getNtiEstadoElaboracion(),
					command.getNtiTipoDocumental(),
					command.getNtiIdOrigen());
		}
		MissatgesHelper.success(request, getMessage(request, "info.document.guardat"));
		return modalUrlTancar(false);
	}

	@RequestMapping(value="/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/descarregar")
	public String descarregar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
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
		ExpedientDocumentDto expedientDocument = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);
		model.addAttribute("expedientDocument", expedientDocument);
		model.addAttribute(
				"arxiuDetall",
				expedientDocumentService.getArxiuDetall(
						expedientId,
						processInstanceId,
						documentStoreId));
		return "v3/expedientDocumentMetadadesNti";
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

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentCodi}/generar")
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
			MissatgesHelper.error(request, getMessage(request, "error.generar.document"));
			logger.error("Error generant el document: " + documentCodi, ex);
			ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
					expedientId,
					processInstanceId,
					documentCodi);
			return "redirect:/modal/v3/expedient/" + expedientId + "/proces/" + processInstanceId + "/document/" + document.getId() + "/modificar";
		} 
		return "arxiuView";
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

		return "redirect:/v3/expedient/" + expedientId;
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

	public class DocumentModificarValidator implements Validator {
		private boolean validarArxiu;
		public DocumentModificarValidator(boolean validarArxiu) {
			super();
			this.validarArxiu = validarArxiu;
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Object.class);
		}
		public void validate(Object command, Errors errors) {
			DocumentExpedientCommand documentExpedientCommand = (DocumentExpedientCommand)command;
			if (validarArxiu && (documentExpedientCommand.getArxiu() == null || documentExpedientCommand.getArxiu().isEmpty())) {
				errors.rejectValue("arxiu", "not.blank");
			}
			if (documentExpedientCommand.isNtiActiu()) {
				if (documentExpedientCommand.getNtiOrigen() == null)
					errors.rejectValue("ntiOrigen", "not.blank");
				if (documentExpedientCommand.getNtiEstadoElaboracion() == null)
					errors.rejectValue("ntiEstadoElaboracion", "not.blank");
				if (documentExpedientCommand.getNtiTipoDocumental() == null)
					errors.rejectValue("ntiTipoDocumental", "not.blank");
			}
 			if ("##adjuntar_arxiu##".equalsIgnoreCase(documentExpedientCommand.getDocumentCodi()) || documentExpedientCommand.getDocumentCodi() == null) {
 				ValidationUtils.rejectIfEmpty(errors, "nom", "not.blank");
 			}
		}
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
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}

	private List<DocumentDto> getDocumentsNoUtilitzats(Long expedientId, String procesId) {
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesId);
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
				expedient.getTipus().getId(),
				instanciaProces.getDefinicioProces().getId());
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
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
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
