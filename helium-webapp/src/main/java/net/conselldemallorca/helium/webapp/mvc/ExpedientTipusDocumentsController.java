/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
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
 * Controlador per la gestió dels documents d'un tipus d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientTipusDocumentsController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;
	private Validator annotationValidator;



	@Autowired
	public ExpedientTipusDocumentsController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
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
            document.setAdjuntarAuto(vell.isAdjuntarAuto());
            document.setGenerarNomesTasca(vell.isGenerarNomesTasca());
            document.setContentType(vell.getContentType());
            document.setCustodiaCodi(vell.getCustodiaCodi());
            document.setTipusDocPortasignatures(vell.getTipusDocPortasignatures());
            document.setDefinicioProces(vell.getDefinicioProces());
            document.setCampData(vell.getCampData());
			document.setExtensionsPermeses(vell.getExtensionsPermeses());
			document.setConvertirExtensio(vell.getConvertirExtensio());
			return document;
        }
        Document nou = new Document();
        nou.setAdjuntarAuto(true);
        if (definicioProcesId != null)
nou.setDefinicioProces(dissenyService.getById(definicioProcesId, false));
        if (definicioProces != null)
nou.setDefinicioProces(dissenyService.getById(definicioProces, false));
        return nou;
    } 
	
	
	
	

	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "expedientTipusId", required = false) Long id) {
		if (id != null) {
			return dissenyService.getExpedientTipusById(id);
		}
		return null;
	}

	@RequestMapping(value = "/expedientTipus/documentLlistat")
	public String llistat(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.addAttribute(
					"definicionsProces",
					dissenyService.findDarreresAmbExpedientTipusEntorn(entorn.getId(), expedientTipusId, false));
			if (definicioProcesId != null)
				model.addAttribute(
						"llistat",
						dissenyService.findDocumentsAmbDefinicioProcesOrdenatsPerCodi(definicioProcesId));
			return "expedientTipus/documentLlistat";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/documentForm", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potGestionarExpedientTipus(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
				return "expedientTipus/documentForm";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/documentForm", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
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
			if (potGestionarExpedientTipus(entorn, definicioProces)) {
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
			        	return "expedientTipus/documentForm";
			        }
			        try {
			        	if (command.getId() == null)
			        		dissenyService.createDocument(command);
			        	else
			        		dissenyService.updateDocument(command, "deleted".equalsIgnoreCase(deleted));
			        	missatgeInfo(request, "El documents s'ha guardat correctament");
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        	return "expedientTipus/documentForm";
			        }
			        return "redirect:/expedientTipus/documentLlistat.html?expedientTipusId=" + expedientTipusId + "&definicioProcesId=" + definicioProcesId;
				}
				return "redirect:/expedientTipus/documentLlistat.html?expedientTipusId=" + expedientTipusId + "&definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/documentDownload")
	public String downloadAction(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "id", required = true) Long id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potGestionarExpedientTipus(entorn, definicioProces)) {
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
					return "redirect:/expedientTipus/documentLlistat.html?expedientTipusId=" + expedientTipusId + "&definicioProcesId=" + definicioProcesId;
				}
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
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



	private boolean potGestionarExpedientTipus(Entorn entorn, DefinicioProcesDto definicioProces) {
		if (definicioProces.getExpedientTipus() != null) {
			return permissionService.filterAllowed(
					definicioProces.getExpedientTipus(),
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.DESIGN,
						ExtendedPermission.MANAGE}) != null;
		}
		return false;
	}

	private static final Log logger = LogFactory.getLog(ExpedientTipusDocumentsController.class);

}
