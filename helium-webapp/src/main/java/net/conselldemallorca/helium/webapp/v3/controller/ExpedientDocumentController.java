package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
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
	private ExpedientService expedientService;



	@RequestMapping(value = "/{expedientId}/document", method = RequestMethod.GET)
	public String document(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		Map<InstanciaProcesDto, List<ExpedientDocumentDto>> documents = new LinkedHashMap<InstanciaProcesDto, List<ExpedientDocumentDto>>();
		// Per a cada instància de procés ordenem les dades per agrupació  
		// (si no tenen agrupació les primeres) i per ordre alfabètic de la etiqueta
		for (InstanciaProcesDto instanciaProces: arbreProcessos) {
			List<ExpedientDocumentDto> documentsInstancia = null;
			if (instanciaProces.getId().equals(expedient.getProcessInstanceId())) {
				documentsInstancia = expedientService.findDocumentsPerInstanciaProces(expedientId, instanciaProces.getId());
			}
			documents.put(instanciaProces, documentsInstancia);
		}
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("documents",documents);
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientPerPipella(
					request,
					expedientId,
					model,
					"documents");
		}
		return "v3/expedientDocument";
	}

	@RequestMapping(value = "/{expedientId}/document/{processInstanceId}", method = RequestMethod.GET)
	public String documentProces(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId);
		List<ExpedientDocumentDto> dadesInstancia = expedientService.findDocumentsPerInstanciaProces(expedientId, instanciaProces.getId());
		Map<InstanciaProcesDto, List<ExpedientDocumentDto>> documents = new LinkedHashMap<InstanciaProcesDto, List<ExpedientDocumentDto>>();
		documents.put(instanciaProces, dadesInstancia);
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("documents",documents);
		return "v3/procesDocuments";
	}

	@RequestMapping(value = "/{expedientId}/document/{processInstanceId}/{documentCodi}/generar", method = RequestMethod.GET)
	public String documentGenerarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable String documentCodi,
			Model model) {
		try {
			ArxiuDto generat = expedientService.generarDocumentAmbPlantillaProces(
					expedientId,
					processInstanceId,
					documentCodi);
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, generat.getNom());
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, generat.getContingut());
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.generar.document"));
			logger.error("Error generant el document: " + documentCodi, ex);
			Long documentStoreId = expedientService.findDocumentStorePerInstanciaProcesAndDocumentCodi(processInstanceId, documentCodi);
			return "redirect:/modal/v3/expedient/" + expedientId + "/document/" + processInstanceId + "/" + documentStoreId + "/modificar";
