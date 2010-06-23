/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.model.hibernate.Document;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;
import net.conselldemallorca.helium.security.permission.ExtendedPermission;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;



/**
 * Controlador per la gestió dels documents d'una definició de procés
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class DefinicioProcesDocumentController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;
	private Validator annotationValidator;



	@Autowired
	public DefinicioProcesDocumentController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("campsTipusData")
	public List<Camp> populateCampsTipusData(
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "definicioProces", required = false) Long definicioProces) {
		if (definicioProcesId != null)
			return dissenyService.findCampAmbDefinicioProcesITipus(definicioProcesId, TipusCamp.DATE);
		if (definicioProces != null)
			return dissenyService.findCampAmbDefinicioProcesITipus(definicioProces, TipusCamp.DATE);
		return null;
	}

	@ModelAttribute("command")
	public Document populateCommand(
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "definicioProces", required = false) Long definicioProces) {
		if (id != null) {
			Document vell = dissenyService.getDocumentById(id);
			Document document = new Document();
			document.setId(id);
			document.setCodi(vell.getCodi());
			document.setNom(vell.getNom());
			document.setArxiuNom(vell.getArxiuNom());
			document.setDescripcio(vell.getDescripcio());
			document.setPlantilla(vell.isPlantilla());
			document.setContentType(vell.getContentType());
			document.setCustodiaCodi(vell.getCustodiaCodi());
			document.setTipusDocPortasignatures(vell.getTipusDocPortasignatures());
			document.setDefinicioProces(vell.getDefinicioProces());
			document.setCampData(vell.getCampData());
			return document;
		}
		Document nou = new Document();
		if (definicioProcesId != null)
			nou.setDefinicioProces(dissenyService.getById(definicioProcesId));
		if (definicioProces != null)
			nou.setDefinicioProces(dissenyService.getById(definicioProces));
		return nou;
	}

	@RequestMapping(value = "/definicioProces/documentLlistat", method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
				model.addAttribute("documents", dissenyService.findDocumentsAmbDefinicioProces(definicioProces.getId()));
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
		return "definicioProces/documentLlistat";
	}

	@RequestMapping(value = "/definicioProces/documentForm", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
				return "definicioProces/documentForm";
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/documentForm", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "definicioProces", required = true) Long definicioProcesId,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "arxiuContingut", required = false) final MultipartFile multipartFile,
			@RequestParam(value = "arxiuContingut_deleted", required = false) final String deleted,
			@ModelAttribute("command") Document command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
				if ("submit".equals(submit) || submit.length() == 0) {
					command.setArxiuNom(null);
					command.setArxiuContingut(null);
					if (multipartFile != null && multipartFile.getSize() > 0) {
						try {
							command.setArxiuContingut(multipartFile.getBytes());
							command.setArxiuNom(multipartFile.getOriginalFilename());
						} catch (Exception ignored) {}
					}
					annotationValidator.validate(command, result);
			        if (result.hasErrors()) {
			        	return "definicioProces/documentForm";
			        }
			        try {
			        	if (command.getId() == null)
			        		dissenyService.createDocument(command);
			        	else
			        		dissenyService.updateDocument(command, "deleted".equalsIgnoreCase(deleted));
			        	missatgeInfo(request, "El documents s'ha guardat correctament");
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        	return "definicioProces/documentForm";
			        }
			        return "redirect:/definicioProces/documentLlistat.html?definicioProcesId=" + definicioProcesId;
				}
				return "redirect:/definicioProces/documentLlistat.html?definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/definicioProces/documentDelete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "id", required = true) Long id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				try {
					dissenyService.deleteDocument(id);
					missatgeInfo(request, "El document s'ha esborrat correctament");
				} catch (Exception ex) {
		        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el registre", ex);
		        }
				return "redirect:/definicioProces/documentLlistat.html?definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/definicioProces/documentDownload")
	public String downloadAction(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "id", required = true) Long id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				try {
					Document document = dissenyService.getDocumentById(id);
					model.addAttribute(
							ArxiuView.MODEL_ATTRIBUTE_FILENAME,
							document.getArxiuNom());
					model.addAttribute(
							ArxiuView.MODEL_ATTRIBUTE_DATA,
							document.getArxiuContingut());
					return "arxiuView";
				} catch (Exception ignored) {
					return "redirect:/definicioProces/documentLlistat.html?definicioProcesId=" + definicioProcesId;
				}
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				byte[].class,
				new ByteArrayMultipartFileEditor());
		binder.registerCustomEditor(
				DefinicioProces.class,
				new DefinicioProcesTypeEditor(dissenyService));
		binder.registerCustomEditor(
				Camp.class,
				new CampTypeEditor(dissenyService));
	}



	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	private boolean potDissenyarDefinicioProces(Entorn entorn, DefinicioProcesDto definicioProces) {
		if (potDissenyarEntorn(entorn))
			return true;
		if (definicioProces.getExpedientTipus() != null) {
			return permissionService.filterAllowed(
					definicioProces.getExpedientTipus(),
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.DESIGN}) != null;
		}
		return false;
	}
	private boolean potDissenyarEntorn(Entorn entorn) {
		return permissionService.filterAllowed(
				entorn,
				Entorn.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}

	private static final Log logger = LogFactory.getLog(DefinicioProcesDocumentController.class);

}
