/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientDocumentController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;

	@RequestMapping(value = "/{expedientId}/document", method = RequestMethod.GET)
	public String documents(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientPerPipella(
					request,
					expedientId,
					model,
					"documents",
					expedientService);
		}
		model.addAttribute(
				"expedient",
				expedientService.findAmbId(expedientId));
		model.addAttribute(
				"documents",
				expedientService.findDocumentsPerInstanciaProces(
						expedientId,
						null));
		return "v3/expedientDocument";
	}

	@RequestMapping(value = "/{expedientId}/verificarSignatura/{documentStoreId}/{docCodi}", method = RequestMethod.GET)
	public String verificarSignatura(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			@PathVariable String docCodi,			
			ModelMap model) throws ServletException {
		try {
			model.addAttribute("signatura", expedientService.findDocumentPerInstanciaProcesDocumentStoreId(expedientId, documentStoreId, docCodi));
			model.addAttribute("signatures", expedientService.verificarSignatura(documentStoreId));
			return "v3/expedientTascaTramitacioSignarVerificar";
		} catch(Exception ex) {
			logger.error("Error al verificar la signatura", ex);
			throw new ServletException(ex);
	    }
	}
	
	@RequestMapping(value = "/{expedientId}/documentEsborrar/{documentStoreId}/{docCodi}", method = RequestMethod.POST)
	@ResponseBody
	public boolean documentProcesEsborrar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			@PathVariable String docCodi,			
			ModelMap model) throws ServletException {
		boolean response = false; 
		try {
			expedientService.esborrarDocument(expedientId, documentStoreId, docCodi);
			MissatgesHelper.info(request, getMessage(request, "info.doc.proces.esborrat") );
			response = true; 
		} catch (Exception ex) {
			logger.error(getMessage(request, "error.esborrar.doc.proces"), ex);
			MissatgesHelper.error(request, getMessage(request, "error.esborrar.doc.proces") + ": " + ex.getLocalizedMessage());
		}
		return response;
	}

	@RequestMapping(value = "/{expedientId}/signaturaEsborrar/{documentStoreId}", method = RequestMethod.POST)
	@ResponseBody
	public boolean signaturaEsborrar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,	
			ModelMap model) throws ServletException {
		boolean response = false; 
		try {
			expedientService.deleteSignatura(expedientId, documentStoreId);
			MissatgesHelper.info(request, getMessage(request, "info.signatura.esborrat") );
			response = true; 
		} catch (Exception ex) {
			logger.error(getMessage(request, "error.esborrar.signatura"), ex);
			MissatgesHelper.error(request, getMessage(request, "error.esborrar.signatura") + ": " + ex.getLocalizedMessage());
		}
		return response;
	}
	
//	@ModelAttribute("command")
//	public DocumentExpedientCommand populateCommand(
//			HttpServletRequest request,
//			@RequestParam(value = "id") String id,
//			@RequestParam(value = "adjuntId", required = false) String adjuntId) {
//		Entorn entorn = getEntornActiu(request);
//		if (entorn != null) {
//			DocumentExpedientCommand command = new DocumentExpedientCommand();
//			if (adjuntId == null)
//				command.setData(new Date());
//			return command;
//		}
//		return null;
//	}

//	@RequestMapping(value = "/expedient/documentAdjuntForm", method = RequestMethod.GET)
//	public String formGet(
//			HttpServletRequest request,
//			@RequestParam(value = "id", required = false) String id,
//			ModelMap model) {
//		Entorn entorn = getEntornActiu(request);
//		if (entorn != null) {
//			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
//			if (potModificarOReassignarExpedient(expedient)) {
//				return "expedient/documentAdjuntForm";
//			} else {
//				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
//			}
//			return "redirect:/expedient/consulta.html";
//		} else {
//			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec") );
//			return "redirect:/index.html";
//		}
//	}
	
