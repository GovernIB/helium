/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.model.hibernate.Domini;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Enumeracio;
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
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió dels camps d'una definició de procés
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class DefinicioProcesCampController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;
	private Validator annotationValidator;
	private Validator additionalValidator;



	@Autowired
	public DefinicioProcesCampController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
		this.additionalValidator = new CampValidator();
	}

	@ModelAttribute("tipusCamp")
	public TipusCamp[] populateTipusCamp() {
		return Camp.TipusCamp.values();
	}
	@ModelAttribute("dominis")
	public List<Domini> populateDominis(HttpServletRequest request) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			return dissenyService.findDominiAmbEntorn(entorn.getId());
		}
		return null;

	}
	@ModelAttribute("enumeracions")
	public List<Enumeracio> populateEnumeracions(HttpServletRequest request) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null)
			return dissenyService.findEnumeracionsAmbEntorn(entorn.getId());
		return null;
	}
	@ModelAttribute("agrupacions")
	public List<CampAgrupacio> populateAgrupacions(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "definicioProces", required = false) Long definicioProces) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Long id = (definicioProcesId != null) ? definicioProcesId : definicioProces;
			return dissenyService.findCampAgrupacioAmbDefinicioProces(id);
		}
		return null;
	}

	@ModelAttribute("command")
	public Camp populateCommand(
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "definicioProces", required = false) Long definicioProces) {
		if (id != null) {
			return dissenyService.getCampById(id);
		}
		Camp nou = new Camp();
		if (definicioProcesId != null)
			nou.setDefinicioProces(dissenyService.getById(definicioProcesId));
		if (definicioProces != null)
			nou.setDefinicioProces(dissenyService.getById(definicioProces));
		return nou;
	}

	@RequestMapping(value = "/definicioProces/campLlistat", method = RequestMethod.GET)
	public String campLlistat(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
				model.addAttribute("camps", dissenyService.findCampsAmbDefinicioProces(definicioProces.getId()));
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
		return "definicioProces/campLlistat";
	}

	@RequestMapping(value = "/definicioProces/campForm", method = RequestMethod.GET)
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
		return "definicioProces/campForm";
	}
	@RequestMapping(value = "/definicioProces/campForm", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "definicioProces", required = true) Long definicioProcesId,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") Camp command,
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
					additionalValidator.validate(command, result);
			        if (result.hasErrors()) {
			        	return "definicioProces/campForm";
			        }
			        try {
			        	if (command.getId() == null)
			        		dissenyService.createCamp(command);
			        	else
			        		dissenyService.updateCamp(command);
			        	missatgeInfo(request, "El camp s'ha guardat correctament");
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        	return "definicioProces/campForm";
			        }
			        return "redirect:/definicioProces/campLlistat.html?definicioProcesId=" + definicioProcesId;
				}
				return "redirect:/definicioProces/campLlistat.html?definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/definicioProces/campDelete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				try {
					dissenyService.deleteCamp(id);
					missatgeInfo(request, "El camp s'ha esborrat correctament");
				} catch (Exception ex) {
		        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el registre", ex);
		        }
				return "redirect:/definicioProces/campLlistat.html?definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	/*@RequestMapping(value = "/definicioProces/consultaCamp")
	public String consultaCamp(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "codi", required = true) String codi,
			@RequestParam(value = "q", required = false) String textInicial,
			@RequestParam(value = "tipus", required = true) String tipus,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				try {
					model.addAttribute(
							"camp",
							dissenyService.findCampAmbDefinicioProcesICodi(definicioProcesId, codi));
					model.addAttribute(
							"resultat",
							dissenyService.consultaPerCamp(
									definicioProcesId,
									codi,
									textInicial));
				} catch (Exception ex) {
		        	logger.error("No s'ha pogut executar la consulta pel camp '" + codi + "'", ex);
		        }
				if (tipus.equals("select"))
					return "definicioProces/consultaCampSelect";
				else
					return "definicioProces/consultaCampSuggest";
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}*/

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				DefinicioProces.class,
				new DefinicioProcesTypeEditor(dissenyService));
		binder.registerCustomEditor(
				Domini.class,
				new DominiTypeEditor(dissenyService));
		binder.registerCustomEditor(
				Enumeracio.class,
				new EnumeracioTypeEditor(dissenyService));
		binder.registerCustomEditor(
				CampAgrupacio.class,
				new CampAgrupacioTypeEditor(dissenyService));
	}



	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	private class CampValidator implements Validator {
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Camp.class);
		}
		public void validate(Object target, Errors errors) {
			Camp camp = (Camp)target;
			if (camp.getCodi().contains(".")) {
				errors.rejectValue("codi", "error.camp.codi.char.nok");
			}
		}
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
