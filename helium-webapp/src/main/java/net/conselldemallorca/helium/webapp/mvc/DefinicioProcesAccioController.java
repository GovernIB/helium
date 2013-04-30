/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
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



/**
 * Controlador per la gestió de les accions d'una definició de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class DefinicioProcesAccioController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;
	private Validator annotationValidator;



	@Autowired
	public DefinicioProcesAccioController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("accionsJbpm")
	public List<String> populateAccionsJbpm(
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "definicioProces", required = false) Long definicioProces) {
		if (definicioProcesId != null)
			return dissenyService.findAccionsJbpmOrdenades(definicioProcesId);
		if (definicioProces != null)
			return dissenyService.findAccionsJbpmOrdenades(definicioProces);
		return null;
	}

	@ModelAttribute("command")
	public Accio populateCommand(
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "definicioProces", required = false) Long definicioProces) {
		if (id != null)
			return dissenyService.getAccioById(id);
		Accio nova = new Accio();
		if (definicioProcesId != null)
			nova.setDefinicioProces(dissenyService.getById(definicioProcesId, false));
		if (definicioProces != null)
			nova.setDefinicioProces(dissenyService.getById(definicioProces, false));
		return nova;
	}

	@RequestMapping(value = "/definicioProces/accioLlistat", method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
				model.addAttribute("accions", dissenyService.findAccionsAmbDefinicioProces(definicioProces.getId()));
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
		return "definicioProces/accioLlistat";
	}

	@RequestMapping(value = "/definicioProces/accioForm", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
				return "definicioProces/accioForm";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/accioForm", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "definicioProces", required = true) Long definicioProcesId,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") Accio command,
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
			        	return "definicioProces/accioForm";
			        }
			        try {
			        	if (command.getId() == null)
			        		dissenyService.createAccio(command);
			        	else
			        		dissenyService.updateAccio(command);
			        	missatgeInfo(request, getMessage("info.accio.guardat") );
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar l'acció", ex);
			        	return "definicioProces/accioForm";
			        }
				}
				return "redirect:/definicioProces/accioLlistat.html?definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/definicioProces/accioDelete")
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
					dissenyService.deleteAccio(id);
					missatgeInfo(request, getMessage("info.accio.guardat") );
				} catch (Exception ex) {
		        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el registre", ex);
		        }
				return "redirect:/definicioProces/accioLlistat.html?definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc") );
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
				DefinicioProces.class,
				new DefinicioProcesTypeEditor(dissenyService));
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

	private static final Log logger = LogFactory.getLog(DefinicioProcesAccioController.class);

}