//	@RequestMapping(value = "/expedient/documentAdjuntForm", method = RequestMethod.POST)
//	public String formPost(
//			HttpServletRequest request,
//			@RequestParam(value = "id", required = false) String id,
//			@RequestParam(value = "submit", required = false) String submit,
//			@RequestParam("contingut") final MultipartFile multipartFile,
//			@ModelAttribute("command") DocumentExpedientCommand command,
//			BindingResult result,
//			SessionStatus status,
//			ModelMap model) {
//		Entorn entorn = getEntornActiu(request);
//		if (entorn != null) {
//			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
//			if (potModificarOReassignarExpedient(expedient)) {
//				if ("submit".equals(submit) || submit.length() == 0) {
//					new DocumentAdjuntCrearValidator().validate(command, result);
//			        if (result.hasErrors())
//			        	return "expedient/documentAdjuntForm";
//			        String nomArxiu = "arxiu";
//			        if (multipartFile.getSize() > 0) {
//						try {
//							nomArxiu = multipartFile.getOriginalFilename();
//						} catch (Exception ignored) {}
//					}
//			        try {
//			        	documentService.guardarAdjunt(
//				        		id,
//				        		null,
//				        		command.getNom(),
//				        		command.getData(),
//				        		nomArxiu,
//				        		(multipartFile.getSize() > 0) ? command.getContingut() : null);
//						MissatgesHelper.error(request, getMessage(request, "info.document.adjuntat") );
//						status.setComplete();
//			        } catch (Exception ex) {
//			        	Long entornId = entorn.getId();
//						String numeroExpedient = id;
//						logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+numeroExpedient+" No s'ha pogut crear el document adjunt", ex);
//			        	MissatgesHelper.error(request, getMessage(request, "error.proces.peticio"), ex.getLocalizedMessage());
//			        }
//				}
//				return "redirect:/expedient/documents.html?id=" + id;
//			} else {
//				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
//				return "redirect:/expedient/consulta.html";
//			}
//		} else {
//			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec") );
//			return "redirect:/index.html";
//		}
//	}
	
//	@ModelAttribute("command")
//	public DocumentExpedientCommand populateCommand(
//			HttpServletRequest request,
//			@RequestParam(value = "id", required = true) String id,
//			@RequestParam(value = "docId", required = true) Long docId) {
//		Entorn entorn = getEntornActiu(request);
//		if (entorn != null) {
//			DocumentExpedientCommand command = new DocumentExpedientCommand();
//			DocumentDto dto = documentService.documentInfo(docId);
//			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(id, false, false, false);
//			Document document = dissenyService.findDocumentAmbDefinicioProcesICodi(
//					instanciaProces.getDefinicioProces().getId(),
//					dto.getDocumentCodi());
//			command.setDocId(docId);
//			if (dto.isAdjunt())
//				command.setNom(dto.getAdjuntTitol());
//			else
//				command.setNom(document.getNom());
//			command.setData(dto.getDataDocument());
//			return command;
//		}
//		return null;
//	}

//	@RequestMapping(value = "/expedient/documentModificar", method = RequestMethod.GET)
//	public String documentModificarGet(
//			HttpServletRequest request,
//			@RequestParam(value = "id", required = true) String id,
//			@RequestParam(value = "docId", required = true) Long docId,
//			ModelMap model) {
//		Entorn entorn = getEntornActiu(request);
//		if (entorn != null) {
//			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
//			if (potModificarExpedient(expedient)) {
//				DocumentDto doc = documentService.documentInfo(docId);
//				if (!doc.isSignat()) {
//					model.addAttribute("expedient", expedient);
//					model.addAttribute("document", doc);
//					model.addAttribute(
//							"documentDisseny",
//							doc.isAdjunt() ? dissenyService.getDocumentStoreById(doc.getId()) : dissenyService.getDocumentById(doc.getDocumentId()));
//					return "expedient/documentForm";
//				} else {
//					MissatgesHelper.error(request, getMessage(request, "error.modificar.doc.signat") );
//					return "redirect:/expedient/documents.html?id=" + id;
//				}
//			} else {
//				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
//			}
//			return "redirect:/expedient/consulta.html";
//		} else {
//			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec") );
//			return "redirect:/index.html";
//		}
//	}
	
