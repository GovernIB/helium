/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
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
 * @author Limit Tecnologies <limit@limit.es>
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
						expedientService.getArbreInstanciesProces(id));
//				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(id, true);
//				model.addAttribute(
//						"instanciaProces",
//						instanciaProces);
				return "expedient/eines";
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
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
					missatgeInfo(request, getMessage("info.script.executat"));
				} catch (Exception ex) {
					Long entornId = entorn.getId();
					String numeroExpedient = id;
					logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+numeroExpedient+" No s'ha pogut executar l'script", ex);
					missatgeError(request, getMessage("error.executar.script"), getMissageFinalCadenaExcepcions(ex));
		        	return "expedient/eines";
				}
				return "redirect:/expedient/eines.html?id=" + id;
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
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
								command.getMotiu(),
								null);
						missatgeInfo(request, getMessage("info.expedient.aturat") );
					} catch (Exception ex) {
						Long entornId = entorn.getId();
						String numeroExpedient = id;
						logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+numeroExpedient+" No s'ha pogut aturar l'expedient", ex);	
						missatgeError(request, getMessage("error.aturar.expedient"), ex.getLocalizedMessage());
			        	return "expedient/eines";
					}
				} else {
					missatgeError(request, getMessage("error.expedient.ja.aturat") );
				}
				return "redirect:/expedient/eines.html?id=" + id;
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
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
						expedientService.reprendre(
								id,
								null);
						missatgeInfo(request, getMessage("info.expedient.repres") );
					} catch (Exception ex) {
						Long entornId = entorn.getId();
						String numeroExpedient = id;
						logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+numeroExpedient+" No s'ha pogut reprendre l'expedient", ex);	
						missatgeError(request, getMessage("error.reprendre.expedient"), ex.getLocalizedMessage());
			        	return "expedient/eines";
					}
				} else {
					missatgeError(request, getMessage("error.expedient.no.aturat") );
				}
				return "redirect:/expedient/eines.html?id=" + id;
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
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
						missatgeInfo(request, getMessage("info.canvi.versio.realitzat") );
					} else {
						expedientService.changeProcessInstanceVersion(instanciaProcesId);
						missatgeError(request, getMessage("error.especificar.versio.proces") );
					}
				} catch (Exception ex) {
					Long entornId = entorn.getId();
					String numeroExpedient = expedient.getIdentificador();
					logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+numeroExpedient+" No s'ha pogut canviar la versió de procés", ex);
					missatgeError(request, getMessage("error.canviar.versio.proces"), ex.getLocalizedMessage());
		        	return "expedient/eines";
				}
				return "redirect:/expedient/eines.html?id=" + instanciaProcesId;
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	@RequestMapping(value = "/expedient/reindexa", method = RequestMethod.POST)
	public String reindexa(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String instanciaProcesId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(instanciaProcesId);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.luceneReindexarExpedient(instanciaProcesId);
					missatgeInfo(request, getMessage("info.expedient.reindexat"));
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.reindexar.expedient"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut reindexar l'expedient", ex);
				}
				return "expedient/eines";
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}



	private class ExpedientScriptValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEinesScriptCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "script", "not.blank");
		}
	}
	private class ExpedientAturarValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEinesAturarCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "motiu", "not.blank");
		}
	}

	private String getMissageFinalCadenaExcepcions(Throwable ex) {
		if (ex.getCause() == null) {
			return ex.getClass().getName() + ": " + ex.getMessage();
		} else {
			return getMissageFinalCadenaExcepcions(ex.getCause());
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
