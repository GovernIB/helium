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
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.hibernate.Tasca;
import net.conselldemallorca.helium.model.hibernate.Tasca.TipusTasca;
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



/**
 * Controlador per la gestió de les tasques d'una definició de procés
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class DefinicioProcesTascaController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;
	private Validator annotationValidator;



	@Autowired
	public DefinicioProcesTascaController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("command")
	public Tasca populateCommand(
			@RequestParam(value = "id", required = false) Long id) {
		if (id != null)
			return dissenyService.getTascaById(id);
		return null;
	}
	@ModelAttribute("tipusTasca")
	public TipusTasca[] populateTipusTasca() {
		return Tasca.TipusTasca.values();

	}
	@ModelAttribute("campsMultiples")
	public List<Camp> populateCampsMultiples(
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		return dissenyService.findCampAmbDefinicioProcesIMultiple(definicioProcesId);
	}

	@RequestMapping(value = "/definicioProces/tascaLlistat", method = RequestMethod.GET)
	public String tasques(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
				model.addAttribute("tasques", dissenyService.findTasquesAmbDefinicioProces(definicioProces.getId()));
				return "definicioProces/tascaLlistat";
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/definicioProces/tascaForm", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
		return "definicioProces/tascaForm";
	}
	@RequestMapping(value = "/definicioProces/tascaForm", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "definicioProces", required = true) Long definicioProcesId,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") Tasca command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
				if ("submit".equals(submit) || submit.length() == 0) {
					annotationValidator.validate(command, result);
			        if (result.hasErrors()) {
			        	return "definicioProces/tascaForm";
			        }
			        try {
		        		dissenyService.updateTasca(command);
			        	missatgeInfo(request, "El camp s'ha guardat correctament");
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        	return "definicioProces/tascaForm";
			        }
			        return "redirect:/definicioProces/tascaLlistat.html?definicioProcesId=" + definicioProcesId;
				}
				return "redirect:/definicioProces/tascaLlistat.html?definicioProcesId=" + definicioProcesId;
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

	private static final Log logger = LogFactory.getLog(DefinicioProcesCampController.class);

}