//	@RequestMapping(value = "/expedient/documentModificar", method = RequestMethod.POST)
//	public String documentModificarPost(
//			HttpServletRequest request,
//			@RequestParam(value = "id", required = true) String id,
//			@RequestParam(value = "submit", required = false) String submit,
//			@RequestParam("contingut") final MultipartFile multipartFile,
//			@ModelAttribute("command") DocumentExpedientCommand command,
//			BindingResult result,
//			SessionStatus status,
//			ModelMap model) {
//		Entorn entorn = getEntornActiu(request);
//		if (entorn != null) {
//			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
//			if (potModificarExpedient(expedient)) {
//				if ("submit".equals(submit) || submit.length() == 0) {
//					new DocumentModificarValidator().validate(command, result);
//					boolean docAdjunto = !("deleted".equals(request.getParameter("contingut_deleted")) && command.getContingut().length == 0);
//			        if (result.hasErrors() || !docAdjunto) {
//			        	DocumentDto document = documentService.documentInfo(command.getDocId());
//				        if (!docAdjunto) {
//				        	document.setTokenSignatura(null);
//				        	MissatgesHelper.error(request, getMessage(request, "error.especificar.document"));				        	
//				        }
//			        	model.addAttribute("expedient", expedient);
//						model.addAttribute(
//								"document",
//								document);
//			        	return "expedient/documentForm";
//			        }
//					try {
//						DocumentDto doc = documentService.documentInfo(command.getDocId());
//						if (!doc.isAdjunt()) {
//							documentService.guardarDocumentProces(
//									id,
//									doc.getDocumentCodi(),
//									null,
//									command.getData(),
//									(multipartFile.getSize() > 0) ? multipartFile.getOriginalFilename() : doc.getArxiuNom(),
//									(multipartFile.getSize() > 0) ? command.getContingut() : doc.getArxiuContingut(),
//									false);
//						} else {
//							documentService.guardarAdjunt(
//									id,
//									doc.getAdjuntId(),
//									command.getNom(),
//									command.getData(),
//									(multipartFile.getSize() > 0) ? multipartFile.getOriginalFilename() : doc.getArxiuNom(),
//									(multipartFile.getSize() > 0) ? command.getContingut() : doc.getArxiuContingut());
//						}
//						MissatgesHelper.error(request, getMessage(request, "info.document.guardat") );
//			        } catch (Exception ex) {
//			        	Long entornId = entorn.getId();
//						String numeroExpedient = id;
//						logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+numeroExpedient+" No s'ha pogut guardar el document", ex);
//			        	MissatgesHelper.error(request, getMessage(request, "error.proces.peticio"), ex.getLocalizedMessage());
//			        }
//				}
//				return "redirect:/expedient/documents.html?id=" + id;
//			} else {
//				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
//				return "redirect:/expedient/consulta.html";
//			}
//		} else {
//			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec") );
//			return "redirect:/index.html";
//		}
//	}

//	@RequestMapping(value = "/expedient/documentGenerar", method = RequestMethod.GET)
//	public String documentGenerarGet(
//			HttpServletRequest request,
//			@RequestParam(value = "id", required = true) String id,
//			@RequestParam(value = "docId", required = true) Long docId,
//			@RequestParam(value = "data", required = false) Date data,
//			ModelMap model) {
//		Entorn entorn = getEntornActiu(request);
//		if (entorn != null) {
//			try {
//				DocumentDto doc = documentService.documentInfo(docId);
//				DocumentDto generat = documentService.generarDocumentPlantilla(
//						entorn.getId(),
//						doc.getDocumentId(),
//						null,
//						id,
//						(data != null) ? data : new Date(),
//						false);
//				if (generat != null) {
//					model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, generat.getArxiuNom());
//					model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, generat.getArxiuContingut());
//				}
//				return "arxiuView";
//			} catch (Exception ex) {
//				MissatgesHelper.error(request, getMessage(request, "error.generar.document"), ex.getLocalizedMessage());
//	        	logger.error("Error generant el document " + docId + " per la instància de procés " + id, ex);
//	        	return "redirect:/expedient/documentModificar.html?id=" + id + "&docId=" + docId;
//			}
//		} else {
//			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec") );
//			return "redirect:/index.html";
//		}
//	}

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

	private static final Log logger = LogFactory.getLog(ExpedientDocumentController.class);
}