//			return modalUrlTancar(false);
		} 
		return "arxiuView";
	}

	@RequestMapping(value = "/{expedientId}/nouDocument", method = RequestMethod.GET)
	public String nouDocumentGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(value = "processInstanceId", required = true) String processInstanceId,
			Model model) {
		DocumentExpedientCommand command = new DocumentExpedientCommand();
		command.setData(new Date());
		model.addAttribute("processInstanceId", processInstanceId);
		model.addAttribute("documentExpedientCommand", command);
		return "v3/expedientDocumentNou";
	}

	@RequestMapping(value = "/{expedientId}/verificarSignatura/{processInstanceId}/{documentStoreId}/{documentCodi}", method = RequestMethod.GET)
	public String verificarSignatura(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@PathVariable String documentCodi,
			@RequestParam(value = "urlVerificacioCustodia", required = false) final String urlVerificacioCustodia,
			ModelMap model) throws ServletException {
		try {
			if (urlVerificacioCustodia != null && !urlVerificacioCustodia.isEmpty()) {
				model.addAttribute("tittle", getMessage(request, "expedient.document.verif_signatures"));
				model.addAttribute("url", urlVerificacioCustodia);
				return "v3/utils/modalIframe";
			}
			model.addAttribute(
					"signatura",
					expedientService.findDocumentPerInstanciaProces(
							expedientId,
							processInstanceId,
							documentStoreId,
							documentCodi));
			model.addAttribute("signatures", expedientService.verificarSignatura(documentStoreId));
			return "v3/expedientTascaTramitacioSignarVerificar";
		} catch(Exception ex) {
			logger.error("Error al verificar la signatura", ex);
			throw new ServletException(ex);
	    }
	}

	@RequestMapping(value = "/{expedientId}/verificarRegistre/{processInstanceId}/{documentStoreId}/{documentCodi}", method = RequestMethod.GET)
	public String verificarRegistre(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@PathVariable String documentCodi,	
			ModelMap model) throws ServletException {
		try {
			model.addAttribute(
					"document",
					expedientService.findDocumentPerInstanciaProces(
							expedientId,
							processInstanceId,
							documentStoreId,
							documentCodi));
			return "v3/expedientDocumentRegistreVerificar";
		} catch(Exception ex) {
			logger.error("Error al verificar la signatura", ex);
			throw new ServletException(ex);
	    }
	}
	
	@RequestMapping(value="/{expedientId}/documentAdjuntar", method = RequestMethod.POST)
	public String documentModificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(value = "accio", required = true) String accio,
			@ModelAttribute DocumentExpedientCommand command, 
			@RequestParam(value = "arxiu", required = false) final CommonsMultipartFile arxiu,	
			@RequestParam(value = "processInstanceId", required = true) String processInstanceId,
			BindingResult result, 
			SessionStatus status, 
			Model model) {
		try {
			new DocumentModificarValidator().validate(command, result);
			if (result.hasErrors() || arxiu == null || arxiu.isEmpty()) {
	        	if (arxiu == null || arxiu.isEmpty()) {
		        	MissatgesHelper.error(request, getMessage(request, "error.especificar.document"));				        	
		        }
	    		model.addAttribute("processInstanceId", processInstanceId);
	        	return "v3/expedientDocumentNou";
	        }
			
			byte[] contingutArxiu = IOUtils.toByteArray(arxiu.getInputStream());
			String nomArxiu = arxiu.getOriginalFilename();
			command.setNomArxiu(nomArxiu);
			command.setContingut(contingutArxiu);

			expedientService.crearModificarDocument(expedientId, processInstanceId, null, command.getNom(), command.getNomArxiu(), command.getDocId(), command.getContingut(), command.getData());
			MissatgesHelper.success(request, getMessage(request, "info.document.guardat") );
        } catch (Exception ex) {
			logger.error("No s'ha pogut crear el document: expedientId: " + expedientId, ex);
			MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + ": " + ex.getLocalizedMessage());
        }
		return modalUrlTancar(false);
	}
	
	@RequestMapping(value = "/{expedientId}/document/{processInstanceId}/{documentStoreId}/modificar", method = RequestMethod.GET)
	public String documentModificarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		ExpedientDocumentDto document = expedientService.findDocumentPerDocumentStoreId(
				expedientId,
				processInstanceId,
				documentStoreId);
		DocumentExpedientCommand command = new DocumentExpedientCommand();
		command.setDocId(document.getDocumentId());
		if (document.isAdjunt())
			command.setNom(document.getAdjuntTitol());
		else
			command.setNom(document.getDocumentNom());
		command.setCodi(document.getDocumentCodi());
		command.setData(document.getDataDocument());
		model.addAttribute("documentExpedientCommand", command);
		model.addAttribute("document", document);	
		model.addAttribute("expedientId", expedientId);
		return "v3/expedientDocumentModificar";
	}
	

	@RequestMapping(value="/{expedientId}/document/{processInstanceId}/{documentStoreId}/modificar", method = RequestMethod.POST)
	public String documentModificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@ModelAttribute DocumentExpedientCommand command, 
			@RequestParam(value = "modificarArxiu", required = true) boolean modificarArxiu,
			@RequestParam(value = "arxiu", required = false) final CommonsMultipartFile arxiu,	
			@RequestParam(value = "accio", required = true) String accio,
			BindingResult result, 
			SessionStatus status, 
			Model model) {
		try {
			if (!arxiu.isEmpty()) {
				byte[] contingutArxiu = IOUtils.toByteArray(arxiu.getInputStream());
				String nomArxiu = arxiu.getOriginalFilename();
				command.setNomArxiu(nomArxiu);
				command.setContingut(contingutArxiu);
			}
			new DocumentModificarValidator().validate(command, result);
			boolean docAdjunto = !(modificarArxiu && arxiu.isEmpty());
			if (result.hasErrors() || !docAdjunto) {
	        	if (!docAdjunto) {
	        		model.addAttribute("modificarArxiu", modificarArxiu);
		        	MissatgesHelper.error(request, getMessage(request, "error.especificar.document"));				        	
		        }
	        	ExpedientDocumentDto document = expedientService.findDocumentPerInstanciaProces(
	        			expedientId,
	        			processInstanceId,
	        			documentStoreId,
	        			command.getCodi());
	    		model.addAttribute("documentExpedientCommand", command);
	    		model.addAttribute("processInstanceId", processInstanceId);
	    		model.addAttribute("document", document);		
	        	return "v3/expedientDocumentModificar";
	        }
			expedientService.crearModificarDocument(
					expedientId,
					processInstanceId,
					documentStoreId,
					command.getNom(),
					command.getNomArxiu(),
					command.getDocId(),
					command.getContingut(),
					command.getData());
			MissatgesHelper.success(request, getMessage(request, "info.document.guardat"));
        } catch (Exception ex) {
			logger.error("No s'ha pogut guardar el document: expedientId: " + expedientId + " : documentStoreId : " + documentStoreId, ex);
			MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + ": " + ex.getLocalizedMessage());
        }
		return modalUrlTancar(false);
	}
	
	@RequestMapping(value="/{expedientId}/document/{processInstanceId}/{documentStoreId}/descarregar", method = RequestMethod.GET)
	public String documentDesacarregarPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		ArxiuDto arxiu = expedientService.getArxiuPerDocument(expedientId, documentStoreId);
		if (arxiu != null) {
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
		}
		return "arxiuView";
	}
	
	@RequestMapping(value = "/{expedientId}/document/{processInstanceId}/{documentStoreId}/esborrar", method = RequestMethod.GET)
	@ResponseBody
	public boolean documentProcesEsborrar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,	
			ModelMap model) throws ServletException {
		boolean response = false; 
		try {
			expedientService.esborrarDocument(
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

	@RequestMapping(value = "/{expedientId}/document/{documentStoreId}/signaturaEsborrar", method = RequestMethod.GET)
	@ResponseBody
	public boolean signaturaEsborrar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
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

	@RequestMapping(value = "/document/arxiuMostrar")
	public String arxiuMostrar(
		HttpServletRequest request,
		@RequestParam(value = "token", required = true) String token,
		ModelMap model) {
		ArxiuDto arxiu = null;
		if (token != null)
			arxiu = expedientService.arxiuDocumentPerMostrar(token);
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
				arxiu = expedientService.arxiuDocumentPerSignar(token);
			} catch (Exception ex) {
				logger.error("Error al obtenir el document a partir del token '" + token + "'");
				throw ex;
			}	
		if (arxiu != null) {
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
		}
		return "arxiuView";
	}

	@RequestMapping(value = "/{expedientId}/document/{documentId}/descarregar", method = RequestMethod.GET)
	public String documentDescarregar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentId,
			Model model) {
		ArxiuDto arxiu = expedientService.getArxiuPerDocument(
					expedientId,
					documentId);
		model.addAttribute(
				ArxiuView.MODEL_ATTRIBUTE_FILENAME,
				arxiu.getNom());
		model.addAttribute(
				ArxiuView.MODEL_ATTRIBUTE_DATA,
				arxiu.getContingut());
		return "arxiuView";
	}

	public class DocumentModificarValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Object.class);
		}
		public void validate(Object command, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "nom", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "data", "not.blank");
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

	private static final Log logger = LogFactory.getLog(ExpedientDocumentController.class);

}
