/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.ExpedientService;
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
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per a l'execució d'scripts dins un expedient
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class ExpedientEinesController extends BaseController {

	private ExpedientService expedientService;
	private PermissionService permissionService;
	private DissenyService dissenyService;



	@Autowired
	public ExpedientEinesController(
			ExpedientService expedientService,
			PermissionService permissionService,
			DissenyService dissenyService) {
		this.expedientService = expedientService;
		this.permissionService = permissionService;
		this.dissenyService = dissenyService;
	}

	@ModelAttribute("scriptCommand")
	public ExpedientEinesScriptCommand populateScriptCommand() {
		return new ExpedientEinesScriptCommand();
	}
	@ModelAttribute("aturarCommand")
	public ExpedientEinesAturarCommand populateAturarCommand() {
		return new ExpedientEinesAturarCommand();
	}
	@ModelAttribute("canviVersioProcesCommand")
	public CanviVersioProcesCommand populateCanviVersioProcesCommand() {
		return new CanviVersioProcesCommand();
	}
	@ModelAttribute("definicioProces")
	public DefinicioProcesDto populateDefinicioProces(
			@RequestParam(value = "id", required = true) String id) {
		return dissenyService.findDefinicioProcesAmbProcessInstanceId(id);
	}

	@RequestMapping(value = "/expedient/eines", method = RequestMethod.GET)
	public String eines(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				model.addAttribute(
						"expedient",
						expedient);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(id, false));
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(id, true);
				model.addAttribute(
						"instanciaProces",
						instanciaProces);
				return "expedient/eines";
			} else {
				missatgeError(request, "No té permisos per modificar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	
	@RequestMapping(value = "/expedient/script", method = RequestMethod.POST)
	public String script(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@ModelAttribute("scriptCommand") ExpedientEinesScriptCommand command,
			BindingResult result,
			SessionStatus status) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				new ExpedientScriptValidator().validate(command, result);
				if (result.hasErrors()) {
		        	return "expedient/eines";
		        }
				try {
					expedientService.evaluateScript(
							id,
							command.getScript(),
							null);
					missatgeInfo(request, "L'script s'ha executat correctament");
				} catch (Exception ex) {
					missatgeError(request, "No s'ha pogut executar l'script", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut executar l'script", ex);
		        	return "expedient/eines";
				}
				return "redirect:/expedient/eines.html?id=" + id;
			} else {
				missatgeError(request, "No té permisos per modificar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/aturar", method = RequestMethod.POST)
	public String aturar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@ModelAttribute("aturarCommand") ExpedientEinesAturarCommand command,
			BindingResult result,
			SessionStatus status) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				if (!expedient.isAturat()) {
					new ExpedientAturarValidator().validate(command, result);
					if (result.hasErrors()) {
			        	return "expedient/eines";
			        }
					try {
						expedientService.aturar(
								id,
								command.getMotiu());
						missatgeInfo(request, "L'expedient s'ha aturat correctament");
					} catch (Exception ex) {
						missatgeError(request, "No s'ha pogut aturar l'expedient", ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut aturar l'expedient", ex);
			        	return "expedient/eines";
					}
				} else {
					missatgeError(request, "Aquest expedient ja està aturat");
				}
				return "redirect:/expedient/eines.html?id=" + id;
			} else {
				missatgeError(request, "No té permisos per modificar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/reprendre", method = RequestMethod.POST)
	public String reprendre(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				if (expedient.isAturat()) {
					try {
						expedientService.reprendre(id);
						missatgeInfo(request, "L'expedient s'ha représ correctament");
					} catch (Exception ex) {
						missatgeError(request, "No s'ha pogut reprendre l'expedient", ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut reprendre l'expedient", ex);
			        	return "expedient/eines";
					}
				} else {
					missatgeError(request, "Aquest expedient no està aturat");
				}
				return "redirect:/expedient/eines.html?id=" + id;
			} else {
				missatgeError(request, "No té permisos per consultar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/canviVersio", method = RequestMethod.POST)
	public String canviVersio(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String instanciaProcesId,
			@ModelAttribute("canviVersioProcesCommand") CanviVersioProcesCommand command,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(instanciaProcesId);
			if (potModificarExpedient(expedient)) {
				try {
					if (command.getDefinicioProcesId() != null) {
						DefinicioProcesDto definicioProces = dissenyService.getById(command.getDefinicioProcesId(), false);
						expedientService.changeProcessInstanceVersion(instanciaProcesId, definicioProces.getVersio());
						missatgeInfo(request, "El canvi de versió s'ha realitzat correctament");
					} else {
						expedientService.changeProcessInstanceVersion(instanciaProcesId);
						missatgeError(request, "No s'ha especificat cap versió de procés");
					}
				} catch (Exception ex) {
					missatgeError(request, "No s'ha pogut canviar la versió de procés", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut canviar la versió de procés", ex);
		        	return "expedient/eines";
				}
				return "redirect:/expedient/eines.html?id=" + instanciaProcesId;
			} else {
				missatgeError(request, "No té permisos per modificar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}



	private class ExpedientScriptValidator implements Validator {
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEinesScriptCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "script", "not.blank");
		}
	}
	private class ExpedientAturarValidator implements Validator {
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEinesAturarCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "motiu", "not.blank");
		}
	}


	private boolean potModificarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE}) != null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientEinesController.class);

}
